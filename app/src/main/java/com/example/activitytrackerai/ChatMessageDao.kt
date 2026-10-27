package com.example.activitytrackerai

import androidx.room.*

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    suspend fun getAllMessages(): List<ChatMessage>

    @Insert
    suspend fun insertMessage(msg: ChatMessage)

    @Query("DELETE FROM chat_messages")
    suspend fun clearAll()
}