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

enum class CodeStatus {
    ACTIVE, USED, EXPIRED
}

data class CodeHistoryItem(
    val id: Int,
    val tableId: String,
    val code: String,
    val time: String,
    val status: CodeStatus
)

data class MeseroHomeState(
    val waiterName: String = "Marcus",
    val shiftInfo: String = "TURNO NOCHE // 18:00 - 02:00",
    val tablesServed: Int = 12,
    val averageRating: Double = 4.8,
    val ratingTrend: String = "+0.2",
    val codeHistory: List<CodeHistoryItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

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

    private val _homeState = MutableStateFlow(MeseroHomeState())
    val homeState: StateFlow<MeseroHomeState> = _homeState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        _homeState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                // Listamos los PIN codes del mesero para el historial
                val response = RetrofitClient.instance.listPinCodes()
                if (response.isSuccessful) {
                    val pinCodes = response.body() ?: emptyList()
                    val history = pinCodes.take(5).map { pin ->
                        CodeHistoryItem(
                            id = pin.id,
                            tableId = pin.table?.toString() ?: "N/A",
                            code = pin.code,
                            time = pin.created_at.split("T").getOrElse(1) { "" }.take(5),
                            status = if (pin.is_used) CodeStatus.USED else CodeStatus.ACTIVE
                        )
                    }
                    _homeState.update { 
                        it.copy(
                            codeHistory = history,
                            isLoading = false
                        )
                    }
                } else {
                    _homeState.update { it.copy(isLoading = false, errorMessage = "Error cargando historial") }
                }
            } catch (e: Exception) {
                _homeState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

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
                // El pin se envía como password según el spec de LoginRequest
                val response = RetrofitClient.instance.login(
                    LoginRequest(
                        username = _uiState.value.username,
                        password = _uiState.value.pin
                    )
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
                        errorMessage = "Error de conexión: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun onGenerateCodeClick() {
        if (_homeState.value.isLoading) return

        _homeState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                // Según el spec, POST /api/pin-codes/ crea un código automático
                // El modelo PinCode requiere ciertos campos, pero el server genera el código
                // Usamos un ID dummy que el backend ignorará al crear
                val dummyPin = com.mgasd.neonbeatssetlits.data.model.PinCode(
                    id = 0,
                    code = "",
                    credits = 1, // Default credits
                    is_used = false,
                    created_by = 0,
                    table = null,
                    created_at = "",
                    used_at = null
                )
                
                val response = RetrofitClient.instance.createPinCode(dummyPin)
                if (response.isSuccessful) {
                    // Recargamos el historial para mostrar el nuevo código
                    loadHomeData()
                } else {
                    _homeState.update { it.copy(isLoading = false, errorMessage = "Error generando código") }
                }
            } catch (e: Exception) {
                _homeState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun resetLoginState() {
        _uiState.update { MeseroLoginState() }
    }
}
