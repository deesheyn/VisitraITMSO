package com.visitraitmso.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

class VisitViewModel(
    private val repository: AppRepository = FirestoreVisitRepository()
) : ViewModel() {

    var students  by mutableStateOf<List<Student>>(emptyList())
        private set
    var faculties by mutableStateOf<List<Faculty>>(emptyList())
        private set
    var visits    by mutableStateOf<List<Visit>>(emptyList())
        private set
    var staffs    by mutableStateOf<List<User>>(emptyList())
        private set
    var visitors  by mutableStateOf<List<Visitor>>(emptyList())
        private set

    var visitorTab   by mutableStateOf(0)
    var studentId    by mutableStateOf("")
    var employeeId   by mutableStateOf("")
    var extName      by mutableStateOf("")
    var extOrg       by mutableStateOf("")
    var extContact   by mutableStateOf("")
    var purpose      by mutableStateOf("")
    var otherConcern by mutableStateOf("")

    var showSuccess      by mutableStateOf(false)
    var showDuplicate    by mutableStateOf(false)

    // ── Queue overlay ─────────────────────────
    var showQueueOverlay    by mutableStateOf(false)
    var assignedQueueNumber by mutableStateOf(0)
    var assignedStaffName   by mutableStateOf("")

    init {
        viewModelScope.launch {
            repository.getStudents()
                .catch { students = emptyList() }
                .collect { students = it }
        }
        viewModelScope.launch {
            repository.getFaculties()
                .catch { faculties = emptyList() }
                .collect { faculties = it }
        }
        viewModelScope.launch {
            repository.getVisits()
                .catch { visits = emptyList() }
                .collect { visits = it }
        }
        viewModelScope.launch {
            repository.getStaffs()
                .catch { staffs = emptyList() }
                .collect { staffs = it }
        }
        viewModelScope.launch {
            repository.getVisitors()
                .catch { visitors = emptyList() }
                .collect { visitors = it }
        }
    }

    private fun todayStartPH(): Long {
        return Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila")).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    // ── Student ───────────────────────────────
    val selectedStudent: Student?
        get() = students.find {
            it.studentId.trim().equals(studentId.trim(), ignoreCase = true)
        }

    val studentSuggestions: List<Student>
        get() {
            val q = studentId.trim()
            if (q.isEmpty() || students.any {
                    it.studentId.trim().equals(q, true)
                }) return emptyList()
            return students.filter {
                it.studentId.contains(q, true) || it.name.contains(q, true)
            }.take(5)
        }

    // ── Faculty ───────────────────────────────
    val selectedFaculty: Faculty?
        get() = faculties.find {
            it.employeeId.trim().equals(employeeId.trim(), ignoreCase = true)
        }

    val facultySuggestions: List<Faculty>
        get() {
            val q = employeeId.trim()
            if (q.isEmpty() || faculties.any {
                    it.employeeId.trim().equals(q, true)
                }) return emptyList()
            return faculties.filter {
                it.employeeId.contains(q, true) || it.name.contains(q, true)
            }.take(5)
        }

    // ── External visitor suggestions ──────────
    val externalSuggestions: List<Visitor>
        get() {
            val q = extName.trim()
            if (q.isEmpty()) return emptyList()
            return visitors.filter {
                it.name.contains(q, true)
            }.take(5)
        }

    fun selectExternalVisitor(visitor: Visitor) {
        extName    = visitor.name
        extOrg     = visitor.organization
        extContact = visitor.contact
    }

    // ── Active visit ──────────────────────────
    val activeVisit: Visit?
        get() {
            val todayStart = todayStartPH()
            return when (visitorTab) {
                0 -> visits.find {
                    it.studentId.trim().equals(studentId.trim(), true) &&
                            it.timeOut == null &&
                            it.timeIn >= todayStart
                }
                1 -> visits.find {
                    it.studentId.trim().equals(employeeId.trim(), true) &&
                            it.timeOut == null &&
                            it.timeIn >= todayStart
                }
                2 -> if (extName.isBlank()) null else visits.find {
                    it.visitorType == "External" &&
                            it.visitorName.startsWith(extName.trim()) &&
                            it.timeOut == null &&
                            it.timeIn >= todayStart
                }
                else -> null
            }
        }

    val finalPurpose: String
        get() = if (purpose == "Other Concerns") otherConcern else purpose

    val canTimeIn: Boolean
        get() = when (visitorTab) {
            0    -> studentId.isNotBlank() && finalPurpose.isNotBlank() && activeVisit == null
            1    -> employeeId.isNotBlank() && finalPurpose.isNotBlank() && activeVisit == null
            else -> extName.isNotBlank() && finalPurpose.isNotBlank() && activeVisit == null
        }

    val canTimeOut: Boolean
        get() = activeVisit != null

    fun resetAll() {
        studentId    = ""; employeeId   = ""
        extName      = ""; extOrg       = ""; extContact = ""
        purpose      = ""; otherConcern = ""
    }

    // ── Auto-assign staff ─────────────────────
    private fun pickStaff(): User? {
        if (staffs.isEmpty()) return null
        val todayStart  = todayStartPH()
        val todayActive = visits.filter {
            it.timeIn >= todayStart && it.timeOut == null
        }
        return staffs.minByOrNull { staff ->
            todayActive.count { it.assignedStaffUid == staff.uid }
        }
    }

    fun timeIn() {
        if (!canTimeIn) return
        val now  = System.currentTimeMillis()
        val id   = when (visitorTab) {
            0    -> studentId.trim()
            1    -> employeeId.trim()
            else -> "EXT-$now"
        }
        val name = when (visitorTab) {
            0    -> selectedStudent?.name ?: studentId
            1    -> selectedFaculty?.name ?: employeeId
            else -> extName
        }
        val type = when (visitorTab) {
            0    -> "Student"
            1    -> "Faculty"
            else -> "External"
        }

        val hasDuplicate = visits.any {
            it.studentId == id &&
                    isSameDay(it.timeIn, now) &&
                    it.purpose == finalPurpose
        }
        if (hasDuplicate) {
            showDuplicate = true
            return
        }

        viewModelScope.launch {
            val maxQueue = repository.getTodayMaxQueue()
            val queueNum = maxQueue + 1
            val staff    = pickStaff()

            repository.saveVisit(
                Visit(
                    studentId         = id,
                    purpose           = finalPurpose,
                    timeIn            = now,
                    timeOut           = null,
                    visitorType       = type,
                    visitorName       = name,
                    visitorOrg        = extOrg,
                    visitorContact    = extContact,
                    queueNumber       = queueNum,
                    assignedStaffUid  = staff?.uid ?: "",
                    assignedStaffName = staff?.name ?: "Unassigned",
                    serving         = false
                )
            )

            assignedQueueNumber = queueNum
            assignedStaffName   = staff?.name ?: "Unassigned"
            showQueueOverlay    = true
        }
    }

    fun timeOut() {
        val visitToUpdate = activeVisit ?: return
        val now = System.currentTimeMillis()
        viewModelScope.launch {
            repository.saveVisit(visitToUpdate.copy(timeOut = now))
            resetAll()
            showSuccess = true
        }
    }

    private fun isSameDay(t1: Long, t2: Long): Boolean {
        val tz = TimeZone.getTimeZone("Asia/Manila")
        val c1 = Calendar.getInstance(tz).apply { timeInMillis = t1 }
        val c2 = Calendar.getInstance(tz).apply { timeInMillis = t2 }
        return c1.get(Calendar.YEAR)        == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
    }
}
    private fun pickStaff(staffList: List<User>, visits: List<Visit>): User? {
        val availableStaff = staffList.filter { it.isAvailable }
        if (availableStaff.isEmpty()) return null
        return availableStaff.minByOrNull { staff ->
            visits.count { it.assignedStaffUid == staff.uid }
        }
    }