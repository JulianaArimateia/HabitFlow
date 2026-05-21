package com.habitflow.ui.screens.dashboard

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habitflow.data.remote.WeatherData
import com.habitflow.ui.components.HeatmapCalendar
import com.habitflow.ui.components.WeeklyBarChart
import com.habitflow.ui.components.categoryColor
import com.habitflow.ui.components.categoryIcon
import com.habitflow.ui.theme.*
import com.habitflow.util.LocationHelper
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToHome: () -> Unit,
    onAddHabit: () -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.any { it }
        val coords = if (granted) LocationHelper.getLastLocation(context) else null
        viewModel.fetchWeather(coords?.first, coords?.second)
    }

    LaunchedEffect(Unit) {
        val hasPermission = locationPermissions.any {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        if (hasPermission) {
            val coords = LocationHelper.getLastLocation(context)
            viewModel.fetchWeather(coords?.first, coords?.second)
        } else {
            permissionLauncher.launch(locationPermissions)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "HabitFlow",
                            style = MaterialTheme.typography.titleLarge,
                            color = Primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Dashboard Mensal",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVar
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Atualizar", tint = OnSurfaceVar)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Surface) {
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToHome,
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Início") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
                    label = { Text("Estatísticas") }
                )
            }
        },
        containerColor = Background
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            state.weather?.let { item { WeatherCard(it) } }
            item { StreakCard(state.streak) }
            item { MetricsRow(state.completedRate) }
            item { WeeklyChart(state.last7Days) }
            item { MonthlyHeatmap(state.heatmapData) }
            item {
                Text(
                    "Seus Hábitos",
                    style = MaterialTheme.typography.titleLarge,
                    color = OnBackground,
                    fontWeight = FontWeight.Bold
                )
            }
            if (state.habitConsistencies.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Surface)
                    ) {
                        Box(
                            Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Nenhum hábito cadastrado",
                                color = OnSurfaceVar,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            } else {
                items(state.habitConsistencies) { hc ->
                    HabitConsistencyCard(hc)
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun WeatherCard(weather: WeatherData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(weather.emoji, fontSize = 40.sp)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "${weather.temperature.toInt()}°C  •  ${weather.description}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnBackground
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    weather.habitSuggestion,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVar
                )
            }
        }
    }
}

@Composable
private fun StreakCard(streak: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "OFENSIVA ATUAL",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnPrimary.copy(alpha = 0.7f),
                    letterSpacing = 1.5.sp
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        "$streak",
                        style = MaterialTheme.typography.headlineLarge,
                        color = OnPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 48.sp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "dias seguidos",
                        style = MaterialTheme.typography.titleMedium,
                        color = OnPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    if (streak >= 7) "Você está arrasando! Continue assim!"
                    else "Continue para manter sua sequência!",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnPrimary.copy(alpha = 0.8f)
                )
            }
            Text(
                if (streak >= 7) "🔥" else "⚡",
                fontSize = 56.sp
            )
        }
    }
}

@Composable
private fun MetricsRow(completedRate: Float) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MetricCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.CheckCircle,
            iconTint = Success,
            label = "Completos",
            value = "${completedRate.toInt()}%"
        )
        MetricCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.AutoAwesome,
            iconTint = StreakOrange,
            label = "Foco",
            value = when {
                completedRate >= 80 -> "Alto"
                completedRate >= 50 -> "Médio"
                else -> "Baixo"
            }
        )
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    label: String,
    value: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVar)
            Text(value, style = MaterialTheme.typography.titleLarge, color = OnBackground, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun WeeklyChart(last7Days: List<Float>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                "Consistência Semanal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OnBackground
            )
            Spacer(Modifier.height(4.dp))
            val avg = if (last7Days.isNotEmpty()) last7Days.average() else 0.0
            Text(
                "Média de ${String.format("%.1f", avg * 100)}% nos últimos 7 dias",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVar
            )
            Spacer(Modifier.height(16.dp))
            WeeklyBarChart(data = last7Days, modifier = Modifier.fillMaxWidth().height(120.dp))
        }
    }
}

@Composable
private fun MonthlyHeatmap(data: Map<Long, Int>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Atividade Mensal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnBackground
                )
                Text(
                    java.text.SimpleDateFormat("MMMM", Locale("pt", "BR"))
                        .format(java.util.Date())
                        .replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVar
                )
            }
            Spacer(Modifier.height(16.dp))
            HeatmapCalendar(data = data)
            Spacer(Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Menos", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVar)
                listOf(HeatmapEmpty, HeatmapLow, HeatmapMid, HeatmapHigh).forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(color)
                    )
                }
                Text("Mais", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVar)
            }
        }
    }
}

@Composable
private fun HabitConsistencyCard(hc: HabitConsistency) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(categoryColor(hc.habit.categoria)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryIcon(hc.habit.categoria),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(hc.habit.nome, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { hc.rate / 100f },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = categoryColor(hc.habit.categoria),
                    trackColor = HeatmapEmpty
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${hc.rate.toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = Primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "🔥 ${hc.streak}",
                    style = MaterialTheme.typography.bodySmall,
                    color = StreakOrange
                )
            }
        }
    }
}
