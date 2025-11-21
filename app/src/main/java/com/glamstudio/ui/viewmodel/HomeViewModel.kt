package com.glamstudio.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.glamstudio.data.di.Providers
import com.glamstudio.data.model.AppointmentWithClient
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.ZoneId

class HomeViewModel(private val repository: com.glamstudio.data.dao.AppointmentDao) : ViewModel() {
    val todayAppointments: StateFlow<List<AppointmentWithClient>> = repository
        .getWithClientByDay(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = Providers.database(context)
                return HomeViewModel(db.appointmentDao()) as T
            }
        }
    }
}
