package com.glamstudio.data.model

data class AppointmentWithClient(
    val id: String,
    val clientId: String,
    val dateEpochMs: Long,
    val startEpochMs: Long,
    val endEpochMs: Long,
    val status: String,
    val notes: String?,
    val clientName: String
)
