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

class AdminViewModel(
    private val repository: AppRepository = FirestoreVisitRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    var loggedInUser by mutableStateOf<User?>(null)
    var checkingAuth by mutableStateOf(true)

    var email       by mutableStateOf("")
    var password    by mutableStateOf("")
    var loginError  by mutableStateOf("")
    var isLoggingIn by mutableStateOf(false)

    var search       by mutableStateOf("")
    var dateFrom     by mutableStateOf("")
    var dateTo       by mutableStateOf("")
    var statusFilter by mutableStateOf("All")
    var typeFilter   by mutableStateOf("All")

    val allVisits: StateFlow<List<Visit>> = repository.getVisits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val users: StateFlow<List<User>> = repository.getUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val students: StateFlow<List<Student>> = repository.getStudents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val faculties: StateFlow<List<Faculty>> = repository.getFaculties()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val visitors: StateFlow<List<Visitor>> = repository.getVisitors()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        checkCurrentAuth()
    }

    private fun checkCurrentAuth() {
        val current = auth.currentUser
        if (current != null) {
            viewModelScope.launch {
                loggedInUser = repository.getUser(current.uid)
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
                        if (user != null && (user.role == "Admin" || user.role == "Super Admin" || user.role == "Staff")) {
                            loggedInUser = user
                            onSuccess()
                        } else {
                            val admin = repository.getAdminUser(uid)
                            if (admin != null) {
                                loggedInUser = User(uid = admin.uid, email = admin.email, role = admin.role)
                                onSuccess()
                            } else {
                                loginError = "Access Denied: Unauthorized role."
                            }
                        }
                    }
                } else {
                    isLoggingIn = false
                    loginError = task.exception?.message ?: "Login failed."
                }
            }
    }

    fun logout(onComplete: () -> Unit) {
        auth.signOut()
        loggedInUser = null
        onComplete()
    }

    fun deleteVisit(visit: Visit) {
        viewModelScope.launch {
            repository.deleteVisit(visit)
        }
    }

    fun createUser(name: String, email: String, pass: String, role: String, onResult: (Boolean, String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = task.result?.user?.uid ?: ""
                    val user = User(uid = uid, name = name, email = email, role = role, isAvailable = true)
                    viewModelScope.launch {
                        repository.saveUser(user)
                        onResult(true, "Success")
                    }
                } else {
                    onResult(false, task.exception?.message ?: "Failed to create user")
                }
            }
    }

    fun updateUser(user: User, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                repository.saveUser(user)
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun deleteUser(uid: String, onResult: () -> Unit) {
        viewModelScope.launch {
            repository.deleteUser(uid)
            onResult()
        }
    }

    fun setStaffAvailability(uid: String, isAvailable: Boolean) {
        if (uid.isBlank()) return
        viewModelScope.launch {
            repository.setStaffAvailability(uid, isAvailable)
        }
    }
}