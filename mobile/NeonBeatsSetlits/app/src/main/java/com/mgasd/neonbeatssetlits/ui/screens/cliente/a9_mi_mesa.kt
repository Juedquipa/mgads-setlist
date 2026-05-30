package com.mgasd.neonbeatssetlits.ui.screens.cliente

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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mgasd.neonbeatssetlits.ui.theme.NeonBeatsTheme
import com.mgasd.neonbeatssetlits.viewmodel.ClienteViewModel
import com.mgasd.neonbeatssetlits.viewmodel.OrderHistoryItem
import com.mgasd.neonbeatssetlits.viewmodel.OrderStatus

@Composable
fun MiMesaScreen(
    viewModel: ClienteViewModel
) {
    val mesaNumero by viewModel.mesaNumero.collectAsStateWithLifecycle()
    val ordersCount by viewModel.ordersCount.collectAsStateWithLifecycle()
    val songsCount by viewModel.songsCount.collectAsStateWithLifecycle()
    val availableCredits by viewModel.availableCredits.collectAsStateWithLifecycle()
    val orderHistory by viewModel.orderHistory.collectAsStateWithLifecycle()

    MiMesaContent(
        mesaNumero = mesaNumero,
        ordersCount = ordersCount,
        songsCount = songsCount,
        availableCredits = availableCredits,
        orderHistory = orderHistory,
        onReloadCredits = { viewModel.onReloadCreditsClick() },
        onNewOrderCode = { viewModel.onEnterOrderCodeClick() },
        onHomeClick = { viewModel.onHomeClick() },
        onRequestsClick = { viewModel.onRequestsClick() },
        onMenuClick = { viewModel.onMenuClick() },
        onBillsClick = { viewModel.onBillsClick() },
        onProfileClick = { viewModel.onProfileClick() }
    )
}

@Composable
fun MiMesaContent(
    mesaNumero: String,
    ordersCount: Int,
    songsCount: Int,
    availableCredits: Int,
    orderHistory: List<OrderHistoryItem>,
    onReloadCredits: () -> Unit,
    onNewOrderCode: () -> Unit,
    onHomeClick: () -> Unit,
    onRequestsClick: () -> Unit,
    onMenuClick: () -> Unit,
    onBillsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            MiMesaTopBar(mesaNumero = mesaNumero)
        },
        bottomBar = {
            MiMesaBottomBar(
                onHomeClick = onHomeClick,
                onRequestsClick = onRequestsClick,
                onMenuClick = onMenuClick,
                onBillsClick = onBillsClick,
                onProfileClick = onProfileClick
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            MiMesaScanlinesOverlay()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp)
            ) {
                // Table Hero Section
                item {
                    TableHeroSection(mesaNumero = mesaNumero)
                }

                // Summary Bento Section
                item {
                    SummaryBentoSection(
                        ordersCount = ordersCount,
                        songsCount = songsCount,
                        availableCredits = availableCredits,
                        onReloadCredits = onReloadCredits
                    )
                }

                // Action Button
                item {
                    Button(
                        onClick = onNewOrderCode,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.AddCircle, contentDescription = null)
                            Text(
                                text = "INGRESAR NUEVO CÓDIGO DE PEDIDO",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // History Section
                item {
                    Text(
                        text = "HISTORIAL DE PEDIDOS",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
                }

                items(orderHistory) { order ->
                    OrderHistoryCard(order = order)
                }
            }
        }
    }
}

