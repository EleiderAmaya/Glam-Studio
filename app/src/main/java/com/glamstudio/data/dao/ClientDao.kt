package com.glamstudio.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.glamstudio.data.entity.ClientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {
    @Upsert
    suspend fun upsert(client: ClientEntity)

    @Query("DELETE FROM clients WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM clients WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ClientEntity?

    @Query("SELECT * FROM clients ORDER BY fullName ASC")
    fun getAll(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM clients WHERE fullName LIKE '%' || :query || '%' ORDER BY fullName ASC")
    fun searchByName(query: String): Flow<List<ClientEntity>>
}
