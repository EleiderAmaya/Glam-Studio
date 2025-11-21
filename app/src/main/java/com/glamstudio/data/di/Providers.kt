package com.glamstudio.data.di

import android.content.Context
import androidx.room.Room
import com.glamstudio.data.GlamDatabase

object Providers {
    @Volatile
    private var dbInstance: GlamDatabase? = null

    fun database(context: Context): GlamDatabase = dbInstance ?: synchronized(this) {
        dbInstance ?: Room.databaseBuilder(
            context.applicationContext,
            GlamDatabase::class.java,
            "glam.db"
        )
            .fallbackToDestructiveMigration()
            .build()
            .also { dbInstance = it }
    }
}
