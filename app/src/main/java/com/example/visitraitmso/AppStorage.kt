package com.visitraitmso.app

import androidx.compose.runtime.mutableStateListOf

object AppStorage {

    val students = mutableStateListOf<Student>()

    val visits = mutableStateListOf<Visit>()

    val loginLogs = mutableStateListOf<LoginLog>()
}

// ================= MODELS =================

data class Student(
    val name: String,
    val studentId: String,
    val type: String,
    val course: String? = null,
    val yearLevel: String? = null
)

data class Visit(
    val studentId: String,
    val purpose: String,
    val timeIn: Long,
    val timeOut: Long? = null
)

data class LoginLog(
    val username: String,
    val time: Long
)