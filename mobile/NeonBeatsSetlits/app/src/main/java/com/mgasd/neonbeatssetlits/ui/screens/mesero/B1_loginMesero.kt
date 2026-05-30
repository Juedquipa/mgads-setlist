package com.mgasd.neonbeatssetlits.ui.screens.mesero

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mgasd.neonbeatssetlits.ui.theme.NeonBeatsTheme
import com.mgasd.neonbeatssetlits.viewmodel.MeseroViewModel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester

/**
 * Pantalla B1 - Login Mesero
 * Estética: Industrial Neon Underground
 */
@Composable
fun B1_LoginMeseroScreen(
    viewModel: MeseroViewModel,
    onNavigateToDashboard: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            onNavigateToDashboard()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Fondo atmosférico
            AtmosphericBackground()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Header: Branding
                Text(
                    text = "NEON BEATS",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 54.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-2).sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Badge MESERO
                Surface(
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(2.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "MESERO",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 6.sp
                        ),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                Spacer(modifier = Modifier.height(64.dp))

                // Formulario de Login
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                        .padding(32.dp)
                ) {
                    CornerAccents()

                    Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {
                        
                        // Campo Usuario
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            LabelWithIcon(icon = Icons.Default.Person, label = "USUARIO")
                            TextField(
                                value = uiState.username,
                                onValueChange = viewModel::onUsernameChange,
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Ej. marcus_88", modifier = Modifier.alpha(0.3f)) },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
                                    focusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
                                    unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                shape = RoundedCornerShape(0.dp),
                                singleLine = true
                            )
                        }

                        // Campo PIN
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            LabelWithIcon(icon = Icons.Default.Dialpad, label = "PIN DE ACCESO")
                            
                            PinInputGrid(
                                pin = uiState.pin,
                                onPinChange = viewModel::onPinChange
                            )
                        }

                        // Botón de Acción
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            uiState.errorMessage?.let {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                            
                            Button(
                                onClick = { viewModel.onLoginClick() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp)
                                    .shadow(
                                        elevation = 20.dp,
                                        spotColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
                                        ambientColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)
                                    ),
                                shape = RoundedCornerShape(2.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary,
                                    contentColor = Color.Black
                                ),
                                enabled = !uiState.isLoading
                            ) {
                                if (uiState.isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = Color.Black,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Text(
                                            text = "ENTRAR",
                                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Icon(Icons.Default.ArrowForward, contentDescription = null)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Footer
                Text(
                    text = "¿PROBLEMAS DE ACCESO?",
                    style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "SYS_VER 4.2.1 // AUTH_REQ",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Composable
fun AtmosphericBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-100).dp, y = (-50).dp)
                .blur(100.dp)
                .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.08f), shape = RoundedCornerShape(150.dp))
        )
        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 100.dp, y = 100.dp)
                .blur(120.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f), shape = RoundedCornerShape(200.dp))
        )
    }
}

@Composable
fun LabelWithIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.alpha(0.7f)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun PinInputGrid(pin: String, onPinChange: (String) -> Unit) {
    val focusRequester = remember { FocusRequester() }
    
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) { focusRequester.requestFocus() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(4) { index ->
                val digit = pin.getOrNull(index)?.toString() ?: ""
                val isFocused = pin.length == index
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f))
                        .border(
                            width = 2.dp,
                            color = if (isFocused) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = digit,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Black,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    )
                }
            }
        }
        
        TextField(
            value = pin,
            onValueChange = { if (it.length <= 4) onPinChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .alpha(0f)
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            maxLines = 1
        )
    }
}

@Composable
fun CornerAccents() {
    Box(modifier = Modifier.fillMaxSize()) {
        val stroke = 1.dp
        val size = 10.dp
        val color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        
        Canvas(modifier = Modifier.align(Alignment.TopStart).size(size)) {
            drawLine(color, androidx.compose.ui.geometry.Offset.Zero, androidx.compose.ui.geometry.Offset(size.toPx(), 0f), stroke.toPx())
            drawLine(color, androidx.compose.ui.geometry.Offset.Zero, androidx.compose.ui.geometry.Offset(0f, size.toPx()), stroke.toPx())
        }
        Canvas(modifier = Modifier.align(Alignment.TopEnd).size(size)) {
            drawLine(color, androidx.compose.ui.geometry.Offset(size.toPx(), 0f), androidx.compose.ui.geometry.Offset.Zero, stroke.toPx())
            drawLine(color, androidx.compose.ui.geometry.Offset(size.toPx(), 0f), androidx.compose.ui.geometry.Offset(size.toPx(), size.toPx()), stroke.toPx())
        }
        Canvas(modifier = Modifier.align(Alignment.BottomStart).size(size)) {
            drawLine(color, androidx.compose.ui.geometry.Offset(0f, size.toPx()), androidx.compose.ui.geometry.Offset.Zero, stroke.toPx())
            drawLine(color, androidx.compose.ui.geometry.Offset(0f, size.toPx()), androidx.compose.ui.geometry.Offset(size.toPx(), size.toPx()), stroke.toPx())
        }
        Canvas(modifier = Modifier.align(Alignment.BottomEnd).size(size)) {
            drawLine(color, androidx.compose.ui.geometry.Offset(size.toPx(), size.toPx()), androidx.compose.ui.geometry.Offset(0f, size.toPx()), stroke.toPx())
            drawLine(color, androidx.compose.ui.geometry.Offset(size.toPx(), size.toPx()), androidx.compose.ui.geometry.Offset(size.toPx(), 0f), stroke.toPx())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun B1_loginMeseroPreview() {

    val meseroViewModel: MeseroViewModel = viewModel();
    NeonBeatsTheme {
        B1_LoginMeseroScreen(meseroViewModel,
            onNavigateToDashboard = {})
    }
}
