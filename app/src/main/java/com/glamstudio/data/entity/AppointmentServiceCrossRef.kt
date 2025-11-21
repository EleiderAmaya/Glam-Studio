package com.glamstudio.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "appointment_services",
    primaryKeys = ["appointmentId", "serviceId"],
    foreignKeys = [
        ForeignKey(
            entity = AppointmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["appointmentId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ServiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["serviceId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("serviceId")]
)
data class AppointmentServiceCrossRef(
    val appointmentId: String,
    val serviceId: String
)
