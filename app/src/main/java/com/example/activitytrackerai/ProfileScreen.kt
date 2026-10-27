package com.example.activitytrackerai

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun ProfileScreen(
    onSignOut: () -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser
    val email = user?.email ?: "Άγνωστο Email"

    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Προφίλ", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        Text("Email: $email", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { showChangePasswordDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Αλλαγή Password")
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                onSignOut()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Αποσύνδεση")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { showConfirmDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Διαγραφή λογαριασμού")
        }
    }

    // Popup αλλαγής password
    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false }
        )
    }

    // Dialog επιβεβαίωσης διαγραφής
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Επιβεβαίωση διαγραφής") },
            text = { Text("Είσαι σίγουρος/η ότι θέλεις να διαγράψεις τον λογαριασμό σου; Αυτή η ενέργεια δεν αναιρείται!") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        isDeleting = true
                        val user = FirebaseAuth.getInstance().currentUser
                        val uid = user?.uid
                        if (user != null && uid != null) {
                            user.delete()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // Διαγραφή και από Database
                                        val dbRef = FirebaseDatabase.getInstance("https://activity-tracker-ai-default-rtdb.europe-west1.firebasedatabase.app")
                                            .getReference("users").child(uid)
                                        dbRef.removeValue()
                                        // Αποσύνδεση και επιστροφή στην αρχική/login
                                        FirebaseAuth.getInstance().signOut()
                                        onSignOut()
                                    } else {
                                        errorMessage = "Σφάλμα διαγραφής: ${task.exception?.localizedMessage}"
                                        isDeleting = false
                                    }
                                }
                        } else {
                            errorMessage = "Δεν βρέθηκε χρήστης για διαγραφή."
                            isDeleting = false
                        }
                    }
                ) { Text("Διαγραφή", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Άκυρο")
                }
            }
        )
    }

    if (isDeleting) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    errorMessage?.let {
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            title = { Text("Σφάλμα") },
            text = { Text(it) },
            confirmButton = {
                TextButton(onClick = { errorMessage = null }) {
                    Text("ΟΚ")
                }
            }
        )
    }
}

@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var passwordMessage by remember { mutableStateOf<String?>(null) }
    var isChangingPassword by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isChangingPassword) onDismiss() },
        title = { Text("Αλλαγή Password") },
        text = {
            Column {
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Τρέχον Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Νέο Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = confirmNewPassword,
                    onValueChange = { confirmNewPassword = it },
                    label = { Text("Επιβεβαίωση Νέου Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                passwordMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        it,
                        color = if (it.contains("επιτυχ", ignoreCase = true)) Color(0xFF388E3C) else Color.Red
                    )
                }
                if (isChangingPassword) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    passwordMessage = null
                    if (newPassword != confirmNewPassword) {
                        passwordMessage = "Τα νέα password δεν ταιριάζουν."
                    } else if (newPassword.length < 6) {
                        passwordMessage = "Το password πρέπει να έχει τουλάχιστον 6 χαρακτήρες."
                    } else {
                        isChangingPassword = true
                        changePassword(
                            currentPassword = currentPassword,
                            newPassword = newPassword
                        ) { success, errorMsg ->
                            isChangingPassword = false
                            passwordMessage = if (success) {
                                onDismiss()
                                "Το password άλλαξε επιτυχώς!"
                            } else {
                                errorMsg ?: "Αποτυχία αλλαγής password."
                            }
                        }
                    }
                },
                enabled = !isChangingPassword
            ) {
                Text("Αλλαγή")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { if (!isChangingPassword) onDismiss() }
            ) {
                Text("Άκυρο")
            }
        }
    )
}

fun changePassword(
    currentPassword: String,
    newPassword: String,
    onResult: (Boolean, String?) -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser
    if (user == null || user.email == null) {
        onResult(false, "Δεν βρέθηκε ο χρήστης.")
        return
    }
    val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
    user.reauthenticate(credential)
        .addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                user.updatePassword(newPassword)
                    .addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            onResult(true, null)
                        } else {
                            onResult(false, updateTask.exception?.localizedMessage)
                        }
                    }
            } else {
                onResult(false, "Λάθος τρέχον password.")
            }
        }
}