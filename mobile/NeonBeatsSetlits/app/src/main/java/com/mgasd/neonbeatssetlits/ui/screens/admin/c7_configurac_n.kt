package com.mgasd.neonbeatssetlits.ui.screens.admin

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.Rule
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mgasd.neonbeatssetlits.ui.theme.NeonBeatsTheme
import com.mgasd.neonbeatssetlits.viewmodel.AdminViewModel
import com.mgasd.neonbeatssetlits.viewmodel.CatalogItem
import com.mgasd.neonbeatssetlits.viewmodel.ConfigUiState

@Composable
fun ConfigScreen(
    viewModel: AdminViewModel,
    onNavigateToDashboard: () -> Unit,
    onNavigateToQueue: () -> Unit,
    onNavigateToApprovals: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToStaff: () -> Unit
) {
    val uiState by viewModel.configState.collectAsStateWithLifecycle()
    val mesaNumero by viewModel.mesaNumero.collectAsStateWithLifecycle()

    ConfigContent(
        uiState = uiState,
        mesaNumero = mesaNumero,
        onBarNameChange = viewModel::onBarNameChange,
        onToggleYouTube = viewModel::onToggleYouTube,
        onToggleExplicitFilter = viewModel::onToggleExplicitFilter,
        onUpdateInitialCredits = viewModel::updateInitialCredits,
        onUpdateExpiryMinutes = viewModel::updateExpiryMinutes,
        onImportCsv = viewModel::onImportCsv,
        onSaveConfig = viewModel::onSaveConfig,
        onNavigateToDashboard = onNavigateToDashboard,
        onNavigateToQueue = onNavigateToQueue,
        onNavigateToApprovals = onNavigateToApprovals,
        onNavigateToStats = onNavigateToStats,
        onNavigateToStaff = onNavigateToStaff
    )
}

@Composable
fun ConfigContent(
    uiState: ConfigUiState,
    mesaNumero: String,
    onBarNameChange: (String) -> Unit,
    onToggleYouTube: (Boolean) -> Unit,
    onToggleExplicitFilter: (Boolean) -> Unit,
    onUpdateInitialCredits: (Int) -> Unit,
    onUpdateExpiryMinutes: (Int) -> Unit,
    onImportCsv: () -> Unit,
    onSaveConfig: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToQueue: () -> Unit,
    onNavigateToApprovals: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToStaff: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ConfigTopBar(mesaNumero = mesaNumero)
        },
        bottomBar = {
            AdminBottomNavBar(
                onHome = onNavigateToDashboard,
                onQueue = onNavigateToQueue,
                onStats = onNavigateToStats,
                onStaff = onNavigateToStaff,
                onConfig = { /* Already here */ }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = 120.dp)
            ) {
                item {
                    Text(text = "SETTINGS", style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                }

                item {
                    BarProfileCard(barName = uiState.barName, onBarNameChange = onBarNameChange)
                }

                item {
                    IntegrationsCard(allowYouTube = uiState.allowYouTube, explicitFilter = uiState.explicitFilter, onToggleYouTube = onToggleYouTube, onToggleExplicitFilter = onToggleExplicitFilter)
                }

                item {
                    SystemRulesCard(initialCredits = uiState.initialCredits, expiryMinutes = uiState.requestExpiryMinutes, onUpdateInitialCredits = onUpdateInitialCredits, onUpdateExpiryMinutes = onUpdateExpiryMinutes)
                }
            }

            Button(
                onClick = onSaveConfig,
                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp).fillMaxWidth(),
                shape = CircleShape
            ) {
                Text("SAVE CHANGES")
            }
        }
    }
}

@Composable
fun ConfigTopBar(mesaNumero: String) {
    Surface(color = MaterialTheme.colorScheme.background, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Row(modifier = Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { }) { Icon(Icons.Default.Menu, null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)) }
            Text(text = "NEON BEATS SETLIST", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
            Text(text = "TABLE $mesaNumero", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun BarProfileCard(barName: String, onBarNameChange: (String) -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "BAR PROFILE", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            TextField(value = barName, onValueChange = onBarNameChange, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent))
        }
    }
}

@Composable
fun IntegrationsCard(allowYouTube: Boolean, explicitFilter: Boolean, onToggleYouTube: (Boolean) -> Unit, onToggleExplicitFilter: (Boolean) -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "INTEGRATIONS", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Allow YouTube")
                Switch(checked = allowYouTube, onCheckedChange = onToggleYouTube)
            }
        }
    }
}

@Composable
fun SystemRulesCard(initialCredits: Int, expiryMinutes: Int, onUpdateInitialCredits: (Int) -> Unit, onUpdateExpiryMinutes: (Int) -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "SYSTEM RULES", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text("Initial Credits: $initialCredits")
            Text("Expiry: $expiryMinutes min")
        }
    }
}

@Composable
fun LocalCatalogSection(catalog: List<CatalogItem>, onImportCsv: () -> Unit) { }

@Preview(showBackground = true)
@Composable
fun ConfigPreview() {
    NeonBeatsTheme {
        ConfigContent(ConfigUiState(), "04", {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {})
    }
}
