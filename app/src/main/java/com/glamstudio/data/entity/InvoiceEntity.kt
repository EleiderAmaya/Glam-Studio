package com.glamstudio.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "invoices",
    foreignKeys = [
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("clientId"), Index("createdAtEpochMs")]
)
data class InvoiceEntity(
    @PrimaryKey val id: String,
    val clientId: String,
    val createdAtEpochMs: Long,
    val status: String, // DRAFT | ISSUED | PAID | VOID
    val totalCents: Long,
    val notes: String?
)
