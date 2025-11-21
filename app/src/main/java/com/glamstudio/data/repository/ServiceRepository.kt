package com.glamstudio.data.repository

import com.glamstudio.data.dao.ServiceDao
import com.glamstudio.data.entity.ServiceEntity
import kotlinx.coroutines.flow.Flow

class ServiceRepository(private val dao: ServiceDao) {
    fun getActive(): Flow<List<ServiceEntity>> = dao.getActive()
    fun search(query: String): Flow<List<ServiceEntity>> = dao.search(query)
    suspend fun upsert(service: ServiceEntity) = dao.upsert(service)
    suspend fun deleteById(id: String) = dao.deleteById(id)
    suspend fun getById(id: String): ServiceEntity? = dao.getById(id)
}
