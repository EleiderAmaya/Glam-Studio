package com.glamstudio.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.glamstudio.data.entity.InvoiceEntity
import com.glamstudio.data.entity.InvoiceItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceDao {
    @Upsert
    suspend fun upsertInvoice(invoice: InvoiceEntity)

    @Upsert
    suspend fun upsertItems(items: List<InvoiceItemEntity>)

    @Query("SELECT * FROM invoices WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): InvoiceEntity?

    @Query("UPDATE invoices SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)

    @Query("SELECT * FROM invoices WHERE clientId = :clientId ORDER BY createdAtEpochMs DESC")
    fun getByClient(clientId: String): Flow<List<InvoiceEntity>>

    @Query("SELECT * FROM invoice_items WHERE invoiceId = :invoiceId")
    suspend fun getItems(invoiceId: String): List<InvoiceItemEntity>

    @Query("SELECT * FROM invoices ORDER BY createdAtEpochMs DESC")
    fun listAll(): Flow<List<InvoiceEntity>>

    @Query("SELECT * FROM invoices WHERE createdAtEpochMs BETWEEN :startMs AND :endMs ORDER BY createdAtEpochMs DESC")
    fun listByRange(startMs: Long, endMs: Long): Flow<List<InvoiceEntity>>

    @Query("SELECT SUM(totalCents) FROM invoices WHERE status = 'PAID' AND createdAtEpochMs BETWEEN :startMs AND :endMs")
    fun sumPaidInRange(startMs: Long, endMs: Long): Flow<Long?>

    @Query("SELECT COUNT(*) FROM invoices WHERE status = 'PAID' AND createdAtEpochMs BETWEEN :startMs AND :endMs")
    fun countPaidInRange(startMs: Long, endMs: Long): Flow<Int>
}
