package com.glamstudio.data.repository

import com.glamstudio.data.dao.ClientDao
import com.glamstudio.data.entity.ClientEntity
import kotlinx.coroutines.flow.Flow

class ClientRepository(private val dao: ClientDao) {
    fun getAll(): Flow<List<ClientEntity>> = dao.getAll()
    fun searchByName(query: String): Flow<List<ClientEntity>> = dao.searchByName(query)
    suspend fun upsert(client: ClientEntity) = dao.upsert(client)
    suspend fun deleteById(id: String) = dao.deleteById(id)
    suspend fun getById(id: String): ClientEntity? = dao.getById(id)
}
