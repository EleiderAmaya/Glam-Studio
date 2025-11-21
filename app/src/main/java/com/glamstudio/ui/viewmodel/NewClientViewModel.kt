package com.glamstudio.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.glamstudio.data.di.Providers
import com.glamstudio.data.entity.ClientEntity
import com.glamstudio.data.repository.ClientRepository
import java.util.UUID
import kotlinx.coroutines.launch

class NewClientViewModel(private val repository: ClientRepository) : ViewModel() {
    fun save(
        fullName: String,
        phone: String,
        email: String?,
        address: String?,
        neighborhood: String?,
        notes: String?,
        isVip: Boolean = false
    ) {
        val now = System.currentTimeMillis()
        val client = ClientEntity(
            id = UUID.randomUUID().toString(),
            fullName = fullName.trim(),
            phone = phone.trim(),
            email = email?.trim().takeUnless { it.isNullOrBlank() },
            address = address?.trim().takeUnless { it.isNullOrBlank() },
            neighborhood = neighborhood?.trim().takeUnless { it.isNullOrBlank() },
            notes = notes?.trim().takeUnless { it.isNullOrBlank() },
            isVip = isVip,
            isActive = true,
            createdAtEpochMs = now,
            updatedAtEpochMs = now
        )
        viewModelScope.launch { repository.upsert(client) }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = Providers.database(context)
                val repo = ClientRepository(db.clientDao())
                return NewClientViewModel(repo) as T
            }
        }
    }
}
