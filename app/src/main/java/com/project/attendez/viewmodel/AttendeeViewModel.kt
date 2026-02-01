package com.project.attendez.viewmodel

import androidx.lifecycle.ViewModel
import com.project.attendez.data.local.repository.AttendeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AttendeeViewModel @Inject constructor(
    private val repository: AttendeeRepository
) : ViewModel() {
    fun getAttendeeById(id: Long) = repository.getAttendeeById(id)
}
