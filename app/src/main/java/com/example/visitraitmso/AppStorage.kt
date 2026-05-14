package com.visitraitmso.app

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.FirebaseFirestoreException

object AppStorage {
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    val students  = mutableStateListOf<Student>()
    val faculties = mutableStateListOf<Faculty>()
    val visits    = mutableStateListOf<Visit>()
    val loginLogs = mutableStateListOf<LoginLog>()
    val users     = mutableStateListOf<User>()

    fun initFirestore() {
        // Listen for Students
        db.collection("students").addSnapshotListener(EventListener<QuerySnapshot> { snapshot, error ->
            if (error != null) return@EventListener
            snapshot?.let {
                val list = it.toObjects(Student::class.java)
                students.clear()
                students.addAll(list)
            }
        })

        // Listen for Faculty
        db.collection("faculties").addSnapshotListener(EventListener<QuerySnapshot> { snapshot, error ->
            if (error != null) return@EventListener
            snapshot?.let {
                val list = it.toObjects(Faculty::class.java)
                faculties.clear()
                faculties.addAll(list)
            }
        })

        // Listen for Visits
        db.collection("visits").addSnapshotListener(EventListener<QuerySnapshot> { snapshot, error ->
            if (error != null) return@EventListener
            snapshot?.let {
                val list = it.toObjects(Visit::class.java)
                visits.clear()
                visits.addAll(list)
            }
        })

        // Listen for Users
        db.collection("users").addSnapshotListener(EventListener<QuerySnapshot> { snapshot, error ->
            if (error != null) return@EventListener
            snapshot?.let {
                val list = it.toObjects(User::class.java)
                users.clear()
                users.addAll(list)
            }
        })
    }

    fun saveStudent(student: Student) {
        db.collection("students").document(student.studentId).set(student)
    }

    fun saveFaculty(faculty: Faculty) {
        db.collection("faculties").document(faculty.employeeId).set(faculty)
    }

    fun saveVisit(visit: Visit) {
        val docId = if (visit.studentId.startsWith("EXT-")) visit.studentId else "${visit.studentId}_${visit.timeIn}"
        db.collection("visits").document(docId).set(visit)
    }

    fun deleteVisit(visit: Visit) {
        val docId = if (visit.studentId.startsWith("EXT-")) visit.studentId else "${visit.studentId}_${visit.timeIn}"
        db.collection("visits").document(docId).delete()
    }

    fun getUser(uid: String, onResult: (User?) -> Unit) {
        db.collection("users").document(uid).get().addOnSuccessListener { doc ->
            onResult(doc.toObject(User::class.java))
        }.addOnFailureListener {
            onResult(null)
        }
    }

    fun saveUser(user: User) {
        db.collection("users").document(user.uid).set(user)
    }

    fun getAdminUser(uid: String, onResult: (AdminUser?) -> Unit) {
        db.collection("admins").document(uid).get().addOnSuccessListener { doc ->
            onResult(doc.toObject(AdminUser::class.java))
        }.addOnFailureListener {
            onResult(null)
        }
    }
}

// ─────────────────────────────────────────────
//  MODELS
// ─────────────────────────────────────────────

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "User" // "Super Admin", "Admin", "Staff", "User"
)

data class AdminUser(
    val uid: String = "",
    val email: String = "",
    val role: String = "Staff" // "Super Admin" or "Staff"
)

data class Student(
    val name: String = "",
    val studentId: String = "",
    val type: String = "",
    val course: String?    = null,
    val yearLevel: String? = null
)

data class Faculty(
    val name: String = "",
    val employeeId: String = "",
    val department: String = ""
)

data class Visit(
    val studentId: String = "",
    val purpose: String = "",
    val timeIn: Long = 0L,
    val timeOut: Long?     = null,
    val visitorType: String = "Student",
    val visitorName: String = ""
)

data class LoginLog(
    val username: String = "",
    val time: Long = 0L
)
