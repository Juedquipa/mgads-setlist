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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mgasd.neonbeatssetlits.ui.theme.NeonBeatsTheme
import com.mgasd.neonbeatssetlits.viewmodel.ActivityLog
import com.mgasd.neonbeatssetlits.viewmodel.AdminViewModel
import com.mgasd.neonbeatssetlits.viewmodel.LogStatus
import com.mgasd.neonbeatssetlits.viewmodel.StaffManagementUiState
import com.mgasd.neonbeatssetlits.viewmodel.StaffMember
import com.mgasd.neonbeatssetlits.viewmodel.StaffStatus

@Composable
fun StaffManagementScreen(
    viewModel: AdminViewModel,
    onNavigateToDashboard: () -> Unit,
    onNavigateToQueue: () -> Unit,
    onNavigateToApprovals: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToConfig: () -> Unit
) {
    val uiState by viewModel.staffState.collectAsStateWithLifecycle()
    val mesaNumero by viewModel.mesaNumero.collectAsStateWithLifecycle()

    StaffManagementContent(
        uiState = uiState,
        mesaNumero = mesaNumero,
        onAddStaff = viewModel::onAddStaffClick,
        onFilter = viewModel::onFilterStaffClick,
        onDisableStaff = viewModel::onDisableStaff,
        onNavigateToDashboard = onNavigateToDashboard,
        onNavigateToQueue = onNavigateToQueue,
        onNavigateToApprovals = onNavigateToApprovals,
        onNavigateToStats = onNavigateToStats,
        onNavigateToConfig = onNavigateToConfig
    )
}

@Composable
fun StaffManagementContent(
    uiState: StaffManagementUiState,
    mesaNumero: String,
    onAddStaff: () -> Unit,
    onFilter: () -> Unit,
    onDisableStaff: (String) -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToQueue: () -> Unit,
    onNavigateToApprovals: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToConfig: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            StaffTopBar(mesaNumero = mesaNumero)
        },
        bottomBar = {
            AdminBottomNavBar(
                onHome = onNavigateToDashboard,
                onQueue = onNavigateToQueue,
                onStats = onNavigateToStats,
                onStaff = { /* Already here */ },
                onConfig = onNavigateToConfig
            )
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
                StaffManagementHeader(onFilter = onFilter)
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        AddStaffCard(modifier = Modifier.weight(1f), onClick = onAddStaff)
                        uiState.staffMembers.firstOrNull { it.status == StaffStatus.ACTIVE }?.let { activeStaff ->
                            ActiveStaffCard(modifier = Modifier.weight(1f), staff = activeStaff)
                        }
                    }
                    uiState.staffMembers.firstOrNull { it.status == StaffStatus.INACTIVE }?.let { inactiveStaff ->
                        InactiveStaffCard(staff = inactiveStaff, onDisable = { onDisableStaff(inactiveStaff.id) })
                    }
                }
            }

            item {
                ActivityLogSection(logs = uiState.activityLogs)
            }
        }
    }
}

@Composable
fun StaffTopBar(mesaNumero: String) {
    Surface(color = MaterialTheme.colorScheme.background, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Row(modifier = Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { }) { Icon(Icons.Default.Menu, null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)) }
            Text(text = "STAFF MANAGEMENT", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
            Text(text = "TABLE $mesaNumero", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun StaffManagementHeader(onFilter: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = "ACTIVE ROSTER", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onBackground)
        IconButton(onClick = onFilter) { Icon(Icons.Default.FilterList, null) }
    }
}

@Composable
fun AddStaffCard(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(modifier = modifier.height(180.dp).clickable(onClick = onClick), color = MaterialTheme.colorScheme.surface, border = BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(Icons.Default.PersonAdd, null, modifier = Modifier.size(32.dp))
            Text(text = "ADD STAFF", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun ActiveStaffCard(modifier: Modifier = Modifier, staff: StaffMember) {
    Surface(modifier = modifier.height(180.dp), color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Text(text = staff.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text(text = "ID: ${staff.employeeId}", style = MaterialTheme.typography.labelSmall)
            Text(text = "ACTIVE", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun InactiveStaffCard(staff: StaffMember, onDisable: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth().height(100.dp), color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(text = staff.name, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, textDecoration = TextDecoration.LineThrough))
                Text(text = "INACTIVE", style = MaterialTheme.typography.labelSmall)
            }
            IconButton(onClick = onDisable) { Icon(Icons.Default.PersonOff, null, tint = MaterialTheme.colorScheme.error) }
        }
    }
}

@Composable
fun ActivityLogSection(logs: List<ActivityLog>) {
    Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "RECENT ACTIVITY", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            logs.forEach { log ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = log.staffMember, style = MaterialTheme.typography.bodySmall)
                    Text(text = log.action, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f).padding(horizontal = 8.dp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(text = log.timestamp, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StaffManagementPreview() {
    NeonBeatsTheme {
        StaffManagementContent(StaffManagementUiState(), "04", {}, {}, {}, {}, {}, {}, {}, {})
    }
}
