package com.glamstudio.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.glamstudio.data.di.Providers
import com.glamstudio.data.entity.InvoiceEntity
import com.glamstudio.data.repository.BillingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InvoiceListViewModel(private val billing: BillingRepository) : ViewModel() {
    val invoices: StateFlow<List<InvoiceEntity>> = billing.listInvoices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun markPaid(id: String) { viewModelScope.launch { billing.markPaid(id) } }
    fun void(id: String) { viewModelScope.launch { billing.void(id) } }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = Providers.database(context)
                val repo = BillingRepository(db.invoiceDao(), db.appointmentDao())
                return InvoiceListViewModel(repo) as T
            }
        }
    }
}
