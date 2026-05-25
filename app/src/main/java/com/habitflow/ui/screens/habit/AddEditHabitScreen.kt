package com.habitflow.ui.screens.habit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habitflow.ui.components.categoryColor
import com.habitflow.ui.components.categoryIcon
import com.habitflow.ui.theme.*

private val CATEGORIES = listOf("Saúde", "Produtividade", "Bem-estar", "Hidratação", "Leitura", "Sono", "Academia")
private val DAYS_LABEL = listOf("D", "S", "T", "Q", "Q", "S", "S")
private val DAYS_VALUES = listOf(1, 2, 3, 4, 5, 6, 7)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditHabitScreen(
    habitId: Long?,
    onSaved: () -> Unit,
    onBack: () -> Unit,
    viewModel: AddEditHabitViewModel = viewModel()
) {
    val state by viewModel.formState.collectAsState()
    var showTimePicker by remember { mutableStateOf(false) }

    LaunchedEffect(habitId) {
        if (habitId != null) viewModel.loadHabit(habitId)
    }
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onSaved()
    }

    if (showTimePicker) {
        TimePickerDialog(
            initial = state.horario,
            onDismiss = { showTimePicker = false },
            onConfirm = { h, m ->
                viewModel.onHorarioChange("%02d:%02d".format(h, m))
                showTimePicker = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (habitId == null) "Novo Hábito" else "Editar Hábito",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (habitId == null) {
                Text(
                    "Comece uma nova jornada de evolução pessoal.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Nome
            FormSection(title = "NOME DO HÁBITO") {
                OutlinedTextField(
                    value = state.nome,
                    onValueChange = viewModel::onNomeChange,
                    placeholder = { Text("Ex: Meditação Matinal", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    isError = state.error != null
                )
            }

            // Categoria
            FormSection(title = "CATEGORIA") {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CATEGORIES.forEach { cat ->
                        val selected = state.categoria == cat
                        FilterChip(
                            selected = selected,
                            onClick = { viewModel.onCategoriaChange(cat) },
                            label = { Text(cat, fontSize = 12.sp) },
                            leadingIcon = if (selected) {
                                {
                                    Icon(
                                        categoryIcon(cat),
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = categoryColor(cat).copy(alpha = 0.15f),
                                selectedLabelColor = categoryColor(cat)
                            )
                        )
                    }
                }
            }

            // Frequência
            FormSection(title = "FREQUÊNCIA") {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.padding(4.dp)) {
                        Row(Modifier.fillMaxWidth()) {
                            FreqTab("Diário", state.frequencia == "DIARIO") {
                                viewModel.onFrequenciaChange("DIARIO")
                            }
                            FreqTab("Específico", state.frequencia == "ESPECIFICO") {
                                viewModel.onFrequenciaChange("ESPECIFICO")
                            }
                        }
                    }
                }
                if (state.frequencia == "ESPECIFICO") {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        DAYS_VALUES.forEachIndexed { idx, day ->
                            val selected = state.diasSemana.contains(day)
                            DayCircle(
                                label = DAYS_LABEL[idx],
                                selected = selected,
                                onClick = { viewModel.toggleDay(day) }
                            )
                        }
                    }
                }
            }

            // Horário
            FormSection(title = "HORÁRIO PREFERENCIAL") {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                state.horario,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            val (h, _) = state.horario.split(":").let {
                                (it.getOrNull(0)?.toIntOrNull() ?: 7) to (it.getOrNull(1)?.toIntOrNull() ?: 0)
                            }
                            Text(
                                when (h) {
                                    in 5..11 -> "Manhã"
                                    in 12..17 -> "Tarde"
                                    else -> "Noite"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        OutlinedButton(
                            onClick = { showTimePicker = true },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Alterar")
                        }
                    }
                }
            }

            // Dica
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("DICA FLOW", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Notificações inteligentes ajustam automaticamente o horário baseado no seu histórico.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            if (state.error != null) {
                Text(state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { viewModel.save(habitId) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                contentPadding = PaddingValues(vertical = 16.dp),
                enabled = !state.isLoading
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    "Salvar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun FormSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        content()
    }
}

@Composable
private fun RowScope.FreqTab(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun DayCircle(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
            .border(1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    initial: String,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    val parts = initial.split(":").map { it.toIntOrNull() ?: 0 }
    val timePickerState = rememberTimePickerState(
        initialHour = parts.getOrElse(0) { 7 },
        initialMinute = parts.getOrElse(1) { 30 },
        is24Hour = true
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Escolha o horário") },
        text = { TimePicker(state = timePickerState) },
        confirmButton = {
            TextButton(onClick = { onConfirm(timePickerState.hour, timePickerState.minute) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
