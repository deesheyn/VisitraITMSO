package com.visitraitmso.app

import kotlinx.coroutines.flow.Flow

interface StudentRepository {
    fun getStudents(): Flow<List<Student>>
    fun getFaculties(): Flow<List<Faculty>>
    suspend fun saveStudent(student: Student)
    suspend fun saveFaculty(faculty: Faculty)
}

interface VisitorRepository {
    fun getVisitors(): Flow<List<Visitor>>
    suspend fun saveVisitor(visitor: Visitor)
}

interface VisitRepository {
    fun getVisits(): Flow<List<Visit>>
    suspend fun saveVisit(visit: Visit)
    suspend fun deleteVisit(visit: Visit)
}

interface UserRepository {
    fun getUsers(): Flow<List<User>>
    fun getStaffs(): Flow<List<User>>
    suspend fun saveUser(user: User)
    suspend fun getUser(uid: String): User?
    suspend fun getAdminUser(uid: String): AdminUser?
    suspend fun deleteUser(uid: String)
    suspend fun setStaffAvailability(uid: String, isAvailable: Boolean)
}

interface QueueRepository {
    suspend fun getTodayMaxQueue(): Int
}

interface AppRepository :
    StudentRepository,
    VisitorRepository,
    VisitRepository,
    UserRepository,
    QueueRepository