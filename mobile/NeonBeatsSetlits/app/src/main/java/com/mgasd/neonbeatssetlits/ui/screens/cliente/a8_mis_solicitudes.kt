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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mgasd.neonbeatssetlits.ui.theme.NeonBeatsTheme
import com.mgasd.neonbeatssetlits.viewmodel.ClienteViewModel
import com.mgasd.neonbeatssetlits.viewmodel.RequestStatus
import com.mgasd.neonbeatssetlits.viewmodel.UserRequest

@Composable
fun MisSolicitudesScreen(
    viewModel: ClienteViewModel
) {
    val mesaNumero by viewModel.mesaNumero.collectAsStateWithLifecycle()
    val usedCredits by viewModel.usedCredits.collectAsStateWithLifecycle()
    val totalCredits by viewModel.totalCredits.collectAsStateWithLifecycle()
    val userRequests by viewModel.userRequests.collectAsStateWithLifecycle()

    MisSolicitudesContent(
        mesaNumero = mesaNumero,
        usedCredits = usedCredits,
        totalCredits = totalCredits,
        userRequests = userRequests,
        onHomeClick = { viewModel.onHomeClick() },
        onRequestsClick = { viewModel.onRequestsClick() },
        onMenuClick = { viewModel.onMenuClick() },
        onBillsClick = { viewModel.onBillsClick() },
        onProfileClick = { viewModel.onProfileClick() }
    )
}

@Composable
fun MisSolicitudesContent(
    mesaNumero: String,
    usedCredits: Int,
    totalCredits: Int,
    userRequests: List<UserRequest>,
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
            RequestsTopBar(mesaNumero = mesaNumero)
        },
        bottomBar = {
            RequestsBottomBar(
                onHomeClick = onHomeClick,
                onRequestsClick = onRequestsClick,
                onMenuClick = onMenuClick,
                onBillsClick = onBillsClick,
                onProfileClick = onProfileClick
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            RequestsScanlinesOverlay()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp)
            ) {
                // Header
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "MIS SOLICITUDES",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Rastrea tus temas pedidos esta noche.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Credit Widget
                item {
                    CreditCounterWidget(used = usedCredits, total = totalCredits)
                }

                // List
                items(userRequests) { request ->
                    RequestItemCard(request = request)
                }
            }
        }
    }
}

@Composable
fun RequestsTopBar(mesaNumero: String) {
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
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-1).sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Surface(
                color = MaterialTheme.colorScheme.background,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shape = MaterialTheme.shapes.extraSmall
            ) {
                Text(
                    text = "TABLE $mesaNumero",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun CreditCounterWidget(used: Int, total: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Icon background hint
            Icon(
                imageVector = Icons.Default.Toll,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(64.dp)
                    .alpha(0.1f),
                tint = MaterialTheme.colorScheme.primary
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "CRÉDITOS USADOS",
                        style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = used.toString(),
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "/ $total",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                ) {
                    val progress = used.toFloat() / total.toFloat()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .border(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
fun RequestItemCard(request: UserRequest) {
    val borderColor = when (request.status) {
        RequestStatus.IN_QUEUE -> MaterialTheme.colorScheme.primary
        RequestStatus.PENDING -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)
        RequestStatus.REJECTED -> MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
        RequestStatus.PLAYED -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    }

    val alpha = if (request.status == RequestStatus.PLAYED) 0.6f else 1f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha)
            .border(
                1.dp,
                borderColor,
                MaterialTheme.shapes.medium
            ),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Album Art Placeholder
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (request.status == RequestStatus.PENDING) Icons.Default.Album else Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = when (request.status) {
                            RequestStatus.IN_QUEUE -> MaterialTheme.colorScheme.primary
                            RequestStatus.PENDING -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }

                Column {
                    Text(
                        text = request.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            textDecoration = if (request.status == RequestStatus.REJECTED) TextDecoration.LineThrough else null
                        ),
                        color = if (request.status == RequestStatus.REJECTED) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = request.artist,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatusBadge(status = request.status)
                
                if (request.status == RequestStatus.IN_QUEUE) {
                    RequestEqualizer()
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: RequestStatus) {
    val (text, color, icon) = when (status) {
        RequestStatus.IN_QUEUE -> Triple("EN COLA", MaterialTheme.colorScheme.primary, Icons.Default.CheckCircle)
        RequestStatus.PENDING -> Triple("PENDIENTE", MaterialTheme.colorScheme.tertiary, Icons.Default.HourglassEmpty)
        RequestStatus.REJECTED -> Triple("RECHAZADA", MaterialTheme.colorScheme.error, Icons.Default.Close)
        RequestStatus.PLAYED -> Triple("YA SONÓ", MaterialTheme.colorScheme.onSurfaceVariant, Icons.Default.CheckCircle)
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = CircleShape,
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (status == RequestStatus.IN_QUEUE) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(color, CircleShape)
                        .alpha(0.8f)
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = color
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }
    }
}

@Composable
fun RequestEqualizer() {
    Row(
        modifier = Modifier.height(20.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "eq")
        val durations = listOf(600, 800, 500, 700, 900)
        durations.forEach { duration ->
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(duration, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar"
            )
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight(scale)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
fun RequestsBottomBar(
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
            selected = true,
            onClick = onRequestsClick,
            icon = { Icon(Icons.AutoMirrored.Filled.QueueMusic, contentDescription = "Requests") },
            label = { Text("REQUESTS", style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp)) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onMenuClick,
            icon = { Icon(Icons.Default.RestaurantMenu, contentDescription = "Menu") },
            label = { Text("MENU", style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp)) }
        )
        NavigationBarItem(
            selected = false,
            onClick = onBillsClick,
            icon = { Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = "Bills") },
            label = { Text("BILLS", style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp)) }
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
fun RequestsScanlinesOverlay() {
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
fun MisSolicitudesPreview() {
    NeonBeatsTheme {
        MisSolicitudesContent(
            mesaNumero = "04",
            usedCredits = 4,
            totalCredits = 10,
            userRequests = listOf(
                UserRequest("1", "Neon Knights", "Black Sabbath", RequestStatus.IN_QUEUE),
                UserRequest("2", "Midnight City", "M83", RequestStatus.PENDING),
                UserRequest("3", "Despacito", "Luis Fonsi", RequestStatus.REJECTED),
                UserRequest("4", "Blue Monday", "New Order", RequestStatus.PLAYED)
            ),
            onHomeClick = {},
            onRequestsClick = {},
            onMenuClick = {},
            onBillsClick = {},
            onProfileClick = {}
        )
    }
}
