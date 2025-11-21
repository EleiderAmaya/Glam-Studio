package com.glamstudio.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.glamstudio.data.entity.AppointmentEntity
import com.glamstudio.data.entity.AppointmentServiceCrossRef
import com.glamstudio.data.model.AppointmentWithClient
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {
    @Upsert
    suspend fun upsert(appointment: AppointmentEntity)

    @Query("DELETE FROM appointments WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM appointments WHERE dateEpochMs = :dateEpochMs ORDER BY startEpochMs ASC")
    fun getByDay(dateEpochMs: Long): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE startEpochMs BETWEEN :startEpochMs AND :endEpochMs ORDER BY startEpochMs ASC")
    fun getRange(startEpochMs: Long, endEpochMs: Long): Flow<List<AppointmentEntity>>

    @Query(
        "SELECT a.id AS id, a.clientId AS clientId, a.dateEpochMs AS dateEpochMs, a.startEpochMs AS startEpochMs, a.endEpochMs AS endEpochMs, a.status AS status, a.notes AS notes, c.fullName AS clientName " +
        "FROM appointments a INNER JOIN clients c ON c.id = a.clientId " +
        "WHERE a.dateEpochMs = :dateEpochMs ORDER BY a.startEpochMs ASC"
    )
    fun getWithClientByDay(dateEpochMs: Long): Flow<List<AppointmentWithClient>>

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
