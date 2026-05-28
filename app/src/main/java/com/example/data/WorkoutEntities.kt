package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "workout_templates")
data class WorkoutTemplate(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val weekdayMask: Int = 0 // binary representation of weekdays planned: Mon=1, Tue=2, Wed=4 etc.
)

@Entity(
    tableName = "template_exercises",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutTemplate::class,
            parentColumns = ["id"],
            childColumns = ["templateId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["templateId"])]
)
data class TemplateExercise(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val templateId: Int,
    val name: String,
    val targetSets: Int = 3,
    val targetReps: String = "10",
    val defaultWeight: Double = 0.0,
    val restSeconds: Int = 60
)

@Entity(tableName = "workout_sessions")
data class WorkoutSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val templateId: Int? = null,
    val title: String,
    val dateMillis: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 0,
    val notes: String = ""
)

@Entity(
    tableName = "exercise_logs",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutSession::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sessionId"])]
)
data class ExerciseLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sessionId: Int,
    val name: String,
    val notes: String = ""
)

@Entity(
    tableName = "set_logs",
    foreignKeys = [
        ForeignKey(
            entity = ExerciseLog::class,
            parentColumns = ["id"],
            childColumns = ["exerciseLogId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["exerciseLogId"])]
)
data class SetLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val exerciseLogId: Int,
    val setIndex: Int,
    val weightKg: Double,
    val reps: Int,
    val completed: Boolean = true
)

@Entity(tableName = "calendar_schedules")
data class CalendarSchedule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateMillis: Long, // Start of that day
    val title: String,
    val templateId: Int? = null,
    val completed: Boolean = false
)

@Entity(tableName = "hydration_logs")
data class HydrationLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateMillis: Long, // Start of that day
    val volumeMl: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "weight_records")
data class WeightRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateMillis: Long,
    val weightKg: Double
)

@Entity(tableName = "wearable_logs")
data class WearableLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateMillis: Long,
    val stepsCount: Int = 0,
    val caloriesBurned: Double = 0.0,
    val avgHeartRate: Int = 0,
    val maxHeartRate: Int = 0,
    val activityDurationMinutes: Int = 0,
    val activityType: String = "", // e.g. "Corrida", "Crossfit", "Ciclismo"
    val sourceDevice: String = "" // e.g. "Wearable/AppleHealth/GoogleFit"
)

