package com.mgasd.neonbeatssetlits.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AdminLoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val error: String? = null
)

data class DashboardDjUiState(
    val djName: String = "DJ MARCUS",
    val stage: String = "Main Stage",
    val djId: String = "#4492",
    val currentTrackTitle: String = "Midnight City (Remix)",
    val currentTrackArtist: String = "M83 ft. DJ Marcus",
    val isPlaying: Boolean = true,
    val activeTables: Int = 12,
    val totalTables: Int = 15,
    val queueSize: Int = 24,
    val pendingRequests: Int = 8
)

data class QueueAdminItem(
    val id: String,
    val position: String,
    val title: String,
    val artist: String,
    val duration: String
)

data class PendingApprovalItem(
    val id: String,
    val title: String,
    val artist: String,
    val duration: String,
    val requestedBy: String,
    val thumbnailUrl: String? = null
)

class AdminViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AdminLoginUiState())
    val uiState: StateFlow<AdminLoginUiState> = _uiState.asStateFlow()

    private val _dashboardState = MutableStateFlow(DashboardDjUiState())
    val dashboardState: StateFlow<DashboardDjUiState> = _dashboardState.asStateFlow()

    private val _queueList = MutableStateFlow(listOf(
        QueueAdminItem("1", "01", "SYNTHETIC RAIN", "Digital Mirage", "4:12"),
        QueueAdminItem("2", "02", "NEON SHADOWS", "Midnight Vibe", "3:45"),
        QueueAdminItem("3", "03", "INDUSTRIAL BEAT", "Steel Foundry", "5:01"),
        QueueAdminItem("4", "04", "DARK MATTER", "Void Walker", "4:30")
    ))
    val queueList: StateFlow<List<QueueAdminItem>> = _queueList.asStateFlow()

    private val _pendingApprovals = MutableStateFlow(listOf(
        PendingApprovalItem(
            id = "1",
            title = "CYBERNETIC OVERDRIVE V2",
            artist = "DJ Neurotoxic",
            duration = "04:12",
            requestedBy = "Table 12"
        ),
        PendingApprovalItem(
            id = "2",
            title = "ACID BASELINE RITUAL",
            artist = "Underground Sect",
            duration = "03:50",
            requestedBy = "Bar VIP"
        )
    ))
    val pendingApprovals: StateFlow<List<PendingApprovalItem>> = _pendingApprovals.asStateFlow()

    fun onUsernameChange(username: String) {
        _uiState.value = _uiState.value.copy(username = username)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun onLoginClick() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        // Lógica de autenticación simulada
        if (_uiState.value.username == "ADMIN" && _uiState.value.password == "1234") {
            _uiState.value = _uiState.value.copy(isLoading = false, loginSuccess = true)
        } else {
            _uiState.value = _uiState.value.copy(isLoading = false, error = "Credenciales Inválidas")
        }
    }

    fun onRecoverAccessClick() {
        // Lógica de recuperación
    }

    fun onTogglePlayback() {
        _dashboardState.value = _dashboardState.value.copy(isPlaying = !_dashboardState.value.isPlaying)
    }

    fun onSkipPrevious() {
        // Skip previous logic
    }

    fun onSkipNext() {
        // Skip next logic
    }

    fun onRemoveFromQueue(itemId: String) {
        _queueList.value = _queueList.value.filter { it.id != itemId }
            .mapIndexed { index, item -> item.copy(position = (index + 1).toString().padStart(2, '0')) }
    }

    fun onClearQueue() {
        _queueList.value = emptyList()
    }
    
    fun onMoveItem(fromIndex: Int, toIndex: Int) {
        // Drag and drop logic placeholder
    }

    fun onApproveRequest(id: String) {
        _pendingApprovals.value = _pendingApprovals.value.filter { it.id != id }
    }

    fun onRejectRequest(id: String) {
        _pendingApprovals.value = _pendingApprovals.value.filter { it.id != id }
    }
}
