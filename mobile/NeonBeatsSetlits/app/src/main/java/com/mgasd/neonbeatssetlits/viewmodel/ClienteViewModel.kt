package com.mgasd.neonbeatssetlits.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ClienteViewModel : ViewModel() {
    private val _isFlashlightOn = MutableStateFlow(false)
    val isFlashlightOn = _isFlashlightOn.asStateFlow()

    fun toggleFlashlight() {
        _isFlashlightOn.value = !_isFlashlightOn.value
    }

    fun onBackClick() {
        // Manejado por navegación
    }

    fun onHelpClick() {
        // Lógica de ayuda
    }

    fun onQRCodeScanned(content: String) {
        // Procesar QR
    }
}
