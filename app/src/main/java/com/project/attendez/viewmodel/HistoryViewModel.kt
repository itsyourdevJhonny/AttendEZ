package com.project.attendez.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.attendez.data.local.repository.AttendanceRepository
import com.project.attendez.data.local.util.AttendanceSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.value = HistoryUiState.Loading
            try {
                val history = attendanceRepository.getAttendanceHistory()
                _uiState.value = HistoryUiState.Success(history)
            } catch (e: Exception) {
                _uiState.value = HistoryUiState.Error(
                    e.message ?: "Failed to load history"
                )
            }
        }
    }
}

data class AttendanceSummary(
    val eventId: Long,
    val eventName: String,
    val date: String,
    val total: Int,
    val present: Int,
    val absent: Int
)


sealed interface HistoryUiState {
    object Loading : HistoryUiState
    data class Success(val records: List<com.project.attendez.viewmodel.AttendanceSummary>) : HistoryUiState
    data class Error(val message: String) : HistoryUiState
}
