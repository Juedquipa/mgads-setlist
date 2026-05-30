package com.mgasd.neonbeatssetlits.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MeseroLoginState(
    val username: String = "",
    val pin: String = "",
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val errorMessage: String? = null
)

class MeseroViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MeseroLoginState())
    val uiState: StateFlow<MeseroLoginState> = _uiState.asStateFlow()

    fun onUsernameChange(newUsername: String) {
        _uiState.update { it.copy(username = newUsername) }
    }

    fun onPinChange(newPin: String) {
        // Acepta solo hasta 4 dígitos
        if (newPin.length <= 4 && newPin.all { it.isDigit() }) {
            _uiState.update { it.copy(pin = newPin) }
        }
    }

    fun onLoginClick() {
        if (_uiState.value.isLoading) return
        
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        
        // Simulación de login (en producción esto llamaría a un repository)
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            // Simulación simple: si el PIN tiene 4 dígitos, autorizamos
            if (_uiState.value.pin.length == 4 && _uiState.value.username.isNotEmpty()) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        loginSuccess = true 
                    ) 
                }
            } else {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = "Credenciales incorrectas" 
                    ) 
                }
            }
        }, 1500)
    }
    
    fun resetLoginState() {
        _uiState.update { MeseroLoginState() }
    }
}
