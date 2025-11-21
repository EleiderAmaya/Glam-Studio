package com.glamstudio.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "invoice_items",
    foreignKeys = [
        ForeignKey(
            entity = InvoiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ServiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["serviceId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("invoiceId"), Index("serviceId")]
)
data class InvoiceItemEntity(
    @PrimaryKey val id: String,
    val invoiceId: String,
    val serviceId: String,
    val quantity: Int = 1,
    val unitPriceCents: Long,
    val description: String?
)
