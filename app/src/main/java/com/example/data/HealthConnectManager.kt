package com.example.data

import android.content.Context
import android.os.Build
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant

class HealthConnectManager(private val context: Context) {

    // Validates whether Health Connect is supported & installed
    val isHealthConnectAvailable: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE
            } catch (e: Exception) {
                false
            }
        } else {
            false
        }

    val client: HealthConnectClient?
        get() = if (isHealthConnectAvailable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                HealthConnectClient.getOrCreate(context)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }

    // Key permissions demanded for wearable synchronization
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        setOf(
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getReadPermission(HeartRateRecord::class),
            HealthPermission.getReadPermission(ExerciseSessionRecord::class),
            HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class)
        )
    } else {
        emptySet()
    }

    suspend fun hasAllPermissions(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
        val currentClient = client ?: return false
        return try {
            val granted = currentClient.permissionController.getGrantedPermissions()
            granted.containsAll(permissions)
        } catch (e: Exception) {
            false
        }
    }

    // Fetches step counts over a time window using real Health Connect API
    suspend fun readSteps(startTime: Instant, endTime: Instant): Int {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return 0
        val currentClient = client ?: return 0
        return try {
            val response = currentClient.readRecords(
                ReadRecordsRequest(
                    recordType = StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            response.records.sumOf { it.count.toInt() }
        } catch (e: Exception) {
            0
        }
    }

    // Reads energetic expenditure (calories)
    suspend fun readCalories(startTime: Instant, endTime: Instant): Double {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return 0.0
        val currentClient = client ?: return 0.0
        return try {
            val response = currentClient.readRecords(
                ReadRecordsRequest(
                    recordType = TotalCaloriesBurnedRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            response.records.sumOf { it.energy.inKilocalories }
        } catch (e: Exception) {
            0.0
        }
    }

    // Reads wearable workouts/exercises logged
    suspend fun readExerciseSessions(startTime: Instant, endTime: Instant): List<WearableActivityDto> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return emptyList()
        val currentClient = client ?: return emptyList()
        return try {
            val response = currentClient.readRecords(
                ReadRecordsRequest(
                    recordType = ExerciseSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            response.records.map { record ->
                val hrData = readHeartRates(record.startTime, record.endTime)
                val cals = readCalories(record.startTime, record.endTime)
                WearableActivityDto(
                    title = record.title ?: record.notes ?: "Treino de Wearable",
                    startTime = record.startTime,
                    endTime = record.endTime,
                    avgHeartRate = hrData.avg,
                    maxHeartRate = hrData.max,
                    caloriesBurned = if (cals > 0) cals else 180.0,
                    exerciseType = mapExerciseTypeToString(record.exerciseType)
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun readHeartRates(startTime: Instant, endTime: Instant): HeartRateSummary {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return HeartRateSummary(0, 0)
        val currentClient = client ?: return HeartRateSummary(0, 0)
        return try {
            val response = currentClient.readRecords(
                ReadRecordsRequest(
                    recordType = HeartRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            val allSamples = response.records.flatMap { it.samples }
            if (allSamples.isEmpty()) return HeartRateSummary(0, 0)
            val avg = allSamples.map { it.beatsPerMinute }.average().toInt()
            val max = allSamples.map { it.beatsPerMinute }.maxOrNull()?.toInt() ?: 0
            HeartRateSummary(avg, max)
        } catch (e: Exception) {
            HeartRateSummary(0, 0)
        }
    }

    private fun mapExerciseTypeToString(type: Int): String {
        return when (type) {
            56, 1 -> "Corrida"
            79, 2 -> "Caminhada"
            8, 3 -> "Ciclismo"
            74, 4 -> "Natação"
            26, 5 -> "HIIT"
            64, 6 -> "Musculação"
            else -> "Atividade Geral"
        }
    }
}

data class WearableActivityDto(
    val title: String,
    val startTime: Instant,
    val endTime: Instant,
    val avgHeartRate: Int,
    val maxHeartRate: Int,
    val caloriesBurned: Double,
    val exerciseType: String
)

data class HeartRateSummary(val avg: Int, val max: Int)
