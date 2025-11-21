package com.glamstudio.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.glamstudio.data.di.Providers
import com.glamstudio.data.repository.ServiceRepository
import com.glamstudio.data.entity.ServiceEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class ServicesViewModel(private val repository: ServiceRepository) : ViewModel() {
    private val query = MutableStateFlow("")

    val services: StateFlow<List<ServiceEntity>> = query
        .flatMapLatest { q ->
            if (q.isBlank()) repository.getActive() else repository.search(q)
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
                val repo = ServiceRepository(db.serviceDao())
                return ServicesViewModel(repo) as T
            }
        }
    }
}
