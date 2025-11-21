package com.glamstudio.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "appointments",
    foreignKeys = [
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("clientId"), Index("dateEpochMs"), Index("startEpochMs")]
)
data class AppointmentEntity(
    @PrimaryKey val id: String,
    val clientId: String,
    val dateEpochMs: Long,
    val startEpochMs: Long,
    val endEpochMs: Long,
    val status: String, // SCHEDULED | COMPLETED | CANCELLED
    val notes: String?
)
