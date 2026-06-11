package com.example.visitraitmso

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.visitraitmso.app.AdminUser
import com.visitraitmso.app.Faculty
import com.visitraitmso.app.LoginLog
import com.visitraitmso.app.Student
import com.visitraitmso.app.User
import com.visitraitmso.app.Visit

object AppStorage {
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    val students  = mutableStateListOf<Student>()
    val faculties = mutableStateListOf<Faculty>()
    val visits    = mutableStateListOf<Visit>()
    val loginLogs = mutableStateListOf<LoginLog>()
    val users     = mutableStateListOf<User>()

    fun initFirestore() {
        db.collection("students")
            .addSnapshotListener(EventListener<QuerySnapshot> { snap, err ->
                if (err != null) return@EventListener
                snap?.let {
                    students.clear()
                    students.addAll(it.toObjects(Student::class.java))
                }
            })

        db.collection("faculties")
            .addSnapshotListener(EventListener<QuerySnapshot> { snap, err ->
                if (err != null) return@EventListener
                snap?.let {
                    faculties.clear()
                    faculties.addAll(it.toObjects(Faculty::class.java))
                }
            })

        db.collection("visits")
            .addSnapshotListener(EventListener<QuerySnapshot> { snap, err ->
                if (err != null) return@EventListener
                snap?.let {
                    visits.clear()
                    visits.addAll(it.toObjects(Visit::class.java))
                }
            })

        db.collection("users")
            .addSnapshotListener(EventListener<QuerySnapshot> { snap, err ->
                if (err != null) return@EventListener
                snap?.let {
                    users.clear()
                    users.addAll(it.toObjects(User::class.java))
                }
            })
    }

    fun saveStudent(student: Student) {
        db.collection("students")
            .document(student.studentId)
            .set(student)
    }

    fun saveFaculty(faculty: Faculty) {
        db.collection("faculties")
            .document(faculty.employeeId)
            .set(faculty)
    }

    fun saveVisit(visit: Visit) {
        val docId = when {
            visit.id.isNotEmpty()              -> visit.id
            visit.studentId.startsWith("EXT-") -> visit.studentId
            else                               -> "${visit.studentId}_${visit.timeIn}"
        }
        db.collection("visits").document(docId).set(visit)
    }

    fun deleteVisit(visit: Visit) {
        val docId = when {
            visit.id.isNotEmpty()              -> visit.id
            visit.studentId.startsWith("EXT-") -> visit.studentId
            else                               -> "${visit.studentId}_${visit.timeIn}"
        }
        db.collection("visits").document(docId).delete()
    }

    fun getUser(uid: String, onResult: (User?) -> Unit) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                onResult(doc.toObject(User::class.java))
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun saveUser(user: User) {
        db.collection("users").document(user.uid).set(user)
    }

    fun getAdminUser(uid: String, onResult: (AdminUser?) -> Unit) {
        db.collection("admins").document(uid).get()
            .addOnSuccessListener { doc ->
                onResult(doc.toObject(AdminUser::class.java))
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun saveLoginLog(log: LoginLog) {
        db.collection("loginLogs").add(log)
    }
}