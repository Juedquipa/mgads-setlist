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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mgasd.neonbeatssetlits.ui.theme.NeonBeatsTheme
import com.mgasd.neonbeatssetlits.viewmodel.ClienteViewModel
import com.mgasd.neonbeatssetlits.viewmodel.SongItem

@Composable
fun BuscarCancionesScreen(
    viewModel: ClienteViewModel
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedSearchTab.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val pendingCount by viewModel.pendingQueueCount.collectAsStateWithLifecycle()
    val mesaNumero by viewModel.mesaNumero.collectAsStateWithLifecycle()

    BuscarCancionesContent(
        searchQuery = searchQuery,
        selectedTab = selectedTab,
        searchResults = searchResults,
        pendingCount = pendingCount,
        mesaNumero = mesaNumero,
        onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
        onTabChange = { viewModel.onSearchTabChange(it) },
        onRequestSong = { viewModel.onRequestSong(it) },
        onHomeClick = { viewModel.onHomeClick() },
        onRequestsClick = { viewModel.onRequestsClick() },
        onMenuClick = { viewModel.onMenuClick() },
        onBillsClick = { viewModel.onBillsClick() },
        onProfileClick = { viewModel.onProfileClick() }
    )
}

@Composable
fun BuscarCancionesContent(
    searchQuery: String,
    selectedTab: Int,
    searchResults: List<SongItem>,
    pendingCount: Int,
    mesaNumero: String,
    onSearchQueryChange: (String) -> Unit,
    onTabChange: (Int) -> Unit,
    onRequestSong: (String) -> Unit,
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
            SearchTopBar(mesaNumero = mesaNumero)
        },
        bottomBar = {
            SearchBottomBar(
                onHomeClick = onHomeClick,
                onRequestsClick = onRequestsClick,
                onMenuClick = onMenuClick,
                onBillsClick = onBillsClick,
                onProfileClick = onProfileClick
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            SearchScanlinesOverlay()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp)
            ) {
                // Search Field
                item {
                    SearchInputField(
                        query = searchQuery,
                        onQueryChange = onSearchQueryChange
                    )
                }

                // Tabs
                item {
                    SearchTabs(
                        selectedIndex = selectedTab,
                        onTabChange = onTabChange
                    )
                }

                // Results Header
                item {
                    ResultsHeader(count = searchResults.size, pendingCount = pendingCount)
                }

                // Search Results
                items(searchResults) { song ->
                    SongCard(
                        song = song,
                        onRequest = { onRequestSong(song.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchTopBar(mesaNumero: String) {
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
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
            }
            Surface(
                color = MaterialTheme.colorScheme.background,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shape = MaterialTheme.shapes.extraSmall
            ) {
                Text(
                    text = "TABLE $mesaNumero",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium.copy(
                        letterSpacing = 2.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun SearchInputField(
    query: String,
    onQueryChange: (String) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    
    val borderColor = if (isFocused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
    val glowAlpha = if (isFocused) 0.5f else 0f

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(1.dp, borderColor, MaterialTheme.shapes.extraSmall),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = if (isFocused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = "Buscar artista, canción o género...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun SearchTabs(
    selectedIndex: Int,
    onTabChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        SearchTabItem(
            text = "CATÁLOGO DEL BAR",
            isSelected = selectedIndex == 0,
            onClick = { onTabChange(0) },
            modifier = Modifier.weight(1f)
        )
        SearchTabItem(
            text = "YOUTUBE",
            isSelected = selectedIndex == 1,
            onClick = { onTabChange(1) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SearchTabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
        )
    }
}

@Composable
fun ResultsHeader(count: Int, pendingCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "RESULTADOS ($count)",
            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = CircleShape,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val dotAlpha by infiniteTransition.animateFloat(
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
                        .alpha(dotAlpha)
                        .background(MaterialTheme.colorScheme.tertiary, CircleShape)
                )
                Text(
                    text = "$pendingCount PENDIENTES EN COLA",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
fun SongCard(
    song: SongItem,
    onRequest: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp, 
                if (song.isFromBarCatalog) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                MaterialTheme.shapes.extraSmall
            ),
        shape = MaterialTheme.shapes.extraSmall,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Thumbnail
                Box(
                    modifier = Modifier
                        .size(if (song.isFromBarCatalog) 80.dp else 120.dp, 80.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (song.isFromBarCatalog) Icons.Default.MusicNote else Icons.Default.SmartDisplay,
                        contentDescription = null,
                        tint = if (song.isFromBarCatalog) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(32.dp)
                    )
                    
                    if (!song.isFromBarCatalog) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(4.dp)
                                .background(Color.Black.copy(alpha = 0.8f), MaterialTheme.shapes.extraSmall)
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = song.duration,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = Color.White
                            )
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, lineHeight = 20.sp),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = song.artist,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = song.duration, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                imageVector = if (song.isFromBarCatalog) Icons.Default.Verified else Icons.Default.SmartDisplay,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = if (song.isFromBarCatalog) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = if (song.isFromBarCatalog) "Catálogo" else "YouTube",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (song.isFromBarCatalog) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
            
            // Action Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .border(width = 1.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    .padding(8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (song.isRequested) {
                    Button(
                        onClick = {},
                        enabled = false,
                        shape = MaterialTheme.shapes.extraSmall,
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor = MaterialTheme.colorScheme.primary,
                            disabledContentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text(text = "SOLICITADA", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                } else {
                    val btnColor = if (song.isFromBarCatalog) MaterialTheme.colorScheme.primary else Color.Transparent
                    val contentColor = if (song.isFromBarCatalog) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.secondary
                    val border = if (song.isFromBarCatalog) null else BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
                    
                    Button(
                        onClick = onRequest,
                        shape = MaterialTheme.shapes.extraSmall,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = btnColor,
                            contentColor = contentColor
                        ),
                        border = border,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text(
                                text = if (song.isFromBarCatalog) "SOLICITAR" else "SOLICITAR YT",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
        
        // Pending Badge Overlay
        if (song.isPending) {
            Surface(
                modifier = Modifier.padding(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                shape = MaterialTheme.shapes.extraSmall,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.tertiary)
                    Text(text = "PENDIENTE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
                }
            }
        }
    }
}

@Composable
fun SearchBottomBar(
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
            icon = { Icon(Icons.Default.QueueMusic, contentDescription = "Requests") },
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
fun SearchScanlinesOverlay() {
    Canvas(modifier = Modifier.fillMaxSize().alpha(0.03f)) {
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
fun BuscarCancionesPreview() {
    NeonBeatsTheme {
        BuscarCancionesContent(
            searchQuery = "",
            selectedTab = 0,
            searchResults = listOf(
                SongItem("1", "Midnight City", "M83 • Hurry Up, We're Dreaming", "4:03", true, isPending = true, isRequested = true),
                SongItem("2", "Starboy", "The Weeknd ft. Daft Punk", "3:50", true),
                SongItem("3", "Daft Punk - Harder, Better, Faster, Stronger (Official Audio)", "DaftPunkVEVO", "5:12", false)
            ),
            pendingCount = 3,
            mesaNumero = "04",
            onSearchQueryChange = {},
            onTabChange = {},
            onRequestSong = {},
            onHomeClick = {},
            onRequestsClick = {},
            onMenuClick = {},
            onBillsClick = {},
            onProfileClick = {}
        )
    }
}
