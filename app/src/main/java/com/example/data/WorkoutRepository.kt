package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WorkoutRepository(private val workoutDao: WorkoutDao) {

    // --- TEMPLATES ---
    val allTemplates: Flow<List<WorkoutTemplate>> = workoutDao.getAllTemplates()

    suspend fun getTemplateById(id: Int): WorkoutTemplate? = workoutDao.getTemplateById(id)

    suspend fun getTemplateExercises(templateId: Int): List<TemplateExercise> {
        return workoutDao.getTemplateExercises(templateId)
    }

    fun getTemplateExercisesFlow(templateId: Int): Flow<List<TemplateExercise>> {
        return workoutDao.getTemplateExercisesFlow(templateId)
    }

    suspend fun saveTemplate(template: WorkoutTemplate, exercises: List<TemplateExercise>): Int {
        return withContext(Dispatchers.IO) {
            val templateId = if (template.id == 0) {
                workoutDao.insertTemplate(template).toInt()
            } else {
                workoutDao.updateTemplate(template)
                template.id
            }

            // Simple update strategy: clear old template exercises, add new ones
            if (template.id != 0) {
                workoutDao.deleteExercisesByTemplateId(template.id)
            }

            for (ex in exercises) {
                workoutDao.insertTemplateExercise(ex.copy(templateId = templateId))
            }
            templateId
        }
    }

    suspend fun deleteTemplate(id: Int) {
        withContext(Dispatchers.IO) {
            workoutDao.deleteTemplateById(id)
        }
    }

    // --- LOGS AND SESSIONS ---
    val allSessions: Flow<List<WorkoutSession>> = workoutDao.getAllSessions()

    suspend fun saveCompletedSession(
        session: WorkoutSession,
        exercises: List<Pair<String, List<SetLog>>>
    ) {
        withContext(Dispatchers.IO) {
            val sessionId = workoutDao.insertWorkoutSession(session).toInt()
            for (exPair in exercises) {
                val exerciseName = exPair.first
                val sets = exPair.second
                if (sets.isEmpty()) continue

                val exerciseLogId = workoutDao.insertExerciseLog(
                    ExerciseLog(sessionId = sessionId, name = exerciseName)
                ).toInt()

                for (set in sets) {
                    workoutDao.insertSetLog(set.copy(exerciseLogId = exerciseLogId))
                }
            }
        }
    }

    suspend fun loadFullSessionDetails(sessionId: Int): List<Pair<ExerciseLog, List<SetLog>>> {
        return withContext(Dispatchers.IO) {
            val exercises = workoutDao.getExerciseLogsForSession(sessionId)
            exercises.map { exercise ->
                val sets = workoutDao.getSetLogsForExercise(exercise.id)
                Pair(exercise, sets)
            }
        }
    }

    suspend fun deleteSession(sessionId: Int) {
        withContext(Dispatchers.IO) {
            workoutDao.deleteSessionById(sessionId)
        }
    }

    fun getLoggedExerciseNamesFlow(): Flow<List<String>> = workoutDao.getLoggedExerciseNamesFlow()

    suspend fun getExerciseProgress(exerciseName: String): List<SetWithDate> {
        return withContext(Dispatchers.IO) {
            workoutDao.getCompletedSetsForExercise(exerciseName)
        }
    }

    // --- CALENDAR SCHEDULES ---
    val allSchedules: Flow<List<CalendarSchedule>> = workoutDao.getAllSchedules()

    suspend fun addSchedule(schedule: CalendarSchedule) {
        withContext(Dispatchers.IO) {
            workoutDao.insertSchedule(schedule)
        }
    }

    suspend fun deleteSchedule(id: Int) {
        withContext(Dispatchers.IO) {
            workoutDao.deleteScheduleById(id)
        }
    }

    suspend fun toggleScheduleCompletion(id: Int, completed: Boolean) {
        withContext(Dispatchers.IO) {
            workoutDao.updateScheduleCompletion(id, completed)
        }
    }

    // --- HYDRATION ---
    val allHydrationLogs: Flow<List<HydrationLog>> = workoutDao.getAllHydrationLogs()

    fun getHydrationLogsForDay(dayStartMillis: Long): Flow<List<HydrationLog>> {
        return workoutDao.getHydrationLogsForDay(dayStartMillis)
    }

    suspend fun addHydration(volumeMl: Int, dateMillis: Long) {
        withContext(Dispatchers.IO) {
            workoutDao.insertHydrationLog(HydrationLog(dateMillis = dateMillis, volumeMl = volumeMl))
        }
    }

    suspend fun deleteHydration(id: Int) {
        withContext(Dispatchers.IO) {
            workoutDao.deleteHydrationLogById(id)
        }
    }

    // --- WEIGHTS ---
    val allWeightRecords: Flow<List<WeightRecord>> = workoutDao.getAllWeightRecords()

    suspend fun addWeightRecord(weightKg: Double, dateMillis: Long) {
        withContext(Dispatchers.IO) {
            workoutDao.insertWeightRecord(WeightRecord(dateMillis = dateMillis, weightKg = weightKg))
        }
    }

    suspend fun deleteWeightRecord(id: Int) {
        withContext(Dispatchers.IO) {
            workoutDao.deleteWeightRecordById(id)
        }
    }

    // --- WEARABLE HEALTH INTEGRATION ---
    val allWearableLogs: Flow<List<WearableLog>> = workoutDao.getAllWearableLogs()

    suspend fun addWearableLog(log: WearableLog) {
        withContext(Dispatchers.IO) {
            workoutDao.insertWearableLog(log)
        }
    }

    suspend fun deleteWearableLog(id: Int) {
        withContext(Dispatchers.IO) {
            workoutDao.deleteWearableLogById(id)
        }
    }

    suspend fun clearAllWearableLogs() {
        withContext(Dispatchers.IO) {
            workoutDao.clearAllWearableLogs()
        }
    }
}
