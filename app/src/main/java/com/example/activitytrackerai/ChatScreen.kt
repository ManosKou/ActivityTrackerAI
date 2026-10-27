package com.example.activitytrackerai

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.util.UUID
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.AlertDialog

data class Message(val text: String, val isUser: Boolean)

@Composable
fun ChatScreen() {
    val context = LocalContext.current
    val chatViewModel: ChatViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ChatViewModel(context.applicationContext as Application) as T
            }
        }
    )
    val messages by chatViewModel.messages.collectAsState()
    var input by remember { mutableStateOf(TextFieldValue("")) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Κατάσταση για το dialog επιβεβαίωσης
    var showCleanDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F4F4))
            .padding(8.dp)
    ) {
        // Τίτλος και κουμπί καθαρισμού στην ίδια σειρά
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ActivityTrackerAI Chatbot",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = { showCleanDialog = true },
                modifier = Modifier,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE57373), // Ή όποιο χρώμα θες
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("Clean")
            }
        }

        // Πλαίσιο επιβεβαίωσης
        if (showCleanDialog) {
            AlertDialog(
                onDismissRequest = { showCleanDialog = false },
                title = { Text("Επιβεβαίωση") },
                text = { Text("Θέλεις σίγουρα να καθαρίσεις όλη τη συνομιλία;") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            chatViewModel.clearChat()
                            showCleanDialog = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCleanDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.White)
                .padding(8.dp),
            reverseLayout = true
        ) {
            items(messages.size) { index ->
                val msg = messages[messages.size - index - 1]
                ChatBubble(Message(msg.text, msg.isUser))
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                enabled = !isLoading,
                placeholder = { Text("Γράψε μήνυμα...") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (input.text.isNotBlank()) {
                        val userMessage = input.text
                        input = TextFieldValue("")
                        isLoading = true
                        coroutineScope.launch {
                            chatViewModel.sendMessage(userMessage, true)
                            val botReply = try {
                                sendMessageToDialogflow(
                                    context = context,
                                    sessionId = "user",
                                    userMessage = userMessage
                                )
                            } catch (e: Exception) {
                                "Σφάλμα σύνδεσης με Dialogflow: ${e.message}"
                            }
                            chatViewModel.sendMessage(botReply, false)
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading
            ) {
                Text("Send")
            }
        }
    }
}

// Bubble για κάθε μήνυμα
@Composable
fun ChatBubble(message: Message) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = if (message.isUser) MaterialTheme.colorScheme.primary else Color.LightGray,
            tonalElevation = 2.dp
        ) {
            Text(
                text = message.text,
                color = if (message.isUser) Color.White else Color.Black,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

// Μock απάντηση (θα το αντικαταστήσουμε με Dialogflow)
fun getBotReply(userMessage: String): String {
    return when {
        "γεια" in userMessage.lowercase() -> "Γεια σου! Πώς μπορώ να βοηθήσω;"
        "βήματα" in userMessage.lowercase() -> "Σύντομα θα μπορείς να βλέπεις τα βήματά σου εδώ!"
        else -> "Δεν κατάλαβα. Μπορείς να το ξαναπείς;"
    }
}