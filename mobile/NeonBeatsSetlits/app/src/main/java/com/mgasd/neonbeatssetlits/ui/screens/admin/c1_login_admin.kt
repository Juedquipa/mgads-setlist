package com.mgasd.neonbeatssetlits.ui.screens.admin

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mgasd.neonbeatssetlits.ui.theme.NeonBeatsTheme
import com.mgasd.neonbeatssetlits.viewmodel.AdminViewModel

@Composable
fun AdminLoginScreen(
    viewModel: AdminViewModel,
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            onLoginSuccess()
        }
    }

    AdminLoginContent(
        username = uiState.username,
        password = uiState.password,
        isLoading = uiState.isLoading,
        error = uiState.error,
        onUsernameChange = viewModel::onUsernameChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLoginClick = viewModel::onLoginClick,
        onRecoverAccess = viewModel::onRecoverAccessClick
    )
}

@Composable
fun AdminLoginContent(
    username: String,
    password: String,
    isLoading: Boolean,
    error: String?,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRecoverAccess: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        center = Offset.Zero // Approximation for top-right accent
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Main Container
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    // Header / Logo Area
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.background)
                                .border(1.dp, MaterialTheme.colorScheme.secondary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "NEON BEATS",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = (-2).sp
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Surface(
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                                shape = CircleShape,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
                            ) {
                                Text(
                                    text = "ADMIN",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp),
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }

                    // Login Form
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // User Input
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = "USUARIO",
                                    style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            TextField(
                                value = username,
                                onValueChange = onUsernameChange,
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("ADMIN_ID", modifier = Modifier.alpha(0.3f)) },
                                textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.background,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                                    focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant
                                ),
                                singleLine = true
                            )
                        }

                        // Password Input
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = "PASSWORD",
                                    style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            TextField(
                                value = password,
                                onValueChange = onPasswordChange,
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("••••••••", modifier = Modifier.alpha(0.3f)) },
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.background,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                                    focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant
                                ),
                                singleLine = true
                            )
                        }

                        if (error != null) {
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Action Button
                        Button(
                            onClick = onLoginClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            ),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.onSecondary, modifier = Modifier.size(24.dp))
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = "ACCEDER AL PANEL",
                                        style = MaterialTheme.typography.headlineSmall.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    )
                                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                                }
                            }
                        }

                        // Helper Link
                        Text(
                            text = "Recuperar Acceso",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onRecoverAccess),
                            style = MaterialTheme.typography.labelSmall.copy(
                                textDecoration = TextDecoration.Underline,
                                textAlign = TextAlign.Center
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminLoginPreview() {
    NeonBeatsTheme {
        AdminLoginContent(
            username = "",
            password = "",
            isLoading = false,
            error = null,
            onUsernameChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onRecoverAccess = {}
        )
    }
}
