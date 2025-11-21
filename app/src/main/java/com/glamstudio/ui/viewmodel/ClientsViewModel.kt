package com.glamstudio.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.glamstudio.data.di.Providers
import com.glamstudio.data.repository.ClientRepository
import com.glamstudio.data.entity.ClientEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class ClientsViewModel(private val repository: ClientRepository) : ViewModel() {
    private val query = MutableStateFlow("")

    val clients: StateFlow<List<ClientEntity>> = query
        .flatMapLatest { q ->
            if (q.isBlank()) repository.getAll() else repository.searchByName(q)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onQueryChange(text: String) {
        query.value = text
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = Providers.database(context)
                val repo = ClientRepository(db.clientDao())
                return ClientsViewModel(repo) as T
            }
        }
    }
}
