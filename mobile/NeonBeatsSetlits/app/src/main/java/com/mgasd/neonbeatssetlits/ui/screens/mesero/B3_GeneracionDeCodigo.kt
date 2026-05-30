package com.mgasd.neonbeatssetlits.ui.screens.mesero

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.mgasd.neonbeatssetlits.ui.theme.NeonBeatsTheme
import com.mgasd.neonbeatssetlits.viewmodel.MeseroViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun B3_GeneracionDeCodigoScreen(
    viewModel: MeseroViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.codeGenState.collectAsStateWithLifecycle()
    var showCreateTableDialog by remember { mutableStateOf(false) }
    var newTableName by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "ASIGNAR CÓDIGO",
                        style = MaterialTheme.typography.labelLarge.copy(
                            letterSpacing = 4.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateTableDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = androidx.compose.foundation.shape.CircleShape,
                modifier = Modifier.shadow(12.dp, androidx.compose.foundation.shape.CircleShape, spotColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Table")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Selector de Mesa
            Text(
                "SELECCIONAR MESA",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.heightIn(max = 120.dp)
            ) {
                items(state.tables) { tableId ->
                    val isSelected = state.selectedTable == tableId
                    Surface(
                        onClick = { viewModel.onTableSelect(tableId) },
                        color = if (isSelected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surfaceVariant.copy(
                            alpha = 0.1f
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.aspectRatio(1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                tableId,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Área del Código Generado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CornerAccents()

                if (state.isGenerating) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.tertiary)
                } else if (state.generatedCode.isNotEmpty()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "CÓDIGO QR ACTIVO",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 2.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        QRCodeImage(
                            content = state.generatedCode,
                            modifier = Modifier.size(200.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = state.generatedCode,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp
                            ),
                            color = MaterialTheme.colorScheme.tertiary
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        val progress = if (state.totalSeconds > 0) state.secondsRemaining.toFloat() / state.totalSeconds.toFloat() else 0f
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.size(48.dp),
                                color = MaterialTheme.colorScheme.tertiary,
                                strokeWidth = 2.dp,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                            )
                            Text(
                                "${state.secondsRemaining}s",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Text(
                        "SELECCIONE UNA MESA Y\nGENERE UN CÓDIGO",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.alpha(0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Acciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { viewModel.onGenerateNewCode() },
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp),
                    enabled = state.selectedTable != null,
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.generatedCode.isEmpty()) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                        contentColor = if (state.generatedCode.isEmpty()) Color.Black else MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (state.generatedCode.isEmpty()) "GENERAR" else "REGENERAR")
                }

                Button(
                    onClick = { /* Lógica Compartir */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp),
                    enabled = state.generatedCode.isNotEmpty(),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("COMPARTIR", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Info de Seguridad
            Text(
                text = "ESTE CÓDIGO ES DE UN SOLO USO Y EXPIRA\nEN 5 MINUTOS TRAS SU GENERACIÓN.",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )
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
fun QRCodeImage(content: String, modifier: Modifier = Modifier) {
    val bitmap = remember(content) {
        if (content.isEmpty()) null
        else {
            try {
                val writer = QRCodeWriter()
                val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
                val width = bitMatrix.width
                val height = bitMatrix.height
                val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        bmp.setPixel(x, y, if (bitMatrix.get(x, y)) AndroidColor.BLACK else AndroidColor.WHITE)
                    }
                }
                bmp
            } catch (e: Exception) {
                null
            }
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "QR Code",
            modifier = modifier
                .background(Color.White)
                .padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun B3_GeneracionDeCodigoPreview() {
    val meseroViewModel: MeseroViewModel = viewModel();

    NeonBeatsTheme {
        B3_GeneracionDeCodigoScreen(
            meseroViewModel,
            onBack = {}
        )
    }
}
