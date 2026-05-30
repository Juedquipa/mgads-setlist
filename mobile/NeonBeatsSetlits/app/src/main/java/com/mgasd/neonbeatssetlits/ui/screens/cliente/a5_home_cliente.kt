package com.mgasd.neonbeatssetlits.ui.screens.cliente

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mgasd.neonbeatssetlits.ui.theme.NeonBeatsTheme
import com.mgasd.neonbeatssetlits.viewmodel.ClienteViewModel
import com.mgasd.neonbeatssetlits.viewmodel.TrackInfo

@Composable
fun HomeClienteScreen(
    viewModel: ClienteViewModel
) {
    val mesaNumero by viewModel.mesaNumero.collectAsStateWithLifecycle()
    val currentTrack by viewModel.currentTrack.collectAsStateWithLifecycle()
    val availableCredits by viewModel.availableCredits.collectAsStateWithLifecycle()
    val maxCredits by viewModel.maxCredits.collectAsStateWithLifecycle()
    val nextTrack by viewModel.nextTrack.collectAsStateWithLifecycle()

    HomeClienteContent(
        mesaNumero = mesaNumero,
        currentTrack = currentTrack,
        availableCredits = availableCredits,
        maxCredits = maxCredits,
        nextTrack = nextTrack,
        onSearchTrack = { viewModel.onSearchTrackClick() },
        onHomeClick = { viewModel.onHomeClick() },
        onRequestsClick = { viewModel.onRequestsClick() },
        onMenuClick = { viewModel.onMenuClick() },
        onBillsClick = { viewModel.onBillsClick() },
        onProfileClick = { viewModel.onProfileClick() }
    )
}

@Composable
fun HomeClienteContent(
    mesaNumero: String,
    currentTrack: TrackInfo,
    availableCredits: Int,
    maxCredits: Int,
    nextTrack: Pair<String, String>,
    onSearchTrack: () -> Unit,
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
            HomeTopBar(mesaNumero = mesaNumero)
        },
        bottomBar = {
            HomeBottomBar(
                onHomeClick = onHomeClick,
                onRequestsClick = onRequestsClick,
                onMenuClick = onMenuClick,
                onBillsClick = onBillsClick,
                onProfileClick = onProfileClick
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            HomeScanlinesOverlay()
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp)
            ) {
                // Now Playing Card
                item {
                    NowPlayingCard(track = currentTrack)
                }

                // Credits Section
                item {
                    CreditsSection(available = availableCredits, max = maxCredits)
                }

                // Bento Grid: Quick Actions
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SearchPistaButton(
                            modifier = Modifier.weight(1f),
                            onClick = onSearchTrack
                        )
                        NextTrackCard(
                            modifier = Modifier.weight(1f),
                            trackName = nextTrack.first,
                            requestedBy = nextTrack.second
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeTopBar(mesaNumero: String) {
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
            IconButton(onClick = { /* Open menu */ }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                )
            }
            Text(
                text = "NEON BEATS SETLIST",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1).sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
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
fun NowPlayingCard(track: TrackInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background atmospheric hint
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SONANDO AHORA",
                            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    EqualizerAnimation()
                }

                Column {
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-1).sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${track.artist} • ${track.bpm} BPM",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EqualizerAnimation() {
    Row(
        modifier = Modifier.height(24.dp),
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
                    animation = tween(duration, easing = EaseInOut),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar"
            )
            
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight(scale)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
fun CreditsSection(available: Int, max: Int) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = Color.Transparent) // placeholder for bottom border
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "CRÉDITOS DE SOLICITUD",
                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$available / $max DISPONIBLES",
                style = MaterialTheme.typography.labelLarge.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        // Horizontal line simulated since Compose doesn't have border-bottom easily
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outlineVariant))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            repeat(max) { index ->
                val isActive = index < available
                CreditBoltCircle(isActive = isActive)
            }
        }
    }
}

@Composable
fun CreditBoltCircle(isActive: Boolean) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .border(
                1.dp,
                if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Bolt,
            contentDescription = null,
            tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun SearchPistaButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            Column {
                Text(
                    text = "BUSCAR PISTA",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Explora el catálogo",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun NextTrackCard(modifier: Modifier = Modifier, trackName: String, requestedBy: String) {
    Surface(
        modifier = modifier.fillMaxHeight(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FormatListNumbered,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(24.dp)
                )
                Surface(
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Text(
                        text = "A CONTINUACIÓN",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
            
            Column {
                Text(
                    text = trackName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Por $requestedBy",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun HomeBottomBar(
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
            selected = true,
            onClick = onHomeClick,
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("HOME", style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp)) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = Color.Transparent,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onRequestsClick,
            icon = { Icon(Icons.Default.QueueMusic, contentDescription = "Requests") },
            label = { Text("REQUESTS", style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp)) }
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
            icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Bills") },
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
fun HomeScanlinesOverlay() {
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
fun HomeClientePreview() {
    NeonBeatsTheme {
        HomeClienteContent(
            mesaNumero = "04",
            currentTrack = TrackInfo("SYNTHETIC VENOM", "DJ KINETIC", 138),
            availableCredits = 4,
            maxCredits = 4,
            nextTrack = Pair("NEON NIGHTS (VIP MIX)", "Mesa 12"),
            onSearchTrack = {},
            onHomeClick = {},
            onRequestsClick = {},
            onMenuClick = {},
            onBillsClick = {},
            onProfileClick = {}
        )
    }
}
