package com.visitraitmso.app

import androidx.compose.runtime.mutableStateListOf

object AppStorage {
    val students  = mutableStateListOf<Student>()
    val faculties = mutableStateListOf<Faculty>()
    val visits    = mutableStateListOf<Visit>()
    val loginLogs = mutableStateListOf<LoginLog>()
}

// ─────────────────────────────────────────────
//  MODELS
// ─────────────────────────────────────────────

data class Student(
    val name: String,
    val studentId: String,
    val type: String,
    val course: String?    = null,
    val yearLevel: String? = null
)

data class Faculty(
    val name: String,
    val employeeId: String,
    val department: String
)

data class Visit(
    val studentId: String,       // studentId, employeeId, or "EXT" for walk-ins
    val purpose: String,
    val timeIn: Long,
    val timeOut: Long?     = null,
    val visitorType: String = "Student",  // "Student", "Faculty", "External"
    val visitorName: String = ""          // used for Faculty and External visitors
)

data class LoginLog(
    val username: String,
    val time: Long
)