package com.glamstudio.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.glamstudio.data.entity.ServiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {
    @Upsert
    suspend fun upsert(service: ServiceEntity)

    @Query("DELETE FROM services WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM services WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ServiceEntity?

    @Query("SELECT * FROM services WHERE isActive = 1 ORDER BY name ASC")
    fun getActive(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun search(query: String): Flow<List<ServiceEntity>>
}
