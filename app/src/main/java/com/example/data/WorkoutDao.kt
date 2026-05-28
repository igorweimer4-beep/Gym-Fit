package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    // --- WORKOUT TEMPLATES ---
    @Query("SELECT * FROM workout_templates ORDER BY title ASC")
    fun getAllTemplates(): Flow<List<WorkoutTemplate>>

    @Query("SELECT * FROM workout_templates WHERE id = :id")
    suspend fun getTemplateById(id: Int): WorkoutTemplate?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: WorkoutTemplate): Long

    @Update
    suspend fun updateTemplate(template: WorkoutTemplate)

    @Query("DELETE FROM workout_templates WHERE id = :id")
    suspend fun deleteTemplateById(id: Int)

    // --- TEMPLATE EXERCISES ---
    @Query("SELECT * FROM template_exercises WHERE templateId = :templateId ORDER BY id ASC")
    fun getTemplateExercisesFlow(templateId: Int): Flow<List<TemplateExercise>>

    @Query("SELECT * FROM template_exercises WHERE templateId = :templateId ORDER BY id ASC")
    suspend fun getTemplateExercises(templateId: Int): List<TemplateExercise>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplateExercise(exercise: TemplateExercise): Long

    @Query("DELETE FROM template_exercises WHERE id = :id")
    suspend fun deleteTemplateExerciseById(id: Int)

    @Query("DELETE FROM template_exercises WHERE templateId = :templateId")
    suspend fun deleteExercisesByTemplateId(templateId: Int)

    // --- WORKOUT SESSIONS (LOGGED TRAININGS) ---
    @Query("SELECT * FROM workout_sessions ORDER BY dateMillis DESC")
    fun getAllSessions(): Flow<List<WorkoutSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutSession(session: WorkoutSession): Long

    @Query("DELETE FROM workout_sessions WHERE id = :sessionId")
    suspend fun deleteSessionById(sessionId: Int)

    // --- EXERCISE LOGS ---
    @Query("SELECT * FROM exercise_logs WHERE sessionId = :sessionId ORDER BY id ASC")
    suspend fun getExerciseLogsForSession(sessionId: Int): List<ExerciseLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseLog(log: ExerciseLog): Long

    // --- SET LOGS ---
    @Query("SELECT * FROM set_logs WHERE exerciseLogId = :exerciseLogId ORDER BY setIndex ASC")
    suspend fun getSetLogsForExercise(exerciseLogId: Int): List<SetLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetLog(log: SetLog): Long

    // Query for progressive charts: all set records for a given exercise name, to track strength increase
    @Query("""
        SELECT sl.*, ws.dateMillis as dateMillis 
        FROM set_logs sl 
        INNER JOIN exercise_logs el ON sl.exerciseLogId = el.id
        INNER JOIN workout_sessions ws ON el.sessionId = ws.id
        WHERE el.name = :exerciseName AND sl.completed = 1
        ORDER BY ws.dateMillis ASC
    """)
    suspend fun getCompletedSetsForExercise(exerciseName: String): List<SetWithDate>

    @Query("SELECT DISTINCT name FROM exercise_logs")
    fun getLoggedExerciseNamesFlow(): Flow<List<String>>

    // --- CALENDAR SCHEDULES ---
    @Query("SELECT * FROM calendar_schedules ORDER BY dateMillis ASC")
    fun getAllSchedules(): Flow<List<CalendarSchedule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: CalendarSchedule): Long

    @Query("DELETE FROM calendar_schedules WHERE id = :id")
    suspend fun deleteScheduleById(id: Int)

    @Query("UPDATE calendar_schedules SET completed = :completed WHERE id = :id")
    suspend fun updateScheduleCompletion(id: Int, completed: Boolean)

    // --- HYDRATION LOGS ---
    @Query("SELECT * FROM hydration_logs ORDER BY timestamp DESC")
    fun getAllHydrationLogs(): Flow<List<HydrationLog>>

    @Query("SELECT * FROM hydration_logs WHERE dateMillis = :dayStartMillis ORDER BY timestamp ASC")
    fun getHydrationLogsForDay(dayStartMillis: Long): Flow<List<HydrationLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHydrationLog(log: HydrationLog): Long

    @Query("DELETE FROM hydration_logs WHERE id = :id")
    suspend fun deleteHydrationLogById(id: Int)

    // --- WEIGHT RECORDS ---
    @Query("SELECT * FROM weight_records ORDER BY dateMillis ASC")
    fun getAllWeightRecords(): Flow<List<WeightRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightRecord(record: WeightRecord): Long

    @Query("DELETE FROM weight_records WHERE id = :id")
    suspend fun deleteWeightRecordById(id: Int)

    // --- WEARABLE LOGS ---
    @Query("SELECT * FROM wearable_logs ORDER BY dateMillis DESC")
    fun getAllWearableLogs(): Flow<List<WearableLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWearableLog(log: WearableLog): Long

    @Query("DELETE FROM wearable_logs WHERE id = :id")
    suspend fun deleteWearableLogById(id: Int)

    @Query("DELETE FROM wearable_logs")
    suspend fun clearAllWearableLogs()
}

data class SetWithDate(
    val id: Int,
    val exerciseLogId: Int,
    val setIndex: Int,
    val weightKg: Double,
    val reps: Int,
    val completed: Boolean,
    val dateMillis: Long
)
