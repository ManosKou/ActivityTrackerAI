package com.example.activitytrackerai

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class StepCounterManager(
    private val context: Context,
    private val onStepsChanged: (Int) -> Unit
) : SensorEventListener {

    private var sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private var isRegistered = false

    fun start() {
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            isRegistered = true
        }
    }

    fun stop() {
        if (isRegistered) {
            sensorManager.unregisterListener(this)
            isRegistered = false
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val totalSteps = event.values[0].toInt()

            // Πάρε baseline και ημερομηνία
            val (baseline, storedDate) = StepPrefsUtils.getMidnightSteps(context)
            val today = StepPrefsUtils.getToday()

            // Αν άλλαξε η μέρα, κάνε reset baseline
            if (storedDate != today) {
                StepPrefsUtils.saveMidnightSteps(context, totalSteps)
                onStepsChanged(0)
            } else {
                val todaySteps = (totalSteps - baseline).coerceAtLeast(0)
                onStepsChanged(todaySteps)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}