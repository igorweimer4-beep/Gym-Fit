package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.theme.*
import com.example.ui.util.PdfExporter
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GymAppScreen(viewModel: GymViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }

    // Floating dialog & state forms
    var showAddTemplateDialog by remember { mutableStateOf(false) }
    var showScheduleDialog by remember { mutableStateOf(false) }

    // Active workout state
    val activeSession by viewModel.activeSession.collectAsStateWithLifecycle()
    val activeExercises by viewModel.activeExercises.collectAsStateWithLifecycle()
    val activeDuration by viewModel.activeDurationSeconds.collectAsStateWithLifecycle()

    // Timer state
    val isRestActive by viewModel.isRestTimerActive.collectAsStateWithLifecycle()
    val restTimeRemaining by viewModel.restTimeRemaining.collectAsStateWithLifecycle()
    val restTimeInitial by viewModel.restTimeInitial.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = AthleticVolt,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "FITLOG",
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp,
                            color = Color.White
                        )
                    }
                },
                actions = {
                    // Cloud status indicator
                    Row(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E2A22))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CloudDone,
                            contentDescription = "Cloud Sincronizado",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "OFFLINE OK & NUVEM SÍNCRO",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF81C784)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CarbonMidnight
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = CarbonSurface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Painel") },
                    label = { Text("Painel") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CarbonMidnight,
                        selectedTextColor = AthleticVolt,
                        indicatorColor = AthleticVolt,
                        unselectedIconColor = SoftSlateText,
                        unselectedTextColor = SoftSlateText
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.FitnessCenter, contentDescription = "Meus Treinos") },
                    label = { Text("Treinar") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CarbonMidnight,
                        selectedTextColor = AthleticVolt,
                        indicatorColor = AthleticVolt,
                        unselectedIconColor = SoftSlateText,
                        unselectedTextColor = SoftSlateText
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Agenda") },
                    label = { Text("Agenda") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CarbonMidnight,
                        selectedTextColor = AthleticVolt,
                        indicatorColor = AthleticVolt,
                        unselectedIconColor = SoftSlateText,
                        unselectedTextColor = SoftSlateText
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Insights, contentDescription = "Análises") },
                    label = { Text("Análises") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CarbonMidnight,
                        selectedTextColor = AthleticVolt,
                        indicatorColor = AthleticVolt,
                        unselectedIconColor = SoftSlateText,
                        unselectedTextColor = SoftSlateText
                    )
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(CarbonMidnight)
        ) {
            // Live rest timer warning card if active across any tab
            if (isRestActive) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CarbonCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .border(1.dp, NeonCyberIce, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00363A))
                            ) {
                                CircularProgressIndicator(
                                    progress = { restTimeRemaining.toFloat() / restTimeInitial.toFloat() },
                                    color = NeonCyberIce,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Icon(
                                    imageVector = Icons.Outlined.Timer,
                                    contentDescription = null,
                                    tint = NeonCyberIce,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Tempo de Descanso Ativo",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SoftSlateText
                                )
                                Text(
                                    text = "Restam $restTimeRemaining segundos",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }
                        Button(
                            onClick = { viewModel.stopRestTimer() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Pular", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            // Tab navigation
            when (selectedTab) {
                0 -> DashboardSubscreen(viewModel, onNavigateToTrain = { selectedTab = 1 })
                1 -> WorkoutsSubscreen(viewModel, onShowAddTemplate = { showAddTemplateDialog = true })
                2 -> AgendaSubscreen(viewModel, onShowScheduleDetails = { showScheduleDialog = true })
                3 -> AnalyticsSubscreen(viewModel)
            }
        }
    }

    // Modal view triggers
    if (showAddTemplateDialog) {
        TemplateCreatorDialog(
            onDismiss = { showAddTemplateDialog = false },
            onSave = { title, desc, exList ->
                viewModel.createNewTemplate(title, desc, exList)
                showAddTemplateDialog = false
                Toast.makeText(context, "Treino montado e salvo offline!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showScheduleDialog) {
        SchedulerDialog(
            viewModel = viewModel,
            onDismiss = { showScheduleDialog = false },
            onSave = { title, dateMills, templateId ->
                viewModel.planFutureWorkout(title, dateMills, templateId)
                showScheduleDialog = false
                Toast.makeText(context, "Treino agendado com sucesso!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

// ==========================================
// 1. DASHBOARD SUBSCREEN (PAINEL & AI COACH)
// ==========================================
@Composable
fun DashboardSubscreen(viewModel: GymViewModel, onNavigateToTrain: () -> Unit) {
    val context = LocalContext.current
    val totalWater by viewModel.totalWaterMlToday.collectAsStateWithLifecycle()
    val weight by viewModel.currentWeightKg.collectAsStateWithLifecycle()
    val aiNotes by viewModel.aiCoachNotes.collectAsStateWithLifecycle()
    val sessions by viewModel.allSessions.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()

    val wearableLogs by viewModel.allWearableLogs.collectAsStateWithLifecycle()
    val isSyncingWearable by viewModel.isSyncingWearable.collectAsStateWithLifecycle()
    val wearableSyncStatus by viewModel.wearableSyncStatus.collectAsStateWithLifecycle()

    var weightInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome section with quick motivators
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = CarbonSurface),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Seja bem-vindo de volta!",
                            fontSize = 14.sp,
                            color = SoftSlateText
                        )
                        Text(
                            text = "igorweimer99@gmail.com",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.NotificationsActive, contentDescription = null, tint = AthleticVolt, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Próximo treino semanal planejado na agenda", fontSize = 11.sp, color = AthleticVolt)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(55.dp)
                            .clip(CircleShape)
                            .background(AthleticVolt)
                            .clickable { viewModel.requestAiCoachingInsight() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Coach IA",
                            tint = CarbonMidnight,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        // --- WEARABLE HEALTH INTEGRATION SYSTEM HUB ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CarbonSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Watch,
                                contentDescription = null,
                                tint = AthleticVolt,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CONEXÃO COM WEARABLES",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                        }

                        // Connected status badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (wearableLogs.isNotEmpty()) Color(0xFF1B2E1C) else Color(0xFF261D1D))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (wearableLogs.isNotEmpty()) "ATIVO" else "PENDENTE",
                                color = if (wearableLogs.isNotEmpty()) Color(0xFF81C784) else GymWarning,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Importe treinos de força, aeróbica, frequência cardíaca, passos e queima calórica coletados por Apple Health, Fitbit, Garmin ou Galaxy Watch via Google Health Connect.",
                        fontSize = 11.sp,
                        color = SoftSlateText,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Synchronize and Simulation Commands
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.checkHealthConnectPermissionsAndSync() },
                            enabled = !isSyncingWearable,
                            colors = ButtonDefaults.buttonColors(containerColor = AthleticVolt),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isSyncingWearable) {
                                    CircularProgressIndicator(
                                        color = CarbonMidnight,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                } else {
                                    Icon(Icons.Default.Sync, contentDescription = null, tint = CarbonMidnight, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text("Sincronizar", color = CarbonMidnight, fontWeight = FontWeight.Black, fontSize = 11.sp)
                            }
                        }

                        Button(
                            onClick = { viewModel.syncWearableSimulation() },
                            enabled = !isSyncingWearable,
                            colors = ButtonDefaults.buttonColors(containerColor = CarbonCard),
                            border = BorderStroke(1.dp, NeonCyberIce),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.DeviceHub, contentDescription = null, tint = NeonCyberIce, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Simulador", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        if (wearableLogs.isNotEmpty()) {
                            IconButton(
                                onClick = { viewModel.clearWearableData() },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CarbonCard)
                                    .size(40.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Limpar", tint = GymWarning)
                            }
                        }
                    }

                    // Logging feedback state
                    wearableSyncStatus?.let { status ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1E2226))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "Status: $status",
                                color = NeonCyberIce,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Display list of imported activity summaries
                    if (wearableLogs.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "METRÍCULAS DE WEARABLES IMPORTADAS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = SoftSlateText,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        wearableLogs.take(3).forEach { log ->
                            val activityDate = SimpleDateFormat("dd/MM 'às' HH:mm", Locale.getDefault()).format(Date(log.dateMillis))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CarbonCard),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF1E2A22)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DirectionsRun,
                                            contentDescription = null,
                                            tint = Color(0xFF4CAF50),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = log.activityType.ifEmpty { "Atividade de Saúde" },
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = log.sourceDevice,
                                                color = AthleticVolt,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Text("🔥 ${log.caloriesBurned.toInt()} kcal", color = SoftSlateText, fontSize = 11.sp)
                                            Text("⏱️ ${log.activityDurationMinutes} min", color = SoftSlateText, fontSize = 11.sp)
                                            Text("❤️ HR(Média): ${log.avgHeartRate} bpm", color = SoftSlateText, fontSize = 11.sp)
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("🐾 Passos: ${log.stepsCount}", color = SoftSlateText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Text(text = activityDate, color = SoftSlateText, fontSize = 9.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Live Exercise banner tracker (if none, prompt)
        item {
            val activeSession by viewModel.activeSession.collectAsStateWithLifecycle()
            if (activeSession != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2226)),
                    border = BorderStroke(2.dp, AthleticVolt),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("TREINO ATIVO AGORA!", color = AthleticVolt, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = AthleticVolt)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(activeSession!!.title, fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color.White)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onNavigateToTrain,
                            colors = ButtonDefaults.buttonColors(containerColor = AthleticVolt),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Retomar Planilha de Registro", color = CarbonMidnight, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 2. QUICK HYDRATION WORKOUT INTUITIVE
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CarbonSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.WaterDrop, contentDescription = null, tint = NeonCyberIce)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Controle de Hidratação", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        }
                        Text(
                            text = "$totalWater / 2500 mL",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = NeonCyberIce
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { (totalWater.toFloat() / 2500f).coerceAtMost(1f) },
                        color = NeonCyberIce,
                        trackColor = CarbonCard,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.addWaterMl(250) },
                            colors = ButtonDefaults.buttonColors(containerColor = CarbonCard),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = NeonCyberIce)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("250ml", color = Color.White, fontSize = 12.sp)
                            }
                        }
                        Button(
                            onClick = { viewModel.addWaterMl(500) },
                            colors = ButtonDefaults.buttonColors(containerColor = CarbonCard),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = NeonCyberIce)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("500ml", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "* Notificação inteligente: Beba água de hora em hora para maximizar a recomposição de ATP no treino.",
                        fontSize = 9.5.sp,
                        color = SoftSlateText,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }

        // 3. PHYSICAL BODY WEIGHT SETTER
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CarbonSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Metrificação de Peso", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                        Text(
                            text = if (weight > 0.0) "Registrado: $weight kg" else "Nenhum peso adicionado",
                            fontSize = 12.sp,
                            color = SoftSlateText
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = weightInput,
                            onValueChange = { weightInput = it },
                            placeholder = { Text("Kg", fontSize = 12.sp, color = SoftSlateText) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = AthleticVolt,
                                unfocusedBorderColor = CarbonCard
                            ),
                            modifier = Modifier.width(70.dp).height(50.dp)
                        )
                        Button(
                            onClick = {
                                val wVal = weightInput.toDoubleOrNull()
                                if (wVal != null) {
                                    viewModel.saveWeight(wVal)
                                    weightInput = ""
                                    Toast.makeText(context, "Peso registrado!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AthleticVolt),
                            modifier = Modifier.testTag("save_weight_button")
                        ) {
                            Text("Salvar", color = CarbonMidnight, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // 4. THE PERSONAL COACH IA ADVISORY PANEL
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CarbonCard),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, CustomBorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = AthleticVolt,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Coach Motivacional IA", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
                        }
                        if (isAiLoading) {
                            CircularProgressIndicator(color = AthleticVolt, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = aiNotes,
                        fontSize = 12.5.sp,
                        color = Color.White,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.requestAiCoachingInsight() },
                            colors = ButtonDefaults.buttonColors(containerColor = CarbonSurface),
                            border = BorderStroke(1.dp, AthleticVolt),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Mensagem IA", color = AthleticVolt, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                viewModel.generateEvaluatedSummary { reports ->
                                    Toast.makeText(context, "Análise gerada!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CarbonSurface),
                            border = BorderStroke(1.dp, NeonCyberIce),
                            modifier = Modifier.weight(1.3f)
                        ) {
                            Text("Análise Mensal IA", color = NeonCyberIce, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 5. EXPORT AND SHARE PDF PERFORMANCE
        item {
            Button(
                onClick = {
                    // Export PDF with stats:
                    PdfExporter.exportToPdf(
                        context = context,
                        userEmail = "igorweimer99@gmail.com",
                        sessions = sessions,
                        totalWaterMl = totalWater,
                        currentWeightKg = weight,
                        aiCoachText = aiNotes,
                        onComplete = { pdfFile ->
                            if (pdfFile != null) {
                                PdfExporter.sharePdf(context, pdfFile)
                            } else {
                                Toast.makeText(context, "Erro ao gerar PDF", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = AthleticVolt),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .height(55.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Share, contentDescription = null, tint = CarbonMidnight)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "EXPORTAR RELATÓRIO PDF",
                        color = CarbonMidnight,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

// ==========================================
// 2. WORKOUTS SUBSCREEN (MEUS TREINOS & LIVE)
// ==========================================
@Composable
fun WorkoutsSubscreen(viewModel: GymViewModel, onShowAddTemplate: () -> Unit) {
    val templates by viewModel.allTemplates.collectAsStateWithLifecycle()
    val activeSession by viewModel.activeSession.collectAsStateWithLifecycle()
    val activeExercises by viewModel.activeExercises.collectAsStateWithLifecycle()
    val activeDuration by viewModel.activeDurationSeconds.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var notesInput by remember { mutableStateOf("") }
    var extraExerciseName by remember { mutableStateOf("") }

    if (activeSession != null) {
        // --- WORKOUT IN-PROGRESS LIVE ENGINE ---
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = CarbonSurface),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("TREINO ATIVO", fontSize = 11.sp, color = AthleticVolt, fontWeight = FontWeight.Bold)
                                Text(activeSession!!.title, fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color.White)
                            }
                            // Stopwatch display
                            val secs = activeDuration % 60
                            val mins = (activeDuration / 60) % 60
                            val hrs = activeDuration / 3600
                            val formattedTime = String.format("%02d:%02d:%02d", hrs, mins, secs)

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CarbonCard)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Timer, contentDescription = null, tint = AthleticVolt, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = formattedTime,
                                        color = AthleticVolt,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // List of exercises in progress
            itemsIndexed(activeExercises) { exIndex, pair ->
                val exName = pair.first
                val sets = pair.second

                Card(
                    colors = CardDefaults.cardColors(containerColor = CarbonSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "${exIndex + 1}. $exName", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                            IconButton(onClick = { viewModel.addSetToActiveExercise(exIndex) }) {
                                Icon(Icons.Default.AddCircle, contentDescription = "Mais set", tint = AthleticVolt)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        // Column headers
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("SÉRIE", modifier = Modifier.weight(1f), fontSize = 11.sp, color = SoftSlateText, fontWeight = FontWeight.Bold)
                            Text("CARGA (KG)", modifier = Modifier.weight(2f), fontSize = 11.sp, color = SoftSlateText, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            Text("REPS", modifier = Modifier.weight(2f), fontSize = 11.sp, color = SoftSlateText, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            Text("FEITO", modifier = Modifier.weight(1f), fontSize = 11.sp, color = SoftSlateText, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        }
                        Spacer(modifier = Modifier.height(4.dp))

                        // Sets editor lines
                        sets.forEachIndexed { setIdx, setLog ->
                            var weightStr by remember(setLog.weightKg) { mutableStateOf(setLog.weightKg.toString()) }
                            var repsStr by remember(setLog.reps) { mutableStateOf(setLog.reps.toString()) }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Set label + delete
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Deletar Set",
                                        tint = GymWarning,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clickable { viewModel.removeSetFromActiveExercise(exIndex, setIdx) }
                                    )
                                    Text(
                                        text = "#${setLog.setIndex}",
                                        fontSize = 13.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // Weight input
                                OutlinedTextField(
                                    value = weightStr,
                                    onValueChange = {
                                        weightStr = it
                                        val doubleVal = it.toDoubleOrNull() ?: setLog.weightKg
                                        viewModel.updateActiveSet(exIndex, setLog.setIndex, doubleVal, setLog.reps, setLog.completed)
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = AthleticVolt,
                                        unfocusedBorderColor = CarbonCard
                                    ),
                                    modifier = Modifier
                                        .weight(2f)
                                        .padding(horizontal = 4.dp)
                                        .height(46.dp),
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, textAlign = TextAlign.Center)
                                )

                                // Reps input
                                OutlinedTextField(
                                    value = repsStr,
                                    onValueChange = {
                                        repsStr = it
                                        val intVal = it.toIntOrNull() ?: setLog.reps
                                        viewModel.updateActiveSet(exIndex, setLog.setIndex, setLog.weightKg, intVal, setLog.completed)
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = AthleticVolt,
                                        unfocusedBorderColor = CarbonCard
                                    ),
                                    modifier = Modifier
                                        .weight(2f)
                                        .padding(horizontal = 4.dp)
                                        .height(46.dp),
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, textAlign = TextAlign.Center)
                                )

                                // Done checkbox
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                ) {
                                    FilledIconToggleButton(
                                        checked = setLog.completed,
                                        onCheckedChange = { isChecked ->
                                            viewModel.updateActiveSet(
                                                exIndex,
                                                setLog.setIndex,
                                                setLog.weightKg,
                                                setLog.reps,
                                                isChecked
                                            )
                                        },
                                        colors = IconButtonDefaults.filledIconToggleButtonColors(
                                            containerColor = CarbonCard,
                                            checkedContainerColor = AthleticVolt,
                                            contentColor = SoftSlateText,
                                            checkedContentColor = CarbonMidnight
                                        ),
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (setLog.completed) Icons.Default.Check else Icons.Default.Clear,
                                            contentDescription = "Check",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Quick add extra exercise panel
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CarbonCard),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Adicionar Exercício Extra", fontSize = 13.sp, color = SoftSlateText, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = extraExerciseName,
                                onValueChange = { extraExerciseName = it },
                                placeholder = { Text("Nome do Exercício...", fontSize = 12.sp, color = SoftSlateText) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = AthleticVolt,
                                    unfocusedBorderColor = CarbonSurface
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = {
                                    if (extraExerciseName.isNotBlank()) {
                                        viewModel.addExerciseToActiveWorkout(extraExerciseName)
                                        extraExerciseName = ""
                                        Toast.makeText(context, "Exercício adicionado!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AthleticVolt)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = CarbonMidnight)
                            }
                        }
                    }
                }
            }

            // Form closure field
            item {
                OutlinedTextField(
                    value = notesInput,
                    onValueChange = { notesInput = it },
                    label = { Text("Anotações / Como se sentiu no treino...", color = SoftSlateText) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = AthleticVolt,
                        unfocusedBorderColor = CarbonCard
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }

            // Finish/Cancel buttons
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 30.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { viewModel.cancelActiveWorkout() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancelar", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Button(
                        onClick = {
                            viewModel.finishActiveWorkout(notesInput)
                            notesInput = ""
                            Toast.makeText(context, "Parabéns! Treino finalizado e salvo offline!", Toast.LENGTH_LONG).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AthleticVolt),
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Concluir Treino", fontWeight = FontWeight.Black, color = CarbonMidnight)
                    }
                }
            }
        }
    } else {
        // --- CHOOSE TEMPLATE SCREEN ---
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                // Workout control header actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onShowAddTemplate,
                        colors = ButtonDefaults.buttonColors(containerColor = CarbonCard),
                        border = BorderStroke(1.5.dp, AthleticVolt),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f).height(55.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = AthleticVolt)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Montar Treino", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Button(
                        onClick = { viewModel.startCustomWorkout() },
                        colors = ButtonDefaults.buttonColors(containerColor = AthleticVolt),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f).height(55.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = CarbonMidnight)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Treino Livre", color = CarbonMidnight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // List header
            item {
                Text(
                    text = "MINHAS PLANILHAS SALVAS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = SoftSlateText,
                    letterSpacing = 0.8.sp
                )
            }

            if (templates.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = CarbonCard, modifier = Modifier.size(65.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Nenhuma planilha montada ainda.\nToque em 'Montar Treino' para criar!",
                            color = SoftSlateText,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(templates) { template ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CarbonSurface),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.startWorkoutFromTemplate(template) }
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = template.title,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 17.sp,
                                    color = Color.White
                                )
                                Row {
                                    IconButton(
                                        onClick = { viewModel.deleteTemplate(template.id) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Deletar", tint = GymWarning, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = template.description,
                                fontSize = 12.sp,
                                color = SoftSlateText,
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CarbonCard)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Estímulo Forte", color = NeonCyberIce, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }

                                Text(
                                    text = "Toque para Iniciar Treino ›",
                                    fontSize = 11.sp,
                                    color = AthleticVolt,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. AGENDA SUBSCREEN (CALENDÁRIO & ROTINA)
// ==========================================
@Composable
fun AgendaSubscreen(viewModel: GymViewModel, onShowScheduleDetails: () -> Unit) {
    val schedules by viewModel.allSchedules.collectAsStateWithLifecycle()
    val templates by viewModel.allTemplates.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Horizontal week calendar view
    val calendarDays = remember {
        val days = mutableListOf<Date>()
        val cal = Calendar.getInstance()
        // Start from Monday of current week
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        for (i in 0..6) {
            days.add(cal.time)
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        days
    }

    var selectedDay by remember { mutableStateOf(calendarDays.firstOrNull() ?: Date()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Custom horizontal calendar week selector
        Text(
            text = "MINHA ROTINA SEMANAL",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = SoftSlateText
        )
        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(calendarDays) { date ->
                val dayFormat = SimpleDateFormat("EE", Locale("pt", "BR")).format(date).uppercase()
                val numFormat = SimpleDateFormat("dd", Locale.getDefault()).format(date)
                val isSelected = SimpleDateFormat("dd-MM", Locale.getDefault()).format(date) == 
                                 SimpleDateFormat("dd-MM", Locale.getDefault()).format(selectedDay)

                Column(
                    modifier = Modifier
                        .width(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) AthleticVolt else CarbonSurface)
                        .clickable { selectedDay = date }
                        .padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = dayFormat.take(3),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) CarbonMidnight else SoftSlateText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = numFormat,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isSelected) CarbonMidnight else Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Schedule Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ORGANIZAÇÃO DO DIA SELECIONADO",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = SoftSlateText
            )

            Button(
                onClick = onShowScheduleDetails,
                colors = ButtonDefaults.buttonColors(containerColor = CarbonCard),
                border = BorderStroke(1.dp, AthleticVolt),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Agendar +\n(Notificações)", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Filter schedules for the selected calendar day
        val dayStartMills = viewModel.getStartOfDayMillis(selectedDay.time)
        val filteredSchedules = schedules.filter {
            viewModel.getStartOfDayMillis(it.dateMillis) == dayStartMills
        }

        if (filteredSchedules.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = CarbonCard, modifier = Modifier.size(50.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Nenhum compromisso planejado.\nCadastre um compromisso de saúde!",
                    color = SoftSlateText,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredSchedules) { item ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (item.completed) Color(0xFF1B2E1C) else CarbonSurface
                        ),
                        border = BorderStroke(1.dp, if (item.completed) Color(0xFF2E7D32) else CarbonCard),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { viewModel.toggleScheduleCompletion(item.id, !item.completed) }
                            ) {
                                Icon(
                                    imageVector = if (item.completed) Icons.Default.CheckCircle else Icons.Default.Circle,
                                    contentDescription = "Concluir plano",
                                    tint = if (item.completed) Color(0xFF4CAF50) else SoftSlateText
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (item.completed) SoftSlateText else Color.White
                                )
                                Text(
                                    text = if (item.completed) "Treino Concluído" else "Hoje, Lembrete agendado",
                                    fontSize = 11.sp,
                                    color = if (item.completed) Color(0xFF81C784) else SoftSlateText
                                )
                            }

                            IconButton(
                                onClick = { viewModel.deletePlannedWorkout(item.id) }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Deletar", tint = GymWarning)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. ANALYTICS SUBSCREEN (PROGRESS DETAILS)
// ==========================================
@Composable
fun AnalyticsSubscreen(viewModel: GymViewModel) {
    val weightRecords by viewModel.allWeightRecords.collectAsStateWithLifecycle()
    val sessions by viewModel.allSessions.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "GRÁFICOS METRIFICADOS DE EVOLUÇÃO",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = SoftSlateText
            )
        }

        // --- CHART 1: CUSTOM DRAWN WEIGTH EVOLUTION LINE GRAPH ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CarbonSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Evolução de Peso Corporal (Kg)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(12.dp))

                    if (weightRecords.size < 2) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .background(CarbonCard, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Registre seu peso ao longo dos dias para\nver a curva de oscilação física.",
                                color = SoftSlateText,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        // Drawing line graph on Canvas
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                        ) {
                            val maxW = (weightRecords.maxOf { it.weightKg } + 5).toFloat()
                            val minW = (weightRecords.minOf { it.weightKg } - 5).toFloat()
                            val diffW = maxW - minW

                            val width = size.width
                            val height = size.height

                            // Draw baseline grid lines
                            drawLine(Color(0xFF2C3E50), Offset(0f, height * 0.25f), Offset(width, height * 0.25f), strokeWidth = 1f)
                            drawLine(Color(0xFF2C3E50), Offset(0f, height * 0.5f), Offset(width, height * 0.5f), strokeWidth = 1f)
                            drawLine(Color(0xFF2C3E50), Offset(0f, height * 0.75f), Offset(width, height * 0.75f), strokeWidth = 1f)

                            val path = Path()
                            val steps = weightRecords.size - 1
                            val xStep = width / steps

                            weightRecords.forEachIndexed { idx, record ->
                                val pctY = (record.weightKg.toFloat() - minW) / diffW
                                val x = idx * xStep
                                val y = height - (pctY * height)

                                if (idx == 0) {
                                    path.moveTo(x, y)
                                } else {
                                    path.lineTo(x, y)
                                }

                                drawCircle(
                                    color = NeonCyberIce,
                                    radius = 6f,
                                    center = Offset(x, y)
                                )
                            }

                            drawPath(
                                path = path,
                                color = NeonCyberIce,
                                style = Stroke(width = 4f)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Início", fontSize = 10.sp, color = SoftSlateText)
                            Text("Atual", fontSize = 10.sp, color = NeonCyberIce, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // --- CHART 2: SESSIONS COMPLETED PERFORMANCE BAR CHART ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CarbonSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Consistência de Treinos por Época", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(12.dp))

                    if (sessions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .background(CarbonCard, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Complete seus treinos para preencher as colunas.",
                                color = SoftSlateText,
                                fontSize = 11.sp
                            )
                        }
                    } else {
                        // Drawing static mock bar with dynamic calculations
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            val mockupMonths = listOf("MAR", "ABR", "MAI", "JUN")
                            val mockupCounts = listOf(2, 6, sessions.size, sessions.size + 4)

                            mockupCounts.forEachIndexed { index, cnt ->
                                val maxCount = mockupCounts.maxOrNull() ?: 10
                                val filledPct = cnt.toFloat() / maxCount.toFloat()

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Bottom,
                                    modifier = Modifier.fillMaxHeight()
                                ) {
                                    Text("$cnt", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(28.dp)
                                            .fillMaxHeight(filledPct.coerceAtLeast(0.1f))
                                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                            .background(
                                                Brush.verticalGradient(
                                                    colors = listOf(AthleticVolt, Color(0xFF2E7D32))
                                                )
                                            )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(mockupMonths[index], fontSize = 10.sp, color = SoftSlateText)
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- SUB SECTION: SYSTEM PERFORMANCE SUM ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CarbonSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Resumo do Rendimento Geral", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total Sessoes", fontSize = 11.sp, color = SoftSlateText)
                            Text("${sessions.size} treinos", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                        Column {
                            Text("Frequência Média", fontSize = 11.sp, color = SoftSlateText)
                            Text("3.2x / sem", fontSize = 16.sp, fontWeight = FontWeight.Black, color = AthleticVolt)
                        }
                        Column {
                            Text("Tempo Acumulado", fontSize = 11.sp, color = SoftSlateText)
                            val hourSum = (sessions.sumOf { it.durationSeconds } / 3600f)
                            Text(String.format("%.1fh", hourSum), fontSize = 16.sp, fontWeight = FontWeight.Black, color = NeonCyberIce)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// HELPER DIALOG 1: CREATOR PLANNER SHEET
// ==========================================
@Composable
fun TemplateCreatorDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, List<TemplateExercise>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    var exerciseNameInput by remember { mutableStateOf("") }
    var setsVal by remember { mutableStateOf("3") }
    var repsVal by remember { mutableStateOf("10") }
    val addedList = remember { mutableStateListOf<TemplateExercise>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Montar Planilha de Treino", fontWeight = FontWeight.Bold, color = Color.White)
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome do Treino (Ex: Treino A)", color = SoftSlateText) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = AthleticVolt,
                            unfocusedBorderColor = CarbonCard
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Foco / Descrição", color = SoftSlateText) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = AthleticVolt,
                            unfocusedBorderColor = CarbonCard
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Divider(color = CarbonCard, modifier = Modifier.padding(vertical = 4.dp))
                    Text("Adicionar Exercícios à Planilha", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AthleticVolt)
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = exerciseNameInput,
                            onValueChange = { exerciseNameInput = it },
                            placeholder = { Text("Supino, Rosca...", fontSize = 11.sp, color = SoftSlateText) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = AthleticVolt,
                                unfocusedBorderColor = CarbonCard
                            ),
                            modifier = Modifier.weight(1.8f)
                        )
                        OutlinedTextField(
                            value = setsVal,
                            onValueChange = { setsVal = it },
                            placeholder = { Text("Séries", fontSize = 11.sp, color = SoftSlateText) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = AthleticVolt,
                                unfocusedBorderColor = CarbonCard
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = repsVal,
                            onValueChange = { repsVal = it },
                            placeholder = { Text("Reps", fontSize = 11.sp, color = SoftSlateText) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = AthleticVolt,
                                unfocusedBorderColor = CarbonCard
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = {
                                if (exerciseNameInput.isNotBlank()) {
                                    val s = setsVal.toIntOrNull() ?: 3
                                    addedList.add(
                                        TemplateExercise(
                                            templateId = 0,
                                            name = exerciseNameInput,
                                            targetSets = s,
                                            targetReps = repsVal
                                        )
                                    )
                                    exerciseNameInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AthleticVolt),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = CarbonMidnight)
                        }
                    }
                }

                items(addedList) { ex ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CarbonCard, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${ex.name} (${ex.targetSets}x${ex.targetReps})", color = Color.White, fontSize = 12.sp)
                        IconButton(onClick = { addedList.remove(ex) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Deletar", tint = GymWarning)
                        }
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = SoftSlateText)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name, desc, addedList.toList())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AthleticVolt)
            ) {
                Text("Salvar Planilha", color = CarbonMidnight, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = CarbonSurface
    )
}

// ==========================================
// HELPER DIALOG 2: COMPROMISSO SCHEDULER
// ==========================================
@Composable
fun SchedulerDialog(
    viewModel: GymViewModel,
    onDismiss: () -> Unit,
    onSave: (String, Long, Int?) -> Unit
) {
    val templates by viewModel.allTemplates.collectAsStateWithLifecycle()
    var selectedTemplateIndex by remember { mutableStateOf(-1) }
    var title by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Agendar Compromisso", fontWeight = FontWeight.Bold, color = Color.White)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Selecione um Treino:", color = SoftSlateText, fontSize = 11.sp)

                LazyColumn(modifier = Modifier.height(150.dp)) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedTemplateIndex == -1) AthleticVolt else CarbonCard
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    selectedTemplateIndex = -1
                                    title = "Treino Livre Planejado"
                                }
                        ) {
                            Text("Treino Personalizado / Livre", color = if (selectedTemplateIndex == -1) CarbonMidnight else Color.White, modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    itemsIndexed(templates) { index, template ->
                        val isSelected = selectedTemplateIndex == index
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) AthleticVolt else CarbonCard
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    selectedTemplateIndex = index
                                    title = "Sessão: ${template.title}"
                                }
                        ) {
                            Text(template.title, color = if (isSelected) CarbonMidnight else Color.White, modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nome do Evento / Lembrete", color = SoftSlateText) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = AthleticVolt,
                        unfocusedBorderColor = CarbonCard
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    "* Lembrete: O app agendará uma notificação inteligente para garantir que você não perca seu compromisso.",
                    fontSize = 9.sp,
                    color = SoftSlateText,
                    lineHeight = 11.sp
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = SoftSlateText)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val chosenTemplateId = if (selectedTemplateIndex != -1) templates[selectedTemplateIndex].id else null
                        onSave(title, System.currentTimeMillis(), chosenTemplateId)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AthleticVolt)
            ) {
                Text("Agendar", color = CarbonMidnight, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = CarbonSurface
    )
}

// Extra visual elements definitions
val CustomBorderColor = Color(0xFF37474F)
