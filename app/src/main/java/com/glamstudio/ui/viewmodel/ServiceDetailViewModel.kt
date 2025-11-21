package com.glamstudio.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.glamstudio.data.di.Providers
import com.glamstudio.data.entity.ServiceEntity
import com.glamstudio.data.repository.ServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ServiceDetailViewModel(private val repo: ServiceRepository, private val serviceId: String) : ViewModel() {
    private val _service = MutableStateFlow<ServiceEntity?>(null)
    val service: StateFlow<ServiceEntity?> = _service

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        viewModelScope.launch { _service.value = repo.getById(serviceId) }
    }

    fun update(name: String, description: String?, durationMinutes: Int, pricePesos: Long) {
        val current = _service.value ?: return
        val now = System.currentTimeMillis()
        val updated = current.copy(
            name = name.trim(),
            description = description?.trim().takeUnless { it.isNullOrBlank() },
            durationMinutes = durationMinutes,
            priceCents = pricePesos * 100,
            updatedAtEpochMs = now
        )
        viewModelScope.launch {
            repo.upsert(updated)
            _service.value = updated
        }
    }

    fun delete(onDone: () -> Unit) {
        viewModelScope.launch {
            val usage = repo.countUsageInAppointments(serviceId)
            if (usage > 0) {
                _error.value = "No se puede eliminar: el servicio tiene citas asociadas"
                return@launch
            }
            try {
                repo.deleteById(serviceId)
                onDone()
            } catch (e: Exception) {
                _error.value = "No se pudo eliminar el servicio"
            }
        }
    }

    companion object {
        fun factory(context: Context, serviceId: String): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = Providers.database(context)
                val repo = ServiceRepository(db.serviceDao())
                return ServiceDetailViewModel(repo, serviceId) as T
            }
        }
    }
}
