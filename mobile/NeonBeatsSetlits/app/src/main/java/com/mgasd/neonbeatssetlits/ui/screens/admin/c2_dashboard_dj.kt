package com.mgasd.neonbeatssetlits.ui.screens.admin

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PendingActions
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.mgasd.neonbeatssetlits.viewmodel.DashboardDjUiState

@Composable
fun DashboardDjScreen(
    viewModel: AdminViewModel,
    onNavigateToQueue: () -> Unit,
    onNavigateToApprovals: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToStaff: () -> Unit,
    onNavigateToConfig: () -> Unit
) {
    val uiState by viewModel.dashboardState.collectAsStateWithLifecycle()

    DashboardDjContent(
        uiState = uiState,
        onTogglePlayback = viewModel::onTogglePlayback,
        onSkipPrevious = viewModel::onSkipPrevious,
        onSkipNext = viewModel::onSkipNext,
        onNavigateToHome = { /* Already here */ },
        onNavigateToQueue = onNavigateToQueue,
        onNavigateToApprovals = onNavigateToApprovals,
        onNavigateToStats = onNavigateToStats,
        onNavigateToStaff = onNavigateToStaff,
        onNavigateToConfig = onNavigateToConfig
    )
}

@Composable
fun DashboardDjContent(
    uiState: DashboardDjUiState,
    onTogglePlayback: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSkipNext: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToQueue: () -> Unit,
    onNavigateToApprovals: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToStaff: () -> Unit,
    onNavigateToConfig: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            DashboardTopBar()
        },
        bottomBar = {
            DashboardBottomBar(
                onHome = onNavigateToHome,
                onQueue = onNavigateToQueue,
                onStats = onNavigateToStats,
                onStaff = onNavigateToStaff,
                onConfig = onNavigateToConfig
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp)
        ) {
            // Now Playing Card
            item {
                NowPlayingAdminCard(
                    title = uiState.currentTrackTitle,
                    artist = uiState.currentTrackArtist,
                    isPlaying = uiState.isPlaying,
                    onTogglePlayback = onTogglePlayback,
                    onSkipPrevious = onSkipPrevious,
                    onSkipNext = onSkipNext
                )
            }

            // Quick Metrics Grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        MetricBox(
                            modifier = Modifier.weight(1f).clickable { onNavigateToStats() },
                            icon = Icons.Default.GridView,
                            title = "Mesas Activas",
                            value = "${uiState.activeTables}/${uiState.totalTables}",
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        MetricBox(
                            modifier = Modifier.weight(1f).clickable { onNavigateToQueue() },
                            icon = Icons.AutoMirrored.Filled.QueueMusic,
                            title = "En Cola",
                            value = uiState.queueSize.toString(),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Box(modifier = Modifier.clickable { onNavigateToApprovals() }) {
                        PendingRequestsCard(count = uiState.pendingRequests)
                    }
                }
            }

            // Quick Access List
            item {
                QuickAccessSection(
                    onSetlist = onNavigateToQueue,
                    onAnuncios = { /* TODO */ },
                    onAudio = onNavigateToConfig
                )
            }
        }
    }
}

@Composable
fun DashboardTopBar() {
    Surface(
        color = MaterialTheme.colorScheme.background,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
            }
            Text(
                text = "EN VIVO",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = (-1).sp),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { }) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
            }
        }
    }
}

@Composable
fun NowPlayingAdminCard(
    title: String,
    artist: String,
    isPlaying: Boolean,
    onTogglePlayback: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSkipNext: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.secondary, MaterialTheme.shapes.small)
            .shadow(12.dp, spotColor = MaterialTheme.colorScheme.secondary),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(
                    modifier = Modifier.size(80.dp).clip(MaterialTheme.shapes.extraSmall).background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Surface(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f), shape = CircleShape) {
                        Text(
                            text = "SONANDO AHORA",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Text(text = title, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(text = artist, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                DashboardEqualizer(isPlaying)
            }
            HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onSkipPrevious) { Icon(Icons.Default.SkipPrevious, contentDescription = "Previous") }
                Surface(onClick = onTogglePlayback, modifier = Modifier.size(48.dp), shape = CircleShape, color = MaterialTheme.colorScheme.secondary) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = "Toggle", tint = MaterialTheme.colorScheme.onSecondary)
                    }
                }
                IconButton(onClick = onSkipNext) { Icon(Icons.Default.SkipNext, contentDescription = "Next") }
            }
        }
    }
}

