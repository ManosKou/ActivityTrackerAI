package com.example.activitytrackerai

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ChatMessage::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatMessageDao(): ChatMessageDao
}