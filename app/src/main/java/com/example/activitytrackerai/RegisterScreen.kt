package com.example.activitytrackerai

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Εγγραφή", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = email, onValueChange = { email = it },
            label = { Text("Email") }
        )
        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text("Κωδικός") },
            visualTransformation = PasswordVisualTransformation()
        )
        Button(onClick = {
            AuthUtils.signUp(email, password) { success, err ->
                if (success) onRegisterSuccess() else error = err
            }
        }) {
            Text("Εγγραφή")
        }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onNavigateToLogin) {
            Text("Έχεις ήδη λογαριασμό; Σύνδεση")
        }
    }
}