package com.example.activitytrackerai

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

// Helper object για SharedPreferences
object GoalPrefsUtils {
    private const val PREFS_NAME = "goal_prefs"
    private const val KEY_DAILY_GOAL = "daily_goal"

    fun getDailyGoal(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_DAILY_GOAL, 8000) // Default: 8000 βήματα
    }

    fun setDailyGoal(context: Context, goal: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_DAILY_GOAL, goal).apply()
    }
}

@Composable
fun GoalsScreen() {
    val context = LocalContext.current

    // Φέρε τον τρέχοντα στόχο (από SharedPreferences)
    var dailyGoal by remember { mutableStateOf(GoalPrefsUtils.getDailyGoal(context)) }
    var inputGoal by remember { mutableStateOf(dailyGoal.toString()) }
    var showSavedMsg by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Στόχοι Βημάτων", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))
        Text("Τρέχων στόχος: $dailyGoal βήματα", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = inputGoal,
            onValueChange = { inputGoal = it.filter { c -> c.isDigit() } },
            label = { Text("Νέος στόχος (βήματα/μέρα)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            val newGoal = inputGoal.toIntOrNull()
            if (newGoal != null && newGoal > 0) {
                GoalPrefsUtils.setDailyGoal(context, newGoal)
                dailyGoal = newGoal
                showSavedMsg = true
            }
        }) {
            Text("Αποθήκευση")
        }
        if (showSavedMsg) {
            Spacer(Modifier.height(12.dp))
            Text("Ο στόχος αποθηκεύτηκε!", color = MaterialTheme.colorScheme.primary)
            // Μικρή καθυστέρηση για να εξαφανιστεί το μήνυμα
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(1200)
                showSavedMsg = false
            }
        }
    }
}