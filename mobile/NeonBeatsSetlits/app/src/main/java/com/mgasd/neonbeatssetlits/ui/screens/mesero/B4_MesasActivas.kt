package com.mgasd.neonbeatssetlits.ui.screens.mesero

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mgasd.neonbeatssetlits.ui.theme.NeonBeatsTheme
import com.mgasd.neonbeatssetlits.viewmodel.ActiveTable
import com.mgasd.neonbeatssetlits.viewmodel.MeseroViewModel
import com.mgasd.neonbeatssetlits.viewmodel.TableStatus

/**
 * Pantalla B4 - Mesas Activas
 * Estética: Industrial Neon Underground
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun B4_MesasActivasScreen(
    viewModel: MeseroViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToRequests: () -> Unit,
    onNavigateToBills: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val state by viewModel.tablesState.collectAsStateWithLifecycle()
    var showCreateTableDialog by remember { mutableStateOf(false) }
    var newTableName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "NEON BEATS SETLIST",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* Drawer */ }) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(2.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {
                        Text(
                            "STAFF VIEW",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            StaffBottomNavigation(
                onHomeClick = onNavigateToHome,
                onRequestsClick = onNavigateToRequests,
                onTablesClick = { /* Already here */ },
                onBillsClick = onNavigateToBills,
                onProfileClick = onNavigateToProfile
            )
        },
        floatingActionButton = {
            // El FAB se define aquí, dentro del slot oficial del Scaffold
            FloatingActionButton(
                onClick = { showCreateTableDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                modifier = Modifier.shadow(12.dp, CircleShape, spotColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Table")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Scanlines industrial overlay (como fondo del contenido)
            Canvas(modifier = Modifier.fillMaxSize().alpha(0.05f)) {
                val scanlineSpacing = 4.dp.toPx()
                for (y in 0 until size.height.toInt() step scanlineSpacing.toInt()) {
                    drawLine(
                        color = Color.White,
                        start = Offset(0f, y.toFloat()),
                        end = Offset(size.width, y.toFloat()),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                // Floor Status Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                "FLOOR STATUS",
                                style = MaterialTheme.typography.displaySmall.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = (-1).sp
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                "Real-time table monitoring & requests",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatusBadge(
                                label = "ACTIVE (${state.activeTablesCount})",
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            StatusBadge(
                                label = "SESSION (${state.sessionTablesCount})",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Divider(
                        modifier = Modifier.padding(top = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant,
                        thickness = 1.dp
                    )
                }

                // Table Grid
                items(state.tables) { table ->
                    TableCard(
                        table = table,
                        onAction = {
                            if (table.status == TableStatus.CALLING) viewModel.onAcknowledgeTable(
                                table.id
                            )
                        },
                        onDelete = { viewModel.deleteTable(table.id) }
                    )
                }

                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }

    if (showCreateTableDialog) {
        AlertDialog(
            onDismissRequest = {
                showCreateTableDialog = false
                newTableName = ""
            },
            title = {
                Text(text = "CREATE TABLE")
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Ingrese el nombre de la mesa para crearla en la API.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = newTableName,
                        onValueChange = { newTableName = it },
                        singleLine = true,
                        label = { Text("Mesa") },
                        placeholder = { Text("Ej. Terraza 1") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val tableName = newTableName.trim()
                        if (tableName.isNotEmpty()) {
                            viewModel.createTable(tableName)
                            showCreateTableDialog = false
                            newTableName = ""
                        }
                    },
                    enabled = newTableName.isNotBlank()
                ) {
                    Text("CREATE")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showCreateTableDialog = false
                        newTableName = ""
                    }
                ) {
                    Text("CANCEL")
                }
            }
        )
    }
}

@Composable
fun StatusBadge(label: String, color: Color) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(modifier = Modifier
                .size(6.dp)
                .background(color, CircleShape))
            Text(
                label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }
    }
}

@Composable
fun TableCard(
    table: ActiveTable,
    onAction: () -> Unit,
    onDelete: () -> Unit
) {
    val borderColor = when (table.status) {
        TableStatus.ACTIVE, TableStatus.CALLING -> MaterialTheme.colorScheme.tertiary
        TableStatus.SESSION -> MaterialTheme.colorScheme.primary
        TableStatus.EMPTY -> MaterialTheme.colorScheme.outlineVariant
    }

    val glowColor = borderColor.copy(alpha = 0.4f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(if (table.status != TableStatus.EMPTY) 8.dp else 0.dp, spotColor = glowColor)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(2.dp))
            .border(1.dp, borderColor, RoundedCornerShape(2.dp))
            .padding(20.dp)
    ) {
        // Delete Action
        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(32.dp)
                .offset(x = 10.dp, y = (-10).dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(16.dp)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        table.id,
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.SansSerif
                        ),
                        color = borderColor
                    )

                    Surface(
                        color = borderColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(2.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            borderColor.copy(alpha = 0.4f)
                        )
                    ) {
                        Text(
                            table.statusLabel.uppercase(),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = borderColor
                        )
                    }
                }

                if (table.isPlaying) {
                    EqualizerAnimation()
                } else {
                    Icon(
                        imageVector = when (table.status) {
                            TableStatus.CALLING -> Icons.Default.NotificationsActive
                            TableStatus.EMPTY -> Icons.Default.Chair
                            else -> Icons.Default.PriorityHigh
                        },
                        contentDescription = null,
                        tint = borderColor,
                        modifier = if (table.status == TableStatus.CALLING) Modifier.size(24.dp) else Modifier.size(
                            24.dp
                        )
                    )
                }
            }

            if (table.status == TableStatus.EMPTY) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            RoundedCornerShape(2.dp)
                        )
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Ready for seating",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TableMetric(
                        modifier = Modifier.weight(1f),
                        label = "ORDERS",
                        value = table.pendingOrders.toString(),
                        subLabel = "Pending",
                        icon = Icons.Default.ReceiptLong
                    )
                    TableMetric(
                        modifier = Modifier.weight(1f),
                        label = "REQUESTS",
                        value = table.queuedRequests.toString(),
                        subLabel = if (table.isPlaying) "Playing" else "Queued",

                        icon = Icons.Default.QueueMusic
                    )
                }
            }

            Button(
                onClick = onAction,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (table.status == TableStatus.EMPTY) Color.Transparent else borderColor,
                    contentColor = if (table.status == TableStatus.EMPTY) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary
                ),
                border = if (table.status == TableStatus.EMPTY) androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline
                ) else null
            ) {
                Text(
                    text = when (table.status) {
                        TableStatus.ACTIVE -> "VIEW ORDERS"
                        TableStatus.SESSION -> "MANAGE"
                        TableStatus.EMPTY -> "ASSIGN"
                        TableStatus.CALLING -> "ACKNOWLEDGE"
                    },
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                )
            }
        }
    }
}

@Composable
fun TableMetric(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    subLabel: String,
    icon: ImageVector
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(2.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(2.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                subLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
    }
}

@Composable
fun EqualizerAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "equalizer")

    Row(
        modifier = Modifier.height(24.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(5) { index ->
            val height by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 400 + (index * 100),
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar_$index"
            )

            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight(height)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
fun StaffBottomNavigation(
    onHomeClick: () -> Unit,
    onRequestsClick: () -> Unit,
    onTablesClick: () -> Unit,
    onBillsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .height(80.dp)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(0.dp)),
        tonalElevation = 0.dp
    ) {
        val items = listOf(
            Triple(Icons.Default.Home, "HOME", false),
            Triple(Icons.Default.QueueMusic, "REQUESTS", false),
            Triple(Icons.Default.RestaurantMenu, "TABLES", true),
            Triple(Icons.Default.ReceiptLong, "BILLS", false),
            Triple(Icons.Default.Person, "PROFILE", false)
        )

        items.forEach { (icon, label, isSelected) ->
            NavigationBarItem(
                selected = isSelected,
                onClick = { 
                    when(label) {
                        "HOME" -> onHomeClick()
                        "REQUESTS" -> onRequestsClick()
                        "TABLES" -> onTablesClick()
                        "BILLS" -> onBillsClick()
                        "PROFILE" -> onProfileClick()
                    }
                },
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            icon,
                            contentDescription = label,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                alpha = 0.6f
                            ),
                            modifier = if (isSelected) Modifier.shadow(
                                8.dp,
                                CircleShape,
                                spotColor = MaterialTheme.colorScheme.primary
                            ) else Modifier
                        )
                        Text(
                            label,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                alpha = 0.6f
                            )
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun B4_MesasActivasScreenPreview() {
    val meseroViewModel: MeseroViewModel = viewModel();

    NeonBeatsTheme {
        B4_MesasActivasScreen(
            meseroViewModel,
            onNavigateToHome = {},
            onNavigateToRequests = {},
            onNavigateToBills = {},
            onNavigateToProfile = {}
        )
    }
}
