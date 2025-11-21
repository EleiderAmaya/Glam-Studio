package com.glamstudio.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.glamstudio.data.di.Providers
import com.glamstudio.data.entity.InvoiceEntity
import com.glamstudio.data.entity.InvoiceItemEntity
import com.glamstudio.data.repository.BillingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InvoiceDetailViewModel(private val repo: BillingRepository, private val invoiceId: String) : ViewModel() {
    private val _invoice = MutableStateFlow<InvoiceEntity?>(null)
    val invoice: StateFlow<InvoiceEntity?> = _invoice

    private val _items = MutableStateFlow<List<InvoiceItemEntity>>(emptyList())
    val items: StateFlow<List<InvoiceItemEntity>> = _items

    init { viewModelScope.launch { reload() } }

    private suspend fun reload() {
        _invoice.value = repo.getById(invoiceId)
        _items.value = repo.itemsForInvoice(invoiceId)
    }

    fun markPaid() { viewModelScope.launch { repo.markPaid(invoiceId); reload() } }
    fun void() { viewModelScope.launch { repo.void(invoiceId); reload() } }

    companion object {
        fun factory(context: Context, invoiceId: String): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = Providers.database(context)
                val repo = BillingRepository(db.invoiceDao(), db.appointmentDao())
                return InvoiceDetailViewModel(repo, invoiceId) as T
            }
        }
    }
}
