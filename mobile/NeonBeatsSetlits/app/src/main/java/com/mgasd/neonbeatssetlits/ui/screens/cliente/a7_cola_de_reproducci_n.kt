package com.mgasd.neonbeatssetlits.ui.screens.cliente

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mgasd.neonbeatssetlits.ui.theme.NeonBeatsTheme
import com.mgasd.neonbeatssetlits.viewmodel.ClienteViewModel
import com.mgasd.neonbeatssetlits.viewmodel.QueueItem
import com.mgasd.neonbeatssetlits.viewmodel.TrackInfo

@Composable
fun ColaReproduccionScreen(
    viewModel: ClienteViewModel
) {
    val mesaNumero by viewModel.mesaNumero.collectAsStateWithLifecycle()
    val currentTrack by viewModel.currentTrack.collectAsStateWithLifecycle()
    val playQueue by viewModel.playQueue.collectAsStateWithLifecycle()

    ColaReproduccionContent(
        mesaNumero = mesaNumero,
        currentTrack = currentTrack,
        playQueue = playQueue,
        onHomeClick = { viewModel.onHomeClick() },
        onRequestsClick = { viewModel.onRequestsClick() },
        onMenuClick = { viewModel.onMenuClick() },
        onBillsClick = { viewModel.onBillsClick() },
        onProfileClick = { viewModel.onProfileClick() }
    )
}

@Composable
fun ColaReproduccionContent(
    mesaNumero: String,
    currentTrack: TrackInfo,
    playQueue: List<QueueItem>,
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
            ColaTopBar(mesaNumero = mesaNumero)
        },
        bottomBar = {
            ColaBottomBar(
                onHomeClick = onHomeClick,
                onRequestsClick = onRequestsClick,
                onMenuClick = onMenuClick,
                onBillsClick = onBillsClick,
                onProfileClick = onProfileClick
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            ColaScanlinesOverlay()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp)
            ) {
                // Live Sync Status
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PLAY QUEUE",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        LiveSyncIndicator()
                    }
                }

                // Now Playing Section
                item {
                    NowPlayingQueueCard(track = currentTrack)
                }

                // Queue List
                items(playQueue) { item ->
                    QueueListItem(item = item)
                }
            }
        }
    }
}

@Composable
fun ColaTopBar(mesaNumero: String) {
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
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
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
            Text(
                text = "TABLE $mesaNumero",
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = 2.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun LiveSyncIndicator() {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = CircleShape,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.4f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "alpha"
            )
            
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .alpha(alpha)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
            Text(
                text = "LIVE SYNC",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun NowPlayingQueueCard(track: TrackInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.medium),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Equalizer background hint
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .height(40.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "eq")
                val durations = listOf(600, 800, 500, 700, 900)
                durations.forEach { duration ->
                    val scale by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(duration, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "bar"
                    )
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .fillMaxHeight(scale)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(end = 48.dp), // Space for EQ
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Placeholder for album cover
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }

                Column {
                    Text(
                        text = "NOW PLAYING",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = track.artist,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun QueueListItem(item: QueueItem) {
    val containerModifier = if (item.isUserRequest) {
        Modifier.border(1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(4.dp))
    } else {
        Modifier
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(containerModifier)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(4.dp))
            .padding(16.dp)
    ) {
        if (!item.isUserRequest) {
            // Custom left border for normal items
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = item.position,
                    style = MaterialTheme.typography.labelLarge.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                    color = if (item.isUserRequest) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Column {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = if (item.isUserRequest) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = item.artist,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = item.time,
                    style = MaterialTheme.typography.labelLarge.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                    color = if (item.isUserRequest) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = item.requestedBy,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (item.isUserRequest) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (item.isUserRequest) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 8.dp, y = (-8).dp),
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(2.dp)
            ) {
                Text(
                    text = "YOUR REQUEST",
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 8.sp),
                    color = MaterialTheme.colorScheme.background
                )
            }
        }
    }
}

@Composable
fun ColaBottomBar(
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
fun ColaScanlinesOverlay() {
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
fun ColaReproduccionPreview() {
    NeonBeatsTheme {
        ColaReproduccionContent(
            mesaNumero = "04",
            currentTrack = TrackInfo("Cybernetic Pulse", "DJ Synthwave", 138),
            playQueue = listOf(
                QueueItem("1", "01", "Midnight Runner", "The Midnight", "2:15", "Table 12"),
                QueueItem("2", "02", "Neon Nights", "Kavinsky", "5:30", "Table 04", isUserRequest = true),
                QueueItem("3", "03", "Digital Love", "Daft Punk", "9:45", "Table 08"),
                QueueItem("4", "04", "Resonance", "HOME", "14:20", "Bar")
            ),
            onHomeClick = {},
            onRequestsClick = {},
            onMenuClick = {},
            onBillsClick = {},
            onProfileClick = {}
        )
    }
}