@Composable
fun MiMesaTopBar(mesaNumero: String) {
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
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    modifier = Modifier.clickable { }
                )
                Text(
                    text = "NEON BEATS SETLIST",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-1).sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = "TABLE $mesaNumero",
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun TableHeroSection(mesaNumero: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = MaterialTheme.shapes.extraLarge,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            // Atmospheric gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "MI MESA",
                    style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 4.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = mesaNumero,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 120.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 120.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = CircleShape,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                        )
                        Text(
                            text = "ACTIVA",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryBentoSection(
    ordersCount: Int,
    songsCount: Int,
    availableCredits: Int,
    onReloadCredits: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SummarySmallCard(
                modifier = Modifier.weight(1f),
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                count = ordersCount.toString(),
                label = "PEDIDOS",
                color = MaterialTheme.colorScheme.tertiary
            )
            SummarySmallCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.QueueMusic,
                count = songsCount.toString(),
                label = "CANCIONES",
                color = MaterialTheme.colorScheme.secondary
            )
        }
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(
                        imageVector = Icons.Default.Toll,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Column {
                        Text(
                            text = "CRÉDITOS DISPONIBLES",
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = availableCredits.toString(),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                OutlinedButton(
                    onClick = onReloadCredits,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = "RECARGAR", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SummarySmallCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: String,
    label: String,
    color: Color
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = count,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun OrderHistoryCard(order: OrderHistoryItem) {
    val statusColor = when (order.status) {
        OrderStatus.PREPARANDO -> MaterialTheme.colorScheme.tertiary
        OrderStatus.ENTREGADO -> MaterialTheme.colorScheme.primary
        OrderStatus.CANCELADO -> MaterialTheme.colorScheme.error
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = 8.dp, bottomEnd = 8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Status bar on the left
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(statusColor)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(start = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = order.orderNumber,
                            style = MaterialTheme.typography.labelLarge.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Surface(
                            color = statusColor.copy(alpha = 0.1f),
                            shape = CircleShape,
                            border = BorderStroke(1.dp, statusColor.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = order.status.name,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = statusColor
                            )
                        }
                    }
                    Text(
                        text = order.itemsSummary,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Text(
                        text = order.timeAgo,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Text(
                    text = order.amount,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun MiMesaBottomBar(
    onHomeClick: () -> Unit,
    onRequestsClick: () -> Unit,
    onMenuClick: () -> Unit,
    onBillsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .height(80.dp)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        NavigationBarItem(
            selected = false,
            onClick = onHomeClick,
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("HOME", style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp)) }
        )
        NavigationBarItem(
            selected = false,
            onClick = onRequestsClick,
            icon = { Icon(Icons.AutoMirrored.Filled.QueueMusic, contentDescription = "Requests") },
            label = { Text("REQUESTS", style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp)) }
        )
        NavigationBarItem(
            selected = false,
            onClick = onMenuClick,
            icon = { Icon(Icons.Default.RestaurantMenu, contentDescription = "Menu") },
            label = { Text("MENU", style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp)) }
        )
        NavigationBarItem(
            selected = true,
            onClick = onBillsClick,
            icon = { Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = "Bills") },
            label = { Text("BILLS", style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp)) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onProfileClick,
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("PROFILE", style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp)) }
        )
    }
}

@Composable
fun MiMesaScanlinesOverlay() {
    Canvas(modifier = Modifier.fillMaxSize().alpha(0.05f)) {
        val lineSpacing = 4.dp.toPx()
        for (y in 0 until size.height.toInt() step lineSpacing.toInt()) {
            drawLine(
                color = Color.Black,
                start = Offset(0f, y.toFloat()),
                end = Offset(size.width, y.toFloat()),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MiMesaPreview() {
    NeonBeatsTheme {
        MiMesaContent(
            mesaNumero = "04",
            ordersCount = 12,
            songsCount = 5,
            availableCredits = 2500,
            orderHistory = listOf(
                OrderHistoryItem("1", "#ORD-9921", "2x Cerveza Artesanal, 1x Nachos", "Hace 15 min", "$24.50", OrderStatus.ENTREGADO),
                OrderHistoryItem("2", "#ORD-9924", "1x Hamburguesa Doble, 1x Papas Fritas", "Hace 5 min", "$18.00", OrderStatus.PREPARANDO),
                OrderHistoryItem("3", "#ORD-9880", "4x Shots Tequila", "Hace 45 min", "$20.00", OrderStatus.ENTREGADO)
            ),
            onReloadCredits = {},
            onNewOrderCode = {},
            onHomeClick = {},
            onRequestsClick = {},
            onMenuClick = {},
            onBillsClick = {},
            onProfileClick = {}
        )
    }
}
