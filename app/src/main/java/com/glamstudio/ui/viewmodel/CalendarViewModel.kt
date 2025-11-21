package com.glamstudio.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.glamstudio.data.di.Providers
import com.glamstudio.data.dao.AppointmentDao
import com.glamstudio.data.model.AppointmentWithClient
import com.glamstudio.data.entity.AppointmentEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class CalendarViewModel(private val dao: AppointmentDao) : ViewModel() {
    private val monthAnchor = MutableStateFlow(LocalDate.now())

    // Ocupación por día del mes: día (1..N) -> ratio [0,1]
    val occupancyByDay: StateFlow<Map<Int, Float>> = monthAnchor
        .flatMapLatest { anchor ->
            val firstDay = anchor.withDayOfMonth(1)
            val lastDay = anchor.withDayOfMonth(anchor.lengthOfMonth())
            val start = LocalDateTime.of(firstDay, java.time.LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val end = LocalDateTime.of(lastDay.plusDays(1), java.time.LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1
            dao.getRange(start, end)
        }
        .map { appts ->
            val minutesPerDay = 8 * 60f // 10:00 - 18:00
            val perDay = HashMap<Int, Long>()
            appts.forEach { a ->
                val day = LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(a.startEpochMs), ZoneId.systemDefault()).dayOfMonth
                val minutes = ((a.endEpochMs - a.startEpochMs) / 60000L).coerceAtLeast(0)
                perDay[day] = (perDay[day] ?: 0L) + minutes
            }
            perDay.mapValues { (_, mins) ->
                val ratio = mins / minutesPerDay
                ratio.coerceIn(0f, 1f)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun setMonth(anchor: LocalDate) {
        monthAnchor.value = anchor
    }

    fun appointmentsWithClientForDay(date: LocalDate): Flow<List<AppointmentWithClient>> {
        val dateEpochMs = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return dao.getWithClientByDay(dateEpochMs)
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = Providers.database(context)
                return CalendarViewModel(db.appointmentDao()) as T
            }
        }
    }
}
