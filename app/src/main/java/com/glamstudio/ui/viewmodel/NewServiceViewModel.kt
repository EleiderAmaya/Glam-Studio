package com.glamstudio.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.glamstudio.data.di.Providers
import com.glamstudio.data.entity.ServiceEntity
import com.glamstudio.data.repository.ServiceRepository
import java.util.UUID
import kotlinx.coroutines.launch

class NewServiceViewModel(private val repository: ServiceRepository) : ViewModel() {
    fun save(
        name: String,
        description: String?,
        durationMinutes: Int,
        priceInput: String
    ) {
        val now = System.currentTimeMillis()
        val pricePesos = priceInput.filter { it.isDigit() }.toLongOrNull() ?: 0L
        val service = ServiceEntity(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            description = description?.trim().takeUnless { it.isNullOrBlank() },
            durationMinutes = durationMinutes,
            priceCents = pricePesos * 100,
            isActive = true,
            createdAtEpochMs = now,
            updatedAtEpochMs = now
        )
        viewModelScope.launch { repository.upsert(service) }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = Providers.database(context)
                val repo = com.glamstudio.data.repository.ServiceRepository(db.serviceDao())
                return NewServiceViewModel(repo) as T
            }
        }
    }
}
