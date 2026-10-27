package com.example.activitytrackerai

import com.google.firebase.database.FirebaseDatabase

object FirebaseUtils {
    // Δηλώνεις ΕΔΩ το instance με το σωστό URL ΜΙΑ φορά
    private val database by lazy {
        FirebaseDatabase.getInstance("https://activity-tracker-ai-default-rtdb.europe-west1.firebasedatabase.app")
    }

    // Αποθήκευση βημάτων για έναν χρήστη
    fun saveStepsForToday(userId: String, steps: Int) {
        val today = StepPrefsUtils.getToday()
        database.getReference("users")
            .child(userId)
            .child("steps")
            .child(today)
            .setValue(steps)
    }

    // Ανάγνωση βημάτων για μια μέρα
    fun getStepsForDay(userId: String, day: String, onResult: (Int?) -> Unit) {
        database.getReference("users")
            .child(userId)
            .child("steps")
            .child(day)
            .get()
            .addOnSuccessListener { snapshot ->
                val value = snapshot.value
                val steps = when (value) {
                    is Long -> value.toInt()
                    is Int -> value
                    is String -> value.toIntOrNull()
                    else -> null
                }
                onResult(steps)
            }
    }

    // Ανάγνωση βημάτων για εναν μηνα
    fun getStepsForMonth(userId: String, yearMonth: String, onResult: (Map<String, Int>) -> Unit) {
        val database = FirebaseDatabase.getInstance("https://activity-tracker-ai-default-rtdb.europe-west1.firebasedatabase.app")
        database.getReference("users")
            .child(userId)
            .child("steps")
            .get()
            .addOnSuccessListener { snapshot ->
                val allSteps = mutableMapOf<String, Int>()
                snapshot.children.forEach { child ->
                    val dateKey = child.key ?: return@forEach
                    if (dateKey.startsWith(yearMonth)) {
                        val value = child.value
                        val steps = when (value) {
                            is Long -> value.toInt()
                            is Int -> value
                            is String -> value.toIntOrNull()
                            else -> null
                        }
                        if (steps != null) {
                            allSteps[dateKey] = steps
                        }
                    }
                }
                onResult(allSteps)
            }
    }

    // Ανάγνωση συνολικων βηματων
    fun getAllSteps(userId: String, onResult: (Map<String, Int>) -> Unit) {
        database.getReference("users")
            .child(userId)
            .child("steps")
            .get()
            .addOnSuccessListener { snapshot ->
                val allSteps = mutableMapOf<String, Int>()
                snapshot.children.forEach { child ->
                    val dateKey = child.key ?: return@forEach
                    val value = child.value
                    val steps = when (value) {
                        is Long -> value.toInt()
                        is Int -> value
                        is String -> value.toIntOrNull()
                        else -> null
                    }
                    if (steps != null) {
                        allSteps[dateKey] = steps
                    }
                }
                onResult(allSteps)
            }
    }
}