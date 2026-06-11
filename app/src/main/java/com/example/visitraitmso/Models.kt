package com.visitraitmso.app

import com.google.firebase.firestore.DocumentId

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "Staff",
    val isAvailable: Boolean = true
)

data class AdminUser(
    val uid: String = "",
    val email: String = "",
    val role: String = "Staff"
)

data class Student(
    val name: String = "",
    val studentId: String = "",
    val type: String = "",
    val course: String? = null,
    val yearLevel: String? = null
)

data class Faculty(
    val name: String = "",
    val employeeId: String = "",
    val department: String = ""
)

data class Visitor(
    val name: String = "",
    val visitorId: String = "",
    val organization: String = "",
    val contact: String = ""
)

data class Visit(
    @DocumentId
    val id: String = "",
    val studentId: String = "",
    val purpose: String = "",
    val timeIn: Long = 0L,
    val timeOut: Long? = null,
    val visitorType: String = "Student",
    val visitorName: String = "",
    val visitorOrg: String = "",
    val visitorContact: String = "",
    val queueNumber: Int = 0,
    val assignedStaffUid: String = "",
    val assignedStaffName: String = "",
    val serving: Boolean = false
)

data class LoginLog(
    val username: String = "",
    val time: Long = 0L
)