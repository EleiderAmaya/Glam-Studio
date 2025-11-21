package com.glamstudio.data.repository

import com.glamstudio.data.dao.AppointmentDao
import com.glamstudio.data.dao.InvoiceDao
import com.glamstudio.data.entity.InvoiceEntity
import com.glamstudio.data.entity.InvoiceItemEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

class BillingRepository(
    private val invoices: InvoiceDao,
    private val appointments: AppointmentDao
) {
    fun listInvoices(): Flow<List<InvoiceEntity>> = invoices.listAll()

    suspend fun markPaid(id: String) = invoices.updateStatus(id, "PAID")
    suspend fun void(id: String) = invoices.updateStatus(id, "VOID")

    suspend fun createInvoiceForAppointment(
        appointmentId: String,
        clientId: String,
        services: List<com.glamstudio.data.entity.ServiceEntity>
    ): String {
        val now = System.currentTimeMillis()
        val total = services.sumOf { it.priceCents }
        val invoiceId = UUID.randomUUID().toString()
        val invoice = InvoiceEntity(
            id = invoiceId,
            clientId = clientId,
            createdAtEpochMs = now,
            status = "ISSUED",
            totalCents = total,
            notes = null
        )
        invoices.upsertInvoice(invoice)
        val items = services.map { s ->
            InvoiceItemEntity(
                id = UUID.randomUUID().toString(),
                invoiceId = invoiceId,
                serviceId = s.id,
                quantity = 1,
                unitPriceCents = s.priceCents,
                description = s.name
            )
        }
        invoices.upsertItems(items)
        return invoiceId
    }

    // Métricas
    fun sumPaidForDay(date: LocalDate): Flow<Long?> {
        val start = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val end = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1
        return invoices.sumPaidInRange(start, end)
    }

    fun expectedForDay(date: LocalDate): Flow<Long> {
        val dayMs = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return appointments.sumServicesPriceForDay(dayMs)
    }

    fun monthPaidSumAndCount(date: LocalDate): Pair<Flow<Long?>, Flow<Int>> {
        val first = date.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val last = date.withDayOfMonth(date.lengthOfMonth()).plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1
        return invoices.sumPaidInRange(first, last) to invoices.countPaidInRange(first, last)
    }

    fun monthAppointmentsCounts(date: LocalDate): Pair<Flow<Int>, Flow<Int>> {
        val first = date.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val last = date.withDayOfMonth(date.lengthOfMonth()).plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1
        val scheduled = appointments.countByStatusInRange("SCHEDULED", first, last)
        val cancelled = appointments.countByStatusInRange("CANCELLED", first, last)
        return scheduled to cancelled
    }
}
