package com.mgasd.neonbeatssetlits.ui.screens.admin

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mgasd.neonbeatssetlits.ui.theme.NeonBeatsTheme
import com.mgasd.neonbeatssetlits.viewmodel.AdminViewModel
import com.mgasd.neonbeatssetlits.viewmodel.DashboardDjUiState
import com.mgasd.neonbeatssetlits.viewmodel.QueueAdminItem
import androidx.compose.material.icons.automirrored.filled.ReceiptLong

@Composable
fun QueueManagementScreen(
    viewModel: AdminViewModel,
    onNavigateToDashboard: () -> Unit,
    onNavigateToApprovals: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToStaff: () -> Unit,
    onNavigateToConfig: () -> Unit
) {
    val dashboardState by viewModel.dashboardState.collectAsStateWithLifecycle()
    val queueList by viewModel.queueList.collectAsStateWithLifecycle()

    QueueManagementContent(
        dashboardState = dashboardState,
        queueList = queueList,
        onRemoveItem = viewModel::onRemoveFromQueue,
        onClearQueue = viewModel::onClearQueue,
        onNavigateToDashboard = onNavigateToDashboard,
        onNavigateToApprovals = onNavigateToApprovals,
        onNavigateToStats = onNavigateToStats,
        onNavigateToStaff = onNavigateToStaff,
        onNavigateToConfig = onNavigateToConfig
    )
}

@Composable
fun QueueManagementContent(
    dashboardState: DashboardDjUiState,
    queueList: List<QueueAdminItem>,
    onRemoveItem: (String) -> Unit,
    onClearQueue: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToApprovals: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToStaff: () -> Unit,
    onNavigateToConfig: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AdminQueueTopBar()
        },
        bottomBar = {
            AdminBottomNavBar(
                onHome = onNavigateToDashboard,
                onQueue = { /* Already here */ },
                onStats = onNavigateToStats,
                onStaff = onNavigateToStaff,
                onConfig = onNavigateToConfig
            )
        },
        floatingActionButton = {
            if (queueList.isNotEmpty()) {
                ClearQueueFAB(onClick = onClearQueue)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "QUEUE MANAGEMENT",
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = (-1).sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Sync, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                        Text(text = "REAL-TIME SYNC ACTIVE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            item {
                NowPlayingAdminQueueCard(
                    title = dashboardState.currentTrackTitle,
                    artist = dashboardState.currentTrackArtist,
                    isPlaying = dashboardState.isPlaying
                )
            }

            items(queueList, key = { it.id }) { item ->
                QueueAdminItemRow(item = item, onRemove = { onRemoveItem(item.id) })
            }
        }
    }
}

@Composable
fun AdminQueueTopBar() {
    Surface(color = MaterialTheme.colorScheme.background, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Row(modifier = Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { }) { Icon(Icons.Default.Menu, null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)) }
            Text(text = "NEON BEATS SETLIST", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = (-1).sp), color = MaterialTheme.colorScheme.primary)
            Text(text = "TABLE 04", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun NowPlayingAdminQueueCard(title: String, artist: String, isPlaying: Boolean) {
    Card(modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.medium).shadow(12.dp, spotColor = MaterialTheme.colorScheme.primary), shape = MaterialTheme.shapes.medium, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.size(64.dp).clip(MaterialTheme.shapes.extraSmall).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.MusicNote, null, tint = MaterialTheme.colorScheme.primary)
                }
                Column {
                    Surface(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), shape = CircleShape) {
                        Text(text = "NOW PLAYING", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                    }
                    Text(text = title, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                    Text(text = artist, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            QueueEqualizer(isPlaying)
        }
    }
}

@Composable
fun QueueEqualizer(isPlaying: Boolean) {
    Row(modifier = Modifier.height(24.dp), horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.Bottom) {
        val infiniteTransition = rememberInfiniteTransition(label = "eq")
        repeat(5) { index ->
            val scale by if (isPlaying) {
                infiniteTransition.animateFloat(initialValue = 0.3f, targetValue = 1.2f, animationSpec = infiniteRepeatable(tween(600 + (index * 100)), RepeatMode.Reverse), label = "bar")
            } else { remember { mutableFloatStateOf(0.3f) } }
            Box(modifier = Modifier.width(4.dp).fillMaxHeight(scale).background(MaterialTheme.colorScheme.primary))
        }
    }
}

@Composable
fun QueueAdminItemRow(item: QueueAdminItem, onRemove: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.small) {
        Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.DragIndicator, null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                Text(text = item.position, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Column {
                    Text(text = item.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text(text = item.artist, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = item.duration, style = MaterialTheme.typography.labelLarge)
                IconButton(onClick = onRemove) { Icon(Icons.Default.Close, null) }
            }
        }
    }
}

@Composable
fun ClearQueueFAB(onClick: () -> Unit) {
    ExtendedFloatingActionButton(onClick = onClick, containerColor = MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.onSecondary, shape = CircleShape) {
        Icon(Icons.Default.DeleteSweep, null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "CLEAR QUEUE")
    }
}

@Composable
fun AdminBottomNavBar(onHome: () -> Unit, onQueue: () -> Unit, onStats: () -> Unit, onStaff: () -> Unit, onConfig: () -> Unit) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.background, modifier = Modifier.height(80.dp).border(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        NavigationBarItem(selected = false, onClick = onHome, icon = { Icon(Icons.Default.Home, null) }, label = { Text("HOME") })
        NavigationBarItem(selected = true, onClick = onQueue, icon = { Icon(Icons.AutoMirrored.Filled.QueueMusic, null) }, label = { Text("QUEUE") })
        NavigationBarItem(selected = false, onClick = onStats, icon = { Icon(Icons.AutoMirrored.Filled.ReceiptLong, null) }, label = { Text("STATS") })
        NavigationBarItem(selected = false, onClick = onStaff, icon = { Icon(Icons.Default.Person, null) }, label = { Text("STAFF") })
        NavigationBarItem(selected = false, onClick = onConfig, icon = { Icon(Icons.Default.Settings, null) }, label = { Text("CONFIG") })
    }
}

@Preview(showBackground = true)
@Composable
fun QueueManagementPreview() {
    NeonBeatsTheme {
        QueueManagementContent(DashboardDjUiState(), emptyList(), {}, {}, {}, {}, {}, {}, {})
    }
}
