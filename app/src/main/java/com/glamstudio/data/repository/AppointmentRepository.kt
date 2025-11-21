package com.glamstudio.data.repository

import com.glamstudio.data.dao.AppointmentDao
import com.glamstudio.data.entity.AppointmentEntity

class AppointmentRepository(private val dao: AppointmentDao) {
    suspend fun createOrUpdateWithServices(appointment: AppointmentEntity, serviceIds: List<String>) {
        dao.upsert(appointment)
        dao.replaceServicesForAppointment(appointment.id, serviceIds)
    }
}
