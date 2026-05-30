package com.mgasd.neonbeatssetlits.ui.screens.admin

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mgasd.neonbeatssetlits.ui.theme.NeonBeatsTheme
import com.mgasd.neonbeatssetlits.viewmodel.AdminTableStatus
import com.mgasd.neonbeatssetlits.viewmodel.AdminViewModel
import com.mgasd.neonbeatssetlits.viewmodel.StatisticsUiState
import com.mgasd.neonbeatssetlits.viewmodel.TableActivity
import com.mgasd.neonbeatssetlits.viewmodel.WaiterProductivity

@Composable
fun StatisticsScreen(
    viewModel: AdminViewModel,
    onNavigateToDashboard: () -> Unit,
    onNavigateToQueue: () -> Unit,
    onNavigateToApprovals: () -> Unit,
    onNavigateToStaff: () -> Unit,
    onNavigateToConfig: () -> Unit
) {
    val uiState by viewModel.statisticsState.collectAsStateWithLifecycle()
    val mesaNumero by viewModel.mesaNumero.collectAsStateWithLifecycle()

    StatisticsContent(
        uiState = uiState,
        mesaNumero = mesaNumero,
        onTabChange = viewModel::onTabChange,
        onSeeAllWaiters = viewModel::onSeeAllWaitersClick,
        onNavigateToDashboard = onNavigateToDashboard,
        onNavigateToQueue = onNavigateToQueue,
        onNavigateToApprovals = onNavigateToApprovals,
        onNavigateToStaff = onNavigateToStaff,
        onNavigateToConfig = onNavigateToConfig
    )
}

@Composable
fun StatisticsContent(
    uiState: StatisticsUiState,
    mesaNumero: String,
    onTabChange: (Int) -> Unit,
    onSeeAllWaiters: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToQueue: () -> Unit,
    onNavigateToApprovals: () -> Unit,
    onNavigateToStaff: () -> Unit,
    onNavigateToConfig: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            StatisticsTopBar(mesaNumero = mesaNumero)
        },
        bottomBar = {
            AdminBottomNavBar(
                onHome = onNavigateToDashboard,
                onQueue = onNavigateToQueue,
                onStats = { /* Already here */ },
                onStaff = onNavigateToStaff,
                onConfig = onNavigateToConfig
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            StatisticsGridBackground()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp)
            ) {
                item {
                    StatisticsHeader(
                        selectedTab = uiState.selectedTab,
                        onTabChange = onTabChange
                    )
                }

                item {
                    StatisticsKpiGrid(uiState)
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        OrderFlowChart(uiState.orderFlow)
                        TableMapSection(uiState.tableMap)
                    }
                }

                item {
                    TopWaitersSection(
                        waiters = uiState.topWaiters,
                        onSeeAll = onSeeAllWaiters
                    )
                }
            }
        }
    }
}

@Composable
fun StatisticsTopBar(mesaNumero: String) {
    Surface(color = MaterialTheme.colorScheme.background, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Row(modifier = Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { }) { Icon(Icons.Default.Menu, null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)) }
            Text(text = "NEON BEATS SETLIST", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = (-1).sp), color = MaterialTheme.colorScheme.primary)
            Text(text = "TABLE $mesaNumero", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun StatisticsHeader(selectedTab: Int, onTabChange: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "RENDIMIENTO EN VIVO", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onBackground)
            LiveDot()
        }
        Row(modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)).border(1.dp, MaterialTheme.colorScheme.outlineVariant).padding(4.dp)) {
            TabButton(text = "Mesas", isSelected = selectedTab == 0, onClick = { onTabChange(0) }, modifier = Modifier.weight(1f))
            TabButton(text = "Meseros", isSelected = selectedTab == 1, onClick = { onTabChange(1) }, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun LiveDot() {
    val infiniteTransition = rememberInfiniteTransition(label = "live")
    val alpha by infiniteTransition.animateFloat(0.4f, 1f, infiniteRepeatable(tween(1000), RepeatMode.Reverse), label = "alpha")
    Box(modifier = Modifier.size(10.dp).alpha(alpha).background(MaterialTheme.colorScheme.primary, CircleShape))
}

@Composable
fun TabButton(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.height(40.dp).background(if (isSelected) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text(text = text.uppercase(), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun StatisticsKpiGrid(uiState: StatisticsUiState) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        KpiCard(modifier = Modifier.weight(1f), label = "Ocupación", value = "${uiState.occupancy}%", icon = Icons.Default.GridOn, color = MaterialTheme.colorScheme.primary)
        KpiCard(modifier = Modifier.weight(1f), label = "Promedio", value = "${uiState.avgTime}m", icon = Icons.Default.Timer, color = MaterialTheme.colorScheme.tertiary)
        KpiCard(modifier = Modifier.weight(1f), label = "Tickets", value = uiState.openTickets.toString(), icon = Icons.AutoMirrored.Filled.ReceiptLong, color = MaterialTheme.colorScheme.secondary)
    }
}

@Composable
fun KpiCard(modifier: Modifier = Modifier, label: String, value: String, icon: ImageVector, color: Color) {
    Surface(modifier = modifier.height(120.dp), color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Icon(imageVector = icon, contentDescription = null, tint = color)
            Column {
                Text(text = value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                Text(text = label.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun OrderFlowChart(flow: List<Float>) {
    Surface(modifier = Modifier.fillMaxWidth().height(200.dp), color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "FLUJO DE PEDIDOS", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                flow.forEach { Box(modifier = Modifier.weight(1f).fillMaxHeight(it).background(MaterialTheme.colorScheme.primary)) }
            }
        }
    }
}

@Composable
fun TableMapSection(tables: List<TableActivity>) {
    Surface(modifier = Modifier.fillMaxWidth().height(200.dp), color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "MAPA DE MESAS", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(top = 8.dp), verticalArrangement = Arrangement.spacedBy(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                items(tables) { table ->
                    Box(modifier = Modifier.aspectRatio(1f).background(MaterialTheme.colorScheme.surfaceVariant).border(1.dp, MaterialTheme.colorScheme.outlineVariant), contentAlignment = Alignment.Center) {
                        Text(text = table.id, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
fun TopWaitersSection(waiters: List<WaiterProductivity>, onSeeAll: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "TOP MESEROS", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Text(text = "VER TODOS", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary), modifier = Modifier.clickable(onClick = onSeeAll))
            }
            waiters.forEach { waiter ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = waiter.name, style = MaterialTheme.typography.bodyMedium)
                    Text(text = waiter.sales, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}

@Composable
fun StatisticsGridBackground() {
    Canvas(modifier = Modifier.fillMaxSize().alpha(0.05f)) {
        val spacing = 20.dp.toPx()
        for (x in 0 until size.width.toInt() step spacing.toInt()) { drawLine(Color.White, Offset(x.toFloat(), 0f), Offset(x.toFloat(), size.height)) }
        for (y in 0 until size.height.toInt() step spacing.toInt()) { drawLine(Color.White, Offset(0f, y.toFloat()), Offset(size.width, y.toFloat())) }
    }
}

@Preview(showBackground = true)
@Composable
fun StatisticsPreview() {
    NeonBeatsTheme {
        StatisticsContent(StatisticsUiState(), "04", {}, {}, {}, {}, {}, {}, {})
    }
}
