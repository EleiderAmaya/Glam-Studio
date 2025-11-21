package com.glamstudio.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.glamstudio.data.dao.AppointmentDao
import com.glamstudio.data.dao.ClientDao
import com.glamstudio.data.dao.InvoiceDao
import com.glamstudio.data.dao.ServiceDao
import com.glamstudio.data.entity.AppointmentEntity
import com.glamstudio.data.entity.AppointmentServiceCrossRef
import com.glamstudio.data.entity.ClientEntity
import com.glamstudio.data.entity.InvoiceEntity
import com.glamstudio.data.entity.InvoiceItemEntity
import com.glamstudio.data.entity.ServiceEntity

@Database(
    entities = [
        ClientEntity::class,
        ServiceEntity::class,
        AppointmentEntity::class,
        AppointmentServiceCrossRef::class,
        InvoiceEntity::class,
        InvoiceItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GlamDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDao
    abstract fun serviceDao(): ServiceDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun invoiceDao(): InvoiceDao
}
