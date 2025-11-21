package com.glamstudio.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.glamstudio.data.di.Providers
import com.glamstudio.data.dao.AppointmentDao
import com.glamstudio.data.model.AppointmentWithClient
import com.glamstudio.data.repository.BillingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.ZoneId

class HomeViewModel(
    private val appointmentDao: AppointmentDao,
    private val billingRepository: BillingRepository
) : ViewModel() {
    val todayAppointments: StateFlow<List<AppointmentWithClient>> = appointmentDao
        .getWithClientByDay(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Ingresos hoy
    val todayConfirmedIncome: StateFlow<Long> = billingRepository.sumPaidForDay(LocalDate.now())
        .map { it ?: 0L }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val todayExpectedIncome: StateFlow<Long> = billingRepository.expectedForDay(LocalDate.now())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    // Métricas mes
    data class MonthMetrics(
        val scheduled: Int,
        val cancelled: Int,
        val avgTicketCents: Long
    )

    val monthMetrics: StateFlow<MonthMetrics> = run {
        val (sumPaid, countPaid) = billingRepository.monthPaidSumAndCount(LocalDate.now())
        val (scheduled, cancelled) = billingRepository.monthAppointmentsCounts(LocalDate.now())
        combine(sumPaid.map { it ?: 0L }, countPaid, scheduled, cancelled) { sum, count, s, c ->
            val avg = if (count > 0) sum / count else 0L
            MonthMetrics(scheduled = s, cancelled = c, avgTicketCents = avg)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MonthMetrics(0, 0, 0))
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = Providers.database(context)
                val billing = BillingRepository(db.invoiceDao(), db.appointmentDao())
                return HomeViewModel(db.appointmentDao(), billing) as T
            }
        }
    }
}
