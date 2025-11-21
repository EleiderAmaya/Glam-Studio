package com.glamstudio.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.glamstudio.data.di.Providers
import com.glamstudio.data.repository.AppointmentRepository
import com.glamstudio.data.repository.ClientRepository
import com.glamstudio.data.repository.ServiceRepository
import com.glamstudio.data.entity.AppointmentEntity
import com.glamstudio.data.entity.ClientEntity
import com.glamstudio.data.entity.ServiceEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.UUID

class ScheduleViewModel(
    private val clientRepository: ClientRepository,
    private val serviceRepository: ServiceRepository,
    private val appointmentRepository: AppointmentRepository
) : ViewModel() {

    val clients: Flow<List<ClientEntity>> = clientRepository.getAll()
    val services: Flow<List<ServiceEntity>> = serviceRepository.getActive()

    suspend fun saveAppointment(
        clientId: String,
        date: LocalDate,
        startTime: LocalTime,
        selectedServices: List<ServiceEntity>,
        notes: String? = null
    ): Boolean {
        val totalMinutes = selectedServices.sumOf { it.durationMinutes }
        val endTime = startTime.plusMinutes(totalMinutes.toLong())
        val dateEpochMs = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val startEpochMs = LocalDateTime.of(date, startTime).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endEpochMs = LocalDateTime.of(date, endTime).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val appointment = AppointmentEntity(
            id = UUID.randomUUID().toString(),
            clientId = clientId,
            dateEpochMs = dateEpochMs,
            startEpochMs = startEpochMs,
            endEpochMs = endEpochMs,
            status = "SCHEDULED",
            notes = notes
        )
        return appointmentRepository.createOrUpdateWithServices(
            appointment = appointment,
            serviceIds = selectedServices.map { it.id }
        )
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = Providers.database(context)
                val clients = ClientRepository(db.clientDao())
                val services = ServiceRepository(db.serviceDao())
                val appointments = AppointmentRepository(db.appointmentDao())
                return ScheduleViewModel(clients, services, appointments) as T
            }
        }
    }
}
