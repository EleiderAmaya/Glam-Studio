package com.glamstudio.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.glamstudio.data.entity.AppointmentEntity
import com.glamstudio.data.entity.AppointmentServiceCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {
    @Upsert
    suspend fun upsert(appointment: AppointmentEntity)

    @Query("DELETE FROM appointments WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM appointments WHERE dateEpochMs = :dateEpochMs ORDER BY startEpochMs ASC")
    fun getByDay(dateEpochMs: Long): Flow<List<AppointmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRefs(refs: List<AppointmentServiceCrossRef>)

    @Query("DELETE FROM appointment_services WHERE appointmentId = :appointmentId")
    suspend fun deleteCrossRefsForAppointment(appointmentId: String)

    @Transaction
    suspend fun replaceServicesForAppointment(appointmentId: String, serviceIds: List<String>) {
        deleteCrossRefsForAppointment(appointmentId)
        if (serviceIds.isNotEmpty()) {
            insertCrossRefs(serviceIds.map { sid -> AppointmentServiceCrossRef(appointmentId = appointmentId, serviceId = sid) })
        }
    }
}
