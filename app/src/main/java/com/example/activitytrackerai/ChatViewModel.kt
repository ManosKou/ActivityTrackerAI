package com.example.activitytrackerai

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = DatabaseProvider.get(app).chatMessageDao()
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    init {
        viewModelScope.launch {
            _messages.value = dao.getAllMessages()
        }
    }

    fun sendMessage(text: String, isUser: Boolean) {
        viewModelScope.launch {
            dao.insertMessage(ChatMessage(text = text, isUser = isUser))
            _messages.value = dao.getAllMessages()
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            dao.clearAll()
            _messages.value = emptyList()
        }
    }
}