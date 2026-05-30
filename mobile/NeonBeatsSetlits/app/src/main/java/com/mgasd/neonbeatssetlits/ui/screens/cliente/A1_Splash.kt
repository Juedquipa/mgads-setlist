package com.mgasd.neonbeatssetlits.ui.screens.cliente

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.mgasd.neonbeatssetlits.ui.theme.NeonGreen
import com.mgasd.neonbeatssetlits.ui.theme.NeonBeatsTheme


@Composable
fun A1_SplashScreen(
    onNavigateToScanner: () -> Unit
) {
    // Animación de pulso para el texto Neón
    val infiniteTransition = rememberInfiniteTransition(label = "NeonPulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AlphaPulse"
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            NeonGreen.copy(alpha = 0.05f)
                        ),
                        startY = 500f
                    )
                )
        ) {
            // Contenido Principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Título con Efecto Neón
                Text(
                    text = "NEON BEATS\nSETLIST",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 54.sp,
                        lineHeight = 54.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-2).sp
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .graphicsLayer(alpha = alpha)
                        .drawBehind {
                            // Simulación de resplandor neón
                        }
                        .shadow(
                            elevation = 20.dp,
                            shape = RoundedCornerShape(0.dp),
                            spotColor = NeonGreen,
                            ambientColor = NeonGreen
                        )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "LA MÚSICA LA PONES TÚ",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(64.dp))

                // Decoración: Ecualizador Simple
                Row(
                    modifier = Modifier.height(48.dp).alpha(0.5f),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(5) { index ->
                        val heightFactor by infiniteTransition.animateFloat(
                            initialValue = 0.3f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(500 + (index * 200), easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "BarHeight"
                        )
                        Box(
                            modifier = Modifier
                                .width(8.dp)
                                .fillMaxHeight(heightFactor)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Botón de Acción
                Button(
                    onClick = onNavigateToScanner,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .shadow(
                            elevation = 24.dp,
                            spotColor = NeonGreen.copy(alpha = 0.6f),
                            ambientColor = NeonGreen.copy(alpha = 0.6f)
                        ),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "ESCANEAR MESA PARA COMENZAR",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }

                Text(
                    text = "REQUIERE ACCESO A LA CÁMARA",
                    style = MaterialTheme.typography.bodySmall.copy(
                        letterSpacing = 2.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.padding(vertical = 24.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun A1_SplashPreview() {
    NeonBeatsTheme {
        A1_SplashScreen(onNavigateToScanner = {})
    }
}
