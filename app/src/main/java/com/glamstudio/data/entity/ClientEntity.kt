package com.glamstudio.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "clients",
    indices = [
        Index(value = ["phone"], unique = true),
        Index(value = ["fullName"])
    ]
)
data class ClientEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val phone: String,
    val email: String?,
    val address: String?,
    val neighborhood: String?,
    val notes: String?,
    val isVip: Boolean = false,
    val isActive: Boolean = true,
    val createdAtEpochMs: Long,
    val updatedAtEpochMs: Long
)
