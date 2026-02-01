package com.project.attendez.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.attendez.data.local.entity.AttendeeEntity
import com.project.attendez.data.local.repository.AttendeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExistingAttendeeViewModel @Inject constructor(
    private val attendeeRepository: AttendeeRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<ExistingAttendeeUiState>(ExistingAttendeeUiState.Loading)
    val uiState: StateFlow<ExistingAttendeeUiState> = _uiState

    fun loadAttendees() {
        viewModelScope.launch {
            _uiState.value = ExistingAttendeeUiState.Loading
            try {
                val attendees = attendeeRepository.getAttendees().first()
                _uiState.value = ExistingAttendeeUiState.Success(attendees)
            } catch (e: Exception) {
                _uiState.value = ExistingAttendeeUiState.Error(
                    e.message ?: "Failed to load attendees"
                )
            }
        }
    }
}


sealed interface ExistingAttendeeUiState {
    object Loading : ExistingAttendeeUiState
    data class Success(val attendees: List<AttendeeEntity>) : ExistingAttendeeUiState
    data class Error(val message: String) : ExistingAttendeeUiState
}
