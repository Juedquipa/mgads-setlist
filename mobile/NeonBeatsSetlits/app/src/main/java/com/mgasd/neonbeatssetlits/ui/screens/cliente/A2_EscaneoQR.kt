package com.mgasd.neonbeatssetlits.ui.screens.cliente

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mgasd.neonbeatssetlits.ui.theme.NeonBeatsTheme
import com.mgasd.neonbeatssetlits.ui.theme.NeonGreen
import com.mgasd.neonbeatssetlits.viewmodel.ClienteViewModel

@Composable
fun A2_EscaneoQR(
    viewModel: ClienteViewModel,
    onBack: () -> Unit,
    onMesaIdentificada: () -> Unit
) {
    val isFlashlightOn by viewModel.isFlashlightOn.collectAsState()
    val session by viewModel.session.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(session) {
        if (session != null) {
            onMesaIdentificada()
        }
    }

    A2_EscaneoQRContent(
        isFlashlightOn = isFlashlightOn,
        onBack = onBack,
        onToggleFlashlight = { viewModel.toggleFlashlight() },
        onHelpClick = { viewModel.onHelpClick() }
    )
}

@Composable
fun A2_EscaneoQRContent(
    isFlashlightOn: Boolean,
    onBack: () -> Unit,
    onToggleFlashlight: () -> Unit,
    onHelpClick: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Black
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. Simulated Camera Feed
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0C1609))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = NeonGreen.copy(alpha = 0.05f),
                        radius = 400f,
                        center = Offset(size.width * 0.2f, size.height * 0.3f)
                    )
                    drawCircle(
                        color = Color(0xFFFF2D78).copy(alpha = 0.03f),
                        radius = 300f,
                        center = Offset(size.width * 0.8f, size.height * 0.7f)
                    )
                }
            }

            // 2. Camera Cutout Overlay (Uses CompositingStrategy for BlendMode.Clear)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                    .drawWithContent {
                        val canvasWidth = size.width
                        val canvasHeight = size.height
                        val viewfinderSize = 260.dp.toPx()
                        val left = (canvasWidth - viewfinderSize) / 2
                        val top = (canvasHeight - viewfinderSize) / 2
                        
                        // Full dark overlay
                        drawRect(color = Color(0xCC0A0A0A))
                        
                        // Viewfinder cutout
                        drawRoundRect(
                            color = Color.Transparent,
                            topLeft = Offset(left, top),
                            size = Size(viewfinderSize, viewfinderSize),
                            cornerRadius = CornerRadius(12.dp.toPx()),
                            blendMode = BlendMode.Clear
                        )
                    }
            )

            // 3. UI Layer
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "NEON BEATS",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-1).sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    IconButton(
                        onClick = onToggleFlashlight,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = if (isFlashlightOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                            contentDescription = "Linterna",
                            tint = if (isFlashlightOn) MaterialTheme.colorScheme.primary else Color.White
                        )
                    }

                    Spacer(modifier = Modifier.size(40.dp))
                }

                Spacer(modifier = Modifier.weight(0.4f))

                // Viewfinder Title
                Text(
                    text = "Apunta al código QR\nde tu mesa",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 28.sp
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 40.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Viewfinder Visuals
                ViewfinderVisuals()

                Spacer(modifier = Modifier.weight(0.6f))

                // Footer Help Button
                OutlinedButton(
                    onClick = onHelpClick,
                    modifier = Modifier
                        .padding(bottom = 48.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "¿PROBLEMAS? PIDE AYUDA AL PERSONAL",
                        style = MaterialTheme.typography.labelLarge.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = NeonGreen)
                }
            }

            error?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 120.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ViewfinderVisuals() {
    val infiniteTransition = rememberInfiniteTransition(label = "ViewfinderAnim")
    
    val cornersAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CornersAlpha"
    )

    val scanLineProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ScanLine"
    )

    Box(
        modifier = Modifier.size(260.dp),
        contentAlignment = Alignment.Center
    ) {
        // Pulsing Corners
        ViewfinderCorners(alpha = cornersAlpha)
        
        // Scanning Line
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .fillMaxHeight()
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val y = size.height * scanLineProgress
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            NeonGreen.copy(alpha = 0.8f),
                            Color.Transparent
                        )
                    ),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
                
                // Glow effect for the line
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            NeonGreen.copy(alpha = 0.1f),
                            Color.Transparent
                        ),
                        startY = y,
                        endY = y + 20.dp.toPx()
                    ),
                    topLeft = Offset(0f, y),
                    size = Size(size.width, 20.dp.toPx())
                )
            }
        }
    }
}

@Composable
fun ViewfinderCorners(alpha: Float) {
    val cornerSize = 40.dp
    val strokeWidth = 5.dp
    val color = NeonGreen.copy(alpha = alpha)

    Box(modifier = Modifier.fillMaxSize()) {
        // Top Left
        Canvas(modifier = Modifier.size(cornerSize).align(Alignment.TopStart)) {
            drawPath(
                path = Path().apply {
                    moveTo(0f, cornerSize.toPx())
                    lineTo(0f, 0f)
                    lineTo(cornerSize.toPx(), 0f)
                },
                color = color,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        // Top Right
        Canvas(modifier = Modifier.size(cornerSize).align(Alignment.TopEnd)) {
            drawPath(
                path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(cornerSize.toPx(), 0f)
                    lineTo(cornerSize.toPx(), cornerSize.toPx())
                },
                color = color,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        // Bottom Left
        Canvas(modifier = Modifier.size(cornerSize).align(Alignment.BottomStart)) {
            drawPath(
                path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(0f, cornerSize.toPx())
                    lineTo(cornerSize.toPx(), cornerSize.toPx())
                },
                color = color,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        // Bottom Right
        Canvas(modifier = Modifier.size(cornerSize).align(Alignment.BottomEnd)) {
            drawPath(
                path = Path().apply {
                    moveTo(0f, cornerSize.toPx())
                    lineTo(cornerSize.toPx(), cornerSize.toPx())
                    lineTo(cornerSize.toPx(), 0f)
                },
                color = color,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun A2_EscaneoQRPreview() {
    NeonBeatsTheme {
        A2_EscaneoQRContent(
            isFlashlightOn = false,
            onBack = {},
            onToggleFlashlight = {},
            onHelpClick = {}
        )
    }
}
