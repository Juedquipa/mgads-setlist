package com.mgasd.neonbeatssetlits.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mgasd.neonbeatssetlits.data.model.LoginRequest
import com.mgasd.neonbeatssetlits.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
        
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.login(
                    LoginRequest(_uiState.value.username, _uiState.value.pin)
                )
                if (response.isSuccessful) {
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
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = "Error de conexión" 
                    ) 
                }
            }
        }
    }
    
    fun resetLoginState() {
        _uiState.update { MeseroLoginState() }
    }
}
