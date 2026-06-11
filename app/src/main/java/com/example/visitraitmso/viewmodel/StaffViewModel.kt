package com.visitraitmso.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

class StaffViewModel(
    private val repository: AppRepository = FirestoreVisitRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    var loggedInStaff by mutableStateOf<User?>(null)
    var checkingAuth  by mutableStateOf(true)

    var email       by mutableStateOf("")
    var password    by mutableStateOf("")
    var loginError  by mutableStateOf("")
    var isLoggingIn by mutableStateOf(false)

    val allVisits: StateFlow<List<Visit>> = repository.getVisits()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allStaffs: StateFlow<List<User>> = repository.getStaffs()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        checkCurrentAuth()
    }

    private fun checkCurrentAuth() {
        val current = auth.currentUser
        if (current != null) {
            viewModelScope.launch {
                val user = repository.getUser(current.uid)
                if (user != null && (user.role == "Staff" || user.role == "Admin")) {
                    loggedInStaff = user
                }
                checkingAuth = false
            }
        } else {
            checkingAuth = false
        }
    }

    fun login(onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            loginError = "Please fill in all fields."
            return
        }
        isLoggingIn = true
        loginError  = ""

        auth.signInWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = task.result?.user?.uid ?: ""
                    viewModelScope.launch {
                        val user = repository.getUser(uid)
                        isLoggingIn = false
                        if (user != null && (user.role == "Staff" || user.role == "Admin")) {
                            loggedInStaff = user
                            onSuccess()
                        } else {
                            loginError = "Access denied. Staff account required."
                            auth.signOut()
                        }
                    }
                } else {
                    isLoggingIn = false
                    loginError  = task.exception?.message ?: "Login failed."
                }
            }
    }

    fun logout(onComplete: () -> Unit) {
        auth.signOut()
        loggedInStaff = null
        onComplete()
    }

    // ── AVAILABILITY ─────────────────────────────────────────────
    fun setAvailability(isAvailable: Boolean) {
        val uid = loggedInStaff?.uid ?: return
        viewModelScope.launch {
            repository.setStaffAvailability(uid, isAvailable)
            loggedInStaff = loggedInStaff?.copy(isAvailable = isAvailable)
        }
    }

    // ── QUEUE HELPERS ─────────────────────────────────────────────
    private fun todayStartPH(): Long {
        return Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila")).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun myVisits(visits: List<Visit>): List<Visit> {
        val uid        = loggedInStaff?.uid ?: return emptyList()
        val todayStart = todayStartPH()
        return visits.filter {
            it.assignedStaffUid == uid && it.timeIn >= todayStart
        }.sortedBy { it.queueNumber }
    }

    fun waitingVisits(visits: List<Visit>): List<Visit> =
        myVisits(visits).filter {
            it.timeOut == null && !it.serving
        }

    fun servingVisits(visits: List<Visit>): List<Visit> =
        myVisits(visits).filter {
            it.timeOut == null && it.serving
        }

    fun doneVisits(visits: List<Visit>): List<Visit> =
        myVisits(visits).filter { it.timeOut != null }

    // ── QUEUE ACTIONS ─────────────────────────────────────────────
    fun callNext(visit: Visit) {
        viewModelScope.launch {
            repository.saveVisit(visit.copy(serving = true))
        }
    }

    fun markDone(visit: Visit) {
        val now = System.currentTimeMillis()
        viewModelScope.launch {
            repository.saveVisit(
                visit.copy(
                    timeOut = now,
                    serving = false
                )
            )
        }
    }

    fun transferVisit(visit: Visit, targetStaff: User) {
        viewModelScope.launch {
            repository.saveVisit(
                visit.copy(
                    assignedStaffUid  = targetStaff.uid,
                    assignedStaffName = targetStaff.name,
                    serving           = false
                )
            )
        }
    }
}