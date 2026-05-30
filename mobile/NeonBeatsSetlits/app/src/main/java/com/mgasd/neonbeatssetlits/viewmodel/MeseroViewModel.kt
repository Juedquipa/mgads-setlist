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

data class CodeHistoryItem(
    val tableId: String,
    val code: String,
    val time: String,
    val status: CodeStatus
)

enum class CodeStatus {
    ACTIVE, USED, EXPIRED
}

data class MeseroHomeState(
    val waiterName: String = "Roberto",
    val shiftInfo: String = "Turno Noche • Sector B",
    val tablesServed: Int = 12,
    val averageRating: Float = 4.8f,
    val ratingTrend: String = "+0.2",
    val codeHistory: List<CodeHistoryItem> = listOf(
        CodeHistoryItem("T12", "NB-8X2F-9A", "20:45", CodeStatus.ACTIVE),
        CodeHistoryItem("T08", "NB-4M9K-2B", "19:30", CodeStatus.USED),
        CodeHistoryItem("T05", "NB-1L5P-7C", "18:00", CodeStatus.EXPIRED)
    )
)

class MeseroViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MeseroLoginState())
    val uiState: StateFlow<MeseroLoginState> = _uiState.asStateFlow()

    private val _homeState = MutableStateFlow(MeseroHomeState())
    val homeState: StateFlow<MeseroHomeState> = _homeState.asStateFlow()

    fun onUsernameChange(newUsername: String) {
        _uiState.update { it.copy(username = newUsername) }
    }

    fun onPinChange(newPin: String) {
        if (newPin.length <= 4 && newPin.all { it.isDigit() }) {
            _uiState.update { it.copy(pin = newPin) }
        }
    }

    fun onLoginClick() {
        if (_uiState.value.isLoading) return
        
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
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
    
    fun onGenerateCodeClick() {
        // Lógica para generar un nuevo código
    }

    fun resetLoginState() {
        _uiState.update { MeseroLoginState() }
    }
}
