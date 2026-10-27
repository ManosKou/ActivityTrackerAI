package com.example.activitytrackerai

import android.content.Context

@Suppress("UNUSED_PARAMETER")
suspend fun sendMessageToDialogflow(
    context: Context,
    sessionId: String,
    userMessage: String
): String {
    return "Το chatbot δεν είναι ενεργό στη δημόσια έκδοση παρουσίασης. " +
            "Η αρχική εφαρμογή χρησιμοποιούσε Google Dialogflow."
}