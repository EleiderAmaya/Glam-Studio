package com.glamstudio.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.glamstudio.data.di.Providers
import com.glamstudio.data.entity.AppointmentEntity
import com.glamstudio.data.entity.ServiceEntity
import com.glamstudio.data.repository.AppointmentRepository
import com.glamstudio.data.repository.BillingRepository
import com.glamstudio.data.repository.ClientRepository
import com.glamstudio.data.repository.ServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class AppointmentDetailViewModel(
    private val appointmentId: String,
    private val appointmentRepo: AppointmentRepository,
    private val clientRepo: ClientRepository,
    private val serviceRepo: ServiceRepository,
    private val billingRepo: BillingRepository,
) : ViewModel() {

    private val _appointment = MutableStateFlow<AppointmentEntity?>(null)
    val appointment: StateFlow<AppointmentEntity?> = _appointment

    private val _clientName = MutableStateFlow("")
    val clientName: StateFlow<String> = _clientName

    private val _allServices = MutableStateFlow<List<ServiceEntity>>(emptyList())
    val allServices: StateFlow<List<ServiceEntity>> = _allServices

    private val _selectedServices = MutableStateFlow<List<ServiceEntity>>(emptyList())
    val selectedServices: StateFlow<List<ServiceEntity>> = _selectedServices

    init {
        viewModelScope.launch { load() }
    }

    private suspend fun load() {
        val appt = appointmentRepo.daoGetById(appointmentId) ?: return
        _appointment.value = appt
        clientRepo.getById(appt.clientId)?.let { _clientName.value = it.fullName }
        _allServices.value = serviceRepo.getActiveOnce()
        _selectedServices.value = appointmentRepo.servicesForAppointment(appointmentId)
    }

    fun setSelectedServices(services: List<ServiceEntity>) { _selectedServices.value = services }

    fun reschedule(newDate: LocalDate, newStart: LocalTime, notes: String?, onResult: (Boolean) -> Unit) {
        val appt = _appointment.value ?: return
        val totalMinutes = _selectedServices.value.sumOf { it.durationMinutes }
        val endTime = newStart.plusMinutes(totalMinutes.toLong())
        val dateEpochMs = newDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val startEpochMs = LocalDateTime.of(newDate, newStart).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endEpochMs = LocalDateTime.of(newDate, endTime).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val updated = appt.copy(dateEpochMs = dateEpochMs, startEpochMs = startEpochMs, endEpochMs = endEpochMs, notes = notes)
        viewModelScope.launch {
            val ok = appointmentRepo.createOrUpdateWithServices(updated, _selectedServices.value.map { it.id })
            if (ok) _appointment.value = updated
            onResult(ok)
        }
    }

    fun cancel(onDone: () -> Unit) {
        val appt = _appointment.value ?: return
        viewModelScope.launch {
            appointmentRepo.updateStatus(appt.id, "CANCELLED")
            _appointment.value = appt.copy(status = "CANCELLED")
            onDone()
        }
    }

    fun finalize(onDone: () -> Unit) {
        val appt = _appointment.value ?: return
        viewModelScope.launch {
            appointmentRepo.updateStatus(appt.id, "COMPLETED")
            _appointment.value = appt.copy(status = "COMPLETED")
            onDone()
        }
    }

    fun generateInvoice(extraPesos: Long?, onCreated: (String) -> Unit) {
        val appt = _appointment.value ?: return
        viewModelScope.launch {
            val invoiceId = billingRepo.createInvoiceFromAppointmentId(appt.id, appt.clientId, extraPesos)
            onCreated(invoiceId)
        }
    }

    companion object {
        fun factory(context: Context, appointmentId: String): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = Providers.database(context)
                val appointmentRepo = AppointmentRepository(db.appointmentDao())
                val clientRepo = ClientRepository(db.clientDao())
                val serviceRepo = ServiceRepository(db.serviceDao())
                val billingRepo = BillingRepository(db.invoiceDao(), db.appointmentDao())
                return AppointmentDetailViewModel(appointmentId, appointmentRepo, clientRepo, serviceRepo, billingRepo) as T
            }
        }
    }
}
