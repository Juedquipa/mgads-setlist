package com.mgasd.neonbeatssetlits.ui.screens.admin

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mgasd.neonbeatssetlits.ui.theme.NeonBeatsTheme
import com.mgasd.neonbeatssetlits.viewmodel.AdminViewModel
import com.mgasd.neonbeatssetlits.viewmodel.PendingApprovalItem

@Composable
fun AprobacionesScreen(
    viewModel: AdminViewModel,
    onNavigateToDashboard: () -> Unit,
    onNavigateToQueue: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToStaff: () -> Unit,
    onNavigateToConfig: () -> Unit
) {
    val pendingApprovals by viewModel.pendingApprovals.collectAsStateWithLifecycle()

    AprobacionesContent(
        pendingApprovals = pendingApprovals,
        onApprove = viewModel::onApproveRequest,
        onReject = viewModel::onRejectRequest,
        onNavigateToDashboard = onNavigateToDashboard,
        onNavigateToQueue = onNavigateToQueue,
        onNavigateToStats = onNavigateToStats,
        onNavigateToStaff = onNavigateToStaff,
        onNavigateToConfig = onNavigateToConfig
    )
}

@Composable
fun AprobacionesContent(
    pendingApprovals: List<PendingApprovalItem>,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToQueue: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToStaff: () -> Unit,
    onNavigateToConfig: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AprobacionesTopBar()
        },
        bottomBar = {
            AdminBottomNavBar(
                onHome = onNavigateToDashboard,
                onQueue = onNavigateToQueue,
                onStats = onNavigateToStats,
                onStaff = onNavigateToStaff,
                onConfig = onNavigateToConfig
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "INCOMING FEED", style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 3.sp, fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.secondary)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(text = pendingApprovals.size.toString(), style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold, fontSize = 64.sp), color = MaterialTheme.colorScheme.primary)
                            Column {
                                Text(text = "PENDING REQUESTS", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onBackground)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    RequestMiniEqualizer()
                                    Text(text = "Live YouTube Queue", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }

                itemsIndexed(pendingApprovals, key = { _, item -> item.id }) { index, item ->
                    PendingRequestCard(
                        item = item,
                        isActive = index == 0,
                        onApprove = { onApprove(item.id) },
                        onReject = { onReject(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun AprobacionesTopBar() {
    Surface(color = MaterialTheme.colorScheme.background, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Row(modifier = Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { }) { Icon(Icons.Default.Menu, null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)) }
            Text(text = "NEON BEATS SETLIST", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = (-1).sp), color = MaterialTheme.colorScheme.primary)
            Text(text = "TABLE 04", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun RequestMiniEqualizer() {
    Row(modifier = Modifier.height(12.dp), horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.Bottom) {
        val infiniteTransition = rememberInfiniteTransition(label = "miniEq")
        repeat(3) { index ->
            val scale by infiniteTransition.animateFloat(initialValue = 0.3f, targetValue = 1f, animationSpec = infiniteRepeatable(tween(600 + (index * 200)), RepeatMode.Reverse), label = "bar")
            Box(modifier = Modifier.width(3.dp).fillMaxHeight(scale).background(MaterialTheme.colorScheme.primary))
        }
    }
}

@Composable
fun PendingRequestCard(item: PendingApprovalItem, isActive: Boolean, onApprove: () -> Unit, onReject: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().alpha(if (isActive) 1f else 0.8f).graphicsLayer(scaleX = if (isActive) 1f else 0.98f, scaleY = if (isActive) 1f else 0.98f).border(1.dp, if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.extraSmall), shape = MaterialTheme.shapes.extraSmall, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f).background(MaterialTheme.colorScheme.background)) {
                Icon(Icons.Default.SmartDisplay, null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), modifier = Modifier.fillMaxSize().padding(32.dp))
                Surface(modifier = Modifier.align(Alignment.BottomStart).padding(12.dp), color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f), shape = MaterialTheme.shapes.extraSmall) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.error)
                        Text(text = item.duration, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = item.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(text = item.artist, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.extraSmall, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
                        Text(text = item.requestedBy.uppercase(), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
                    }
                }
                if (isActive) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = onReject, modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.extraSmall, border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)) { Text("REJECT", color = MaterialTheme.colorScheme.error) }
                        Button(onClick = onApprove, modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.extraSmall, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) { Text("APPROVE") }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AprobacionesPreview() {
    NeonBeatsTheme {
        AprobacionesContent(emptyList(), {}, {}, {}, {}, {}, {}, {})
    }
}
