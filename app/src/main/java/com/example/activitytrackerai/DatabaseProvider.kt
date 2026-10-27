package com.example.activitytrackerai

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private var db: AppDatabase? = null
    fun get(context: Context): AppDatabase {
        return db ?: Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "mychat.db"
        ).build().also { db = it }
    }
}