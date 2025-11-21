package com.glamstudio.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "services",
    indices = [Index(value = ["name"], unique = true)]
)
data class ServiceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String?,
    val durationMinutes: Int,
    val priceCents: Long,
    val isActive: Boolean = true,
    val createdAtEpochMs: Long,
    val updatedAtEpochMs: Long
)
