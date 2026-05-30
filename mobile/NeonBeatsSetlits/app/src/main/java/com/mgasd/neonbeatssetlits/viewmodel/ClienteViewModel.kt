package com.mgasd.neonbeatssetlits.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mgasd.neonbeatssetlits.data.model.ClientSessionRequest
import com.mgasd.neonbeatssetlits.data.model.Session
import com.mgasd.neonbeatssetlits.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ClienteViewModel : ViewModel() {
    private val _isFlashlightOn = MutableStateFlow(false)
    val isFlashlightOn = _isFlashlightOn.asStateFlow()

    private val _mesaNumero = MutableStateFlow("7")
    val mesaNumero = _mesaNumero.asStateFlow()

    private val _session = MutableStateFlow<Session?>(null)
    val session = _session.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

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
        if (_isLoading.value) return
        
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.createClientSession(
                    ClientSessionRequest(content)
                )
                if (response.isSuccessful) {
                    val newSession = response.body()
                    _session.value = newSession
                    _mesaNumero.value = newSession?.table?.toString() ?: "Unknown"
                } else {
                    _error.value = "QR inválido o mesa no disponible"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onEnterOrderCodeClick() {
        // Lógica para ingresar código de pedido
    }

    fun onViewQueueClick() {
        // Lógica para ver la cola
    }
}
