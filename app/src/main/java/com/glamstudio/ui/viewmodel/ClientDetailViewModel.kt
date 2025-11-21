package com.glamstudio.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.glamstudio.data.di.Providers
import com.glamstudio.data.entity.ClientEntity
import com.glamstudio.data.repository.ClientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClientDetailViewModel(private val repo: ClientRepository, private val clientId: String) : ViewModel() {
    private val _client = MutableStateFlow<ClientEntity?>(null)
    val client: StateFlow<ClientEntity?> = _client

    init {
        viewModelScope.launch {
            _client.value = repo.getById(clientId)
        }
    }

    fun update(
        fullName: String,
        phone: String,
        email: String?,
        address: String?,
        neighborhood: String?,
        notes: String?,
        isActive: Boolean,
        isVip: Boolean
    ) {
        val current = _client.value ?: return
        val now = System.currentTimeMillis()
        val updated = current.copy(
            fullName = fullName.trim(),
            phone = phone.trim(),
            email = email?.trim().takeUnless { it.isNullOrBlank() },
            address = address?.trim().takeUnless { it.isNullOrBlank() },
            neighborhood = neighborhood?.trim().takeUnless { it.isNullOrBlank() },
            notes = notes?.trim().takeUnless { it.isNullOrBlank() },
            isVip = isVip,
            isActive = isActive,
            updatedAtEpochMs = now
        )
        viewModelScope.launch {
            repo.upsert(updated)
            _client.value = updated
        }
    }

    fun delete(onDone: () -> Unit) {
        viewModelScope.launch {
            repo.deleteById(clientId)
            onDone()
        }
    }

    companion object {
        fun factory(context: Context, clientId: String): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = Providers.database(context)
                val repo = ClientRepository(db.clientDao())
                return ClientDetailViewModel(repo, clientId) as T
            }
        }
    }
}
