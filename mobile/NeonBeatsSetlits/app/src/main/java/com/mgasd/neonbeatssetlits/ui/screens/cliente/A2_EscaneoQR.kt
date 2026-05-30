package com.mgasd.neonbeatssetlits.ui.screens.cliente

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.HelpOutline
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.mgasd.neonbeatssetlits.ui.theme.NeonGreen
import com.mgasd.neonbeatssetlits.viewmodel.ClienteViewModel
import java.util.concurrent.Executors

@Composable
fun A2_EscaneoQR(
    viewModel: ClienteViewModel,
    onBack: () -> Unit,
    onMesaIdentificada: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val isFlashlightOn by viewModel.isFlashlightOn.collectAsState()
    val session by viewModel.session.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(session) {
        if (session != null) {
            onMesaIdentificada()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Black
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. Camera Feed / Permission Request
            if (hasCameraPermission) {
                CameraPreview(
                    onBarcodeScanned = { barcode ->
                        barcode.rawValue?.let { viewModel.onQRCodeScanned(it) }
                    },
                    isFlashlightOn = isFlashlightOn
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color(0xFF0C1609)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = NeonGreen)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Esperando permiso de cámara...",
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
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
                        onClick = { viewModel.toggleFlashlight() },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = if (isFlashlightOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = "Flash",
                            tint = if (isFlashlightOn) NeonGreen else Color.White
                        )
                    }
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
                    onClick = { viewModel.onHelpClick() },
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
                        imageVector = Icons.Default.HelpOutline,
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
fun CameraPreview(
    onBarcodeScanned: (Barcode) -> Unit,
    isFlashlightOn: Boolean
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var camera: Camera? by remember { mutableStateOf(null) }

    LaunchedEffect(isFlashlightOn) {
        camera?.cameraControl?.enableTorch(isFlashlightOn)
    }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val executor = ContextCompat.getMainExecutor(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                val scanner = BarcodeScanning.getClient(
                    BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                        .build()
                )

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                
                imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                        scanner.process(image)
                            .addOnSuccessListener { barcodes ->
                                barcodes.firstOrNull()?.let { onBarcodeScanned(it) }
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                    } else {
                        imageProxy.close()
                    }
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                } catch (ex: Exception) {
                    Log.e("CameraPreview", "Use case binding failed", ex)
                }
            }, executor)
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
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