@Composable
fun DashboardEqualizer(isPlaying: Boolean) {
    Row(modifier = Modifier.height(16.dp), horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.Bottom) {
        val infiniteTransition = rememberInfiniteTransition(label = "eq")
        repeat(5) { index ->
            val scale by if (isPlaying) {
                infiniteTransition.animateFloat(
                    initialValue = 0.2f, targetValue = 1f,
                    animationSpec = infiniteRepeatable(tween(600 + (index * 100)), RepeatMode.Reverse),
                    label = "bar"
                )
            } else { remember { mutableFloatStateOf(0.2f) } }
            Box(modifier = Modifier.width(4.dp).fillMaxHeight(scale).background(MaterialTheme.colorScheme.secondary))
        }
    }
}

@Composable
fun MetricBox(modifier: Modifier = Modifier, icon: ImageVector, title: String, value: String, color: Color) {
    Surface(modifier = modifier, color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = value, style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold))
            Text(text = title.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun PendingRequestsCard(count: Int) {
    Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Icon(imageVector = Icons.AutoMirrored.Filled.PendingActions, contentDescription = null, modifier = Modifier.align(Alignment.BottomEnd).size(96.dp).alpha(0.05f), tint = MaterialTheme.colorScheme.secondary)
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.PendingActions, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    Surface(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f), shape = CircleShape) {
                        Text(text = "REQUIERE ATENCIÓN", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.secondary)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = count.toString(), style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold))
                Text(text = "PETICIONES PENDIENTES", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun QuickAccessSection(onSetlist: () -> Unit, onAnuncios: () -> Unit, onAudio: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "ACCESOS RÁPIDOS", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        QuickAccessItem(icon = Icons.Default.FormatListBulleted, title = "Gestionar Setlist", subtitle = "Editar lista de reproducción actual", color = MaterialTheme.colorScheme.primary, onClick = onSetlist)
        QuickAccessItem(icon = Icons.Default.RecordVoiceOver, title = "Anuncios en Sala", subtitle = "Emitir mensajes por PA", color = MaterialTheme.colorScheme.tertiary, onClick = onAnuncios)
        QuickAccessItem(icon = Icons.Default.SettingsInputComponent, title = "Configuración de Audio", subtitle = "Ajustes de mesa de mezclas", color = MaterialTheme.colorScheme.secondary, onClick = onAudio)
    }
}

@Composable
fun QuickAccessItem(icon: ImageVector, title: String, subtitle: String, color: Color, onClick: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(color.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                }
                Column {
                    Text(text = title, style = MaterialTheme.typography.bodyLarge)
                    Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}

@Composable
fun DashboardBottomBar(onHome: () -> Unit, onQueue: () -> Unit, onStats: () -> Unit, onStaff: () -> Unit, onConfig: () -> Unit) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.background, modifier = Modifier.height(80.dp).border(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        NavigationBarItem(selected = true, onClick = onHome, icon = { Icon(Icons.Default.Home, null) }, label = { Text("HOME") })
        NavigationBarItem(selected = false, onClick = onQueue, icon = { Icon(Icons.AutoMirrored.Filled.QueueMusic, null) }, label = { Text("QUEUE") })
        NavigationBarItem(selected = false, onClick = onStats, icon = { Icon(Icons.AutoMirrored.Filled.ReceiptLong, null) }, label = { Text("STATS") })
        NavigationBarItem(selected = false, onClick = onStaff, icon = { Icon(Icons.Default.Person, null) }, label = { Text("STAFF") })
        NavigationBarItem(selected = false, onClick = onConfig, icon = { Icon(Icons.Default.Settings, null) }, label = { Text("CONFIG") })
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardDjPreview() {
    NeonBeatsTheme {
        DashboardDjContent(DashboardDjUiState(), {}, {}, {}, {}, {}, {}, {}, {}, {})
    }
}
