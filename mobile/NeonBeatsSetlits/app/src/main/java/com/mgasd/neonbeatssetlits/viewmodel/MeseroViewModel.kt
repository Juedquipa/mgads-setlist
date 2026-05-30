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

data class MeseroCodeGenerationState(
    val selectedTable: String? = "T7",
    val generatedCode: String = "492-817",
    val secondsRemaining: Int = 299,
    val totalSeconds: Int = 300,
    val tables: List<String> = listOf("T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11"),
    val isGenerating: Boolean = false
)

enum class TableStatus {
    ACTIVE, SESSION, EMPTY, CALLING
}

data class ActiveTable(
    val id: String,
    val status: TableStatus,
    val statusLabel: String,
    val pendingOrders: Int = 0,
    val queuedRequests: Int = 0,
    val isPlaying: Boolean = false
)

data class MeseroTablesState(
    val activeTablesCount: Int = 0,
    val sessionTablesCount: Int = 0,
    val tables: List<ActiveTable> = emptyList(),
    val isLoading: Boolean = false
)

class MeseroViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MeseroLoginState())
    val uiState: StateFlow<MeseroLoginState> = _uiState.asStateFlow()

    private val _homeState = MutableStateFlow(MeseroHomeState())
    val homeState: StateFlow<MeseroHomeState> = _homeState.asStateFlow()

    private val _codeGenState = MutableStateFlow(MeseroCodeGenerationState())
    val codeGenState: StateFlow<MeseroCodeGenerationState> = _codeGenState.asStateFlow()

    private val _tablesState = MutableStateFlow(MeseroTablesState())
    val tablesState: StateFlow<MeseroTablesState> = _tablesState.asStateFlow()

    init {
        loadHomeData()
        loadTablesData()
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

    fun approveRequest(requestId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.approveRequest(requestId)
                if (response.isSuccessful) {
                    // Update state or reload
                }
            } catch (e: Exception) {}
        }
    }

    fun rejectRequest(requestId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.rejectRequest(requestId)
                if (response.isSuccessful) {
                    // Update state or reload
                }
            } catch (e: Exception) {}
        }
    }
    
    private fun loadTablesData() {
        _tablesState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.listTables()
                if (response.isSuccessful) {
                    val tables = response.body() ?: emptyList()
                    
                    // Actualizamos la lista de mesas en el estado de generación de código también
                    _codeGenState.update { it.copy(tables = tables.map { t -> "T${t.id}" }) }

                    val activeTables = mutableListOf<ActiveTable>()
                    var activeCount = 0
                    var sessionCount = 0

                    tables.forEach { table ->
                        // Para cada mesa, intentamos obtener su sesión activa para ver pedidos/solicitudes
                        val sessionResponse = RetrofitClient.instance.getTableActiveSession(table.id.toString())
                        val session = if (sessionResponse.isSuccessful) sessionResponse.body() else null
                        
                        val status = when {
                            session != null -> {
                                sessionCount++
                                TableStatus.SESSION
                            }
                            table.is_active -> {
                                activeCount++
                                TableStatus.ACTIVE
                            }
                            else -> TableStatus.EMPTY
                        }

                        activeTables.add(
                            ActiveTable(
                                id = "T${table.id}",
                                status = status,
                                statusLabel = when(status) {
                                    TableStatus.SESSION -> "In Session"
                                    TableStatus.ACTIVE -> "Needs Attention"
                                    TableStatus.EMPTY -> "Empty"
                                    TableStatus.CALLING -> "Calling Staff"
                                },
                                pendingOrders = 0, // El API actual no parece retornar órdenes
                                queuedRequests = if (session != null) 0 else 0, // Podríamos llamar a /api/client/requests/ si tuviéramos el token
                                isPlaying = false // Simulado por ahora
                            )
                        )
                    }

                    _tablesState.update {
                        it.copy(
                            activeTablesCount = activeCount,
                            sessionTablesCount = sessionCount,
                            tables = activeTables,
                            isLoading = false
                        )
                    }
                } else {
                    _tablesState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _tablesState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun createTable(name: String) {
        viewModelScope.launch {
            try {
                val newTable = com.mgasd.neonbeatssetlits.data.model.Table(
                    id = 0,
                    name = name,
                    qr_code_token = null,
                    is_active = false,
                    created_at = ""
                )
                val response = RetrofitClient.instance.createTable(newTable)
                if (response.isSuccessful) {
                    loadTablesData()
                }
            } catch (e: Exception) {}
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
                    table = _codeGenState.value.selectedTable?.replace("T", "")?.toIntOrNull(),
                    created_at = "",
                    used_at = null
                )
                
                val response = RetrofitClient.instance.createPinCode(dummyPin)
                if (response.isSuccessful) {
                    val newPin = response.body()
                    if (newPin != null) {
                        _codeGenState.update { 
                            it.copy(
                                generatedCode = newPin.code,
                                selectedTable = "T${newPin.table ?: ""}",
                                secondsRemaining = 300
                            )
                        }
                    }
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

    fun onTableSelect(tableId: String) {
        _codeGenState.update { it.copy(selectedTable = tableId) }
    }

    fun onGenerateNewCode() {
        onGenerateCodeClick()
    }

    fun onAcknowledgeTable(tableId: String) {
        val numericId = tableId.replace("T", "").toIntOrNull() ?: return
        viewModelScope.launch {
            try {
                // Asumimos que acknowledge es marcar is_active como false o similar
                // O tal vez hay un endpoint dedicado si el YAML fue actualizado
                // Por ahora usamos un PATCH genérico si existiera el modelo PatchedTable
                // RetrofitClient.instance.partialUpdateTable(numericId, PatchedTable(is_active = false))
                
                // Si no, simplemente recargamos para simular que ya no está en estado "Calling"
                loadTablesData()
            } catch (e: Exception) {}
        }
    }

    fun resetLoginState() {
        _uiState.update { MeseroLoginState() }
    }
}
