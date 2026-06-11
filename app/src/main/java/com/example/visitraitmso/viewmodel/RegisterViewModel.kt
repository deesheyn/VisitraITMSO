package com.visitraitmso.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: AppRepository = FirestoreVisitRepository()
) : ViewModel() {

    var registerTab by mutableStateOf(0)

    // Student fields
    var studentName by mutableStateOf("")
    var studentId by mutableStateOf("")
    var studentType by mutableStateOf("College")
    var course by mutableStateOf("")
    var yearLevel by mutableStateOf("")

    // Faculty fields
    var facultyName by mutableStateOf("")
    var employeeId by mutableStateOf("")
    var department by mutableStateOf("")

    // External fields
    var extName by mutableStateOf("")
    var extOrg by mutableStateOf("")

    var agreed by mutableStateOf(false)
    var isLoading by mutableStateOf(false)
    var showSuccessDialog by mutableStateOf(false)

    val canSave: Boolean
        get() = agreed && when (registerTab) {
            0 -> studentName.isNotBlank() && studentId.isNotBlank()
            1 -> facultyName.isNotBlank() && employeeId.isNotBlank() && department.isNotBlank()
            else -> extName.isNotBlank()
        }

    fun resetFields() {
        studentName = ""; studentId = ""; course = ""; yearLevel = ""
        facultyName = ""; employeeId = ""; department = ""
        extName = ""; extOrg = ""
        agreed = false; studentType = "College"
    }

    fun save() {
        if (!canSave) return

        viewModelScope.launch {
            isLoading = true
            try {
                when (registerTab) {
                    0 -> repository.saveStudent(Student(studentName, studentId, studentType, course, yearLevel))
                    1 -> repository.saveFaculty(Faculty(facultyName, employeeId, department))
                    2 -> {
                        // Save to visitors collection instead of visits
                        repository.saveVisitor(
                            Visitor(
                                name = extName,
                                visitorId = "EXT-${System.currentTimeMillis()}",
                                organization = extOrg
                            )
                        )
                    }
                }
                showSuccessDialog = true
            } catch (e: Exception) {
                // Handle error
            } finally {
                isLoading = false
            }
        }
    }
}
