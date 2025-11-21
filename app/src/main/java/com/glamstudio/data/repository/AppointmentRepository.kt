package com.glamstudio.data.repository

import com.glamstudio.data.dao.AppointmentDao
import com.glamstudio.data.entity.AppointmentEntity
import com.glamstudio.data.entity.ServiceEntity

class AppointmentRepository(private val dao: AppointmentDao) {
    suspend fun createOrUpdateWithServices(appointment: AppointmentEntity, serviceIds: List<String>): Boolean {
        val overlaps = dao.countOverlaps(appointment.startEpochMs, appointment.endEpochMs, appointment.id)
        if (overlaps > 0) return false
        dao.upsert(appointment)
        dao.replaceServicesForAppointment(appointment.id, serviceIds)
        return true
    }

    suspend fun servicesForAppointment(appointmentId: String): List<ServiceEntity> = dao.getServicesForAppointment(appointmentId)

    suspend fun daoGetById(id: String): AppointmentEntity? = dao.getById(id)

    suspend fun updateStatus(id: String, status: String) = dao.updateStatus(id, status)
}
