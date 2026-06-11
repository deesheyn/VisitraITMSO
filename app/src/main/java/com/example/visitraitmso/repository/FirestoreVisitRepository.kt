package com.visitraitmso.app

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.TimeZone

class FirestoreVisitRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AppRepository {

    // ── Students ─────────────────────────────
    override fun getStudents(): Flow<List<Student>> = callbackFlow {
        val sub = db.collection("students")
            .addSnapshotListener { snap, _ ->
                snap?.let { trySend(it.toObjects(Student::class.java)) }
            }
        awaitClose { sub.remove() }
    }

    override fun getFaculties(): Flow<List<Faculty>> = callbackFlow {
        val sub = db.collection("faculties")
            .addSnapshotListener { snap, _ ->
                snap?.let { trySend(it.toObjects(Faculty::class.java)) }
            }
        awaitClose { sub.remove() }
    }

    override suspend fun saveStudent(student: Student) {
        db.collection("students").document(student.studentId).set(student).await()
    }

    override suspend fun saveFaculty(faculty: Faculty) {
        db.collection("faculties").document(faculty.employeeId).set(faculty).await()
    }

    // ── Visitors ─────────────────────────────
    override fun getVisitors(): Flow<List<Visitor>> = callbackFlow {
        val sub = db.collection("visitors")
            .addSnapshotListener { snap, _ ->
                snap?.let { trySend(it.toObjects(Visitor::class.java)) }
            }
        awaitClose { sub.remove() }
    }

    override suspend fun saveVisitor(visitor: Visitor) {
        db.collection("visitors").document(visitor.visitorId).set(visitor).await()
    }

    // ── Visits ───────────────────────────────
    override fun getVisits(): Flow<List<Visit>> = callbackFlow {
        val sub = db.collection("visits")
            .orderBy("timeIn", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, _ ->
                snap?.let { snapshot ->
                    val visits = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Visit::class.java)?.copy(id = doc.id)
                    }
                    trySend(visits)
                }
            }
        awaitClose { sub.remove() }
    }

    override suspend fun saveVisit(visit: Visit) {
        val docId = when {
            visit.id.isNotEmpty()              -> visit.id
            visit.studentId.startsWith("EXT-") -> visit.studentId
            else                               -> "${visit.studentId}_${visit.timeIn}"
        }
        db.collection("visits").document(docId).set(visit).await()
    }

    override suspend fun deleteVisit(visit: Visit) {
        val docId = when {
            visit.id.isNotEmpty()              -> visit.id
            visit.studentId.startsWith("EXT-") -> visit.studentId
            else                               -> "${visit.studentId}_${visit.timeIn}"
        }
        db.collection("visits").document(docId).delete().await()
    }

    // ── Users ────────────────────────────────

    // Always copy doc.id into uid so uid is never blank
    override fun getUsers(): Flow<List<User>> = callbackFlow {
        val sub = db.collection("users")
            .addSnapshotListener { snap, _ ->
                snap?.let { snapshot ->
                    val users = snapshot.documents.mapNotNull { doc ->
                        User(
                            uid         = doc.id,
                            name        = doc.getString("name")        ?: "",
                            email       = doc.getString("email")       ?: "",
                            role        = doc.getString("role")        ?: "Staff",
                            isAvailable = doc.getBoolean("isAvailable") ?: true
                        )
                    }
                    trySend(users)
                }
            }
        awaitClose { sub.remove() }
    }

    override fun getStaffs(): Flow<List<User>> = callbackFlow {
        val sub = db.collection("users")
            .whereIn("role", listOf("Staff", "Admin"))
            .addSnapshotListener { snap, _ ->
                snap?.let { snapshot ->
                    val users = snapshot.documents.mapNotNull { doc ->
                        User(
                            uid         = doc.id,
                            name        = doc.getString("name")        ?: "",
                            email       = doc.getString("email")       ?: "",
                            role        = doc.getString("role")        ?: "Staff",
                            isAvailable = doc.getBoolean("isAvailable") ?: true
                        )
                    }
                    trySend(users)
                }
            }
        awaitClose { sub.remove() }
    }

    override suspend fun saveUser(user: User) {
        db.collection("users").document(user.uid)
            .set(
                mapOf(
                    "uid"         to user.uid,
                    "name"        to user.name,
                    "email"       to user.email,
                    "role"        to user.role,
                    "isAvailable" to user.isAvailable
                ),
                SetOptions.merge()
            )
            .await()
    }

    override suspend fun getUser(uid: String): User? {
        val doc = db.collection("users").document(uid).get().await()
        if (!doc.exists()) return null
        return User(
            uid         = doc.id,
            name        = doc.getString("name")        ?: "",
            email       = doc.getString("email")       ?: "",
            role        = doc.getString("role")        ?: "Staff",
            isAvailable = doc.getBoolean("isAvailable") ?: true
        )
    }

    override suspend fun getAdminUser(uid: String): AdminUser? {
        return db.collection("admins").document(uid).get().await()
            .toObject(AdminUser::class.java)
    }

    override suspend fun deleteUser(uid: String) {
        db.collection("users").document(uid).delete().await()
    }

    override suspend fun getTodayMaxQueue(): Int {
        val tz  = TimeZone.getTimeZone("Asia/Manila")
        val cal = Calendar.getInstance(tz).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val start = cal.timeInMillis

        val snap = db.collection("visits")
            .whereGreaterThanOrEqualTo("timeIn", start)
            .orderBy("timeIn")
            .get()
            .await()

        val visits = snap.documents.mapNotNull { doc ->
            doc.toObject(Visit::class.java)?.copy(id = doc.id)
        }
        return visits.maxOfOrNull { it.queueNumber } ?: 0
    }

    // Use two separate update calls — one for isAvailable, one to delete stale field
    override suspend fun setStaffAvailability(uid: String, isAvailable: Boolean) {
        val ref = db.collection("users").document(uid)
        // Write isAvailable
        ref.update("isAvailable", isAvailable).await()
        // Delete the stale "available" field if it exists
        try { ref.update("available", FieldValue.delete()).await() } catch (_: Exception) {}
    }
}