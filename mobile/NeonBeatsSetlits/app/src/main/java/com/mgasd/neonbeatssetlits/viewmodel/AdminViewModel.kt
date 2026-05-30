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

data class TableActivity(
    val id: String,
    val status: AdminTableStatus
)

enum class AdminTableStatus {
    ACTIVE, WARNING, AVAILABLE, ERROR
}

data class WaiterProductivity(
    val id: String,
    val rank: String,
    val name: String,
    val efficiency: Float,
    val sales: String,
    val thumbnailUrl: String? = null
)

data class StatisticsUiState(
    val occupancy: Int = 85,
    val occupancyTrend: Int = 12,
    val avgTime: Int = 42,
    val avgTimeTrend: Int = 5,
    val openTickets: Int = 24,
    val totalTickets: Int = 28,
    val orderFlow: List<Float> = listOf(0.2f, 0.4f, 0.3f, 0.6f, 0.85f, 0.5f, 0.7f, 0.3f),
    val tableMap: List<TableActivity> = listOf(
        TableActivity("T1", AdminTableStatus.ACTIVE),
        TableActivity("T2", AdminTableStatus.ACTIVE),
        TableActivity("T3", AdminTableStatus.WARNING),
        TableActivity("T4", AdminTableStatus.AVAILABLE),
        TableActivity("T5", AdminTableStatus.AVAILABLE),
        TableActivity("T6", AdminTableStatus.ACTIVE),
        TableActivity("T7", AdminTableStatus.AVAILABLE),
        TableActivity("T8", AdminTableStatus.ERROR)
    ),
    val topWaiters: List<WaiterProductivity> = listOf(
        WaiterProductivity("1", "01", "Marcus T.", 0.92f, "$1,240"),
        WaiterProductivity("2", "02", "Elena V.", 0.78f, "$980")
    ),
    val selectedTab: Int = 0 // 0 for Tables, 1 for Waiters
)

enum class StaffStatus {
    ACTIVE, INACTIVE
}

data class StaffMember(
    val id: String,
    val name: String,
    val employeeId: String,
    val status: StaffStatus,
    val currentTables: String? = null,
    val shiftTime: String? = null,
    val lastShift: String? = null,
    val thumbnailUrl: String? = null
)

enum class LogStatus {
    SUCCESS, WARNING, SYSTEM
}

data class ActivityLog(
    val id: String,
    val timestamp: String,
    val staffMember: String,
    val action: String,
    val table: String,
    val status: LogStatus
)

data class StaffManagementUiState(
    val staffMembers: List<StaffMember> = emptyList(),
    val activityLogs: List<ActivityLog> = emptyList()
)

data class CatalogItem(
    val id: String,
    val title: String,
    val artist: String,
    val genre: String,
    val isActive: Boolean
)

data class ConfigUiState(
    val barName: String = "Neon Beats Underground",
    val allowYouTube: Boolean = true,
    val explicitFilter: Boolean = false,
    val initialCredits: Int = 5,
    val requestExpiryMinutes: Int = 30,
    val localCatalog: List<CatalogItem> = emptyList()
)

class AdminViewModel : ViewModel() {
    private val _mesaNumero = MutableStateFlow("04")
    val mesaNumero: StateFlow<String> = _mesaNumero.asStateFlow()

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

    private val _statisticsState = MutableStateFlow(StatisticsUiState())
    val statisticsState: StateFlow<StatisticsUiState> = _statisticsState.asStateFlow()

    private val _staffState = MutableStateFlow(StaffManagementUiState(
        staffMembers = listOf(
            StaffMember("1", "ELIAS V.", "#8821", StaffStatus.ACTIVE, currentTables = "04", shiftTime = "04:22:10"),
            StaffMember("2", "SARAH K.", "#7743", StaffStatus.INACTIVE, lastShift = "YESTERDAY")
        ),
        activityLogs = listOf(
            ActivityLog("1", "23:45:12", "ELIAS V.", "Closed Order #9928 - Payment Processed", "T-04", LogStatus.SUCCESS),
            ActivityLog("2", "23:30:05", "MARCUS", "Voided Item: Neon Margarita", "T-12", LogStatus.WARNING),
            ActivityLog("3", "22:15:00", "SARAH K.", "Shift Ended manually by Admin", "--", LogStatus.SYSTEM)
        )
    ))
    val staffState: StateFlow<StaffManagementUiState> = _staffState.asStateFlow()

    private val _configState = MutableStateFlow(ConfigUiState(
        localCatalog = listOf(
            CatalogItem("1", "Cyberpunk Overture", "Neon Syndicate", "Synthwave", true),
            CatalogItem("2", "Midnight Rider", "Dark Alleys", "Dark Rock", false)
        )
    ))
    val configState: StateFlow<ConfigUiState> = _configState.asStateFlow()

    fun onUsernameChange(username: String) {
        _uiState.value = _uiState.value.copy(username = username)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun onLoginClick() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        
        val inputUser = _uiState.value.username.trim()
        val inputPass = _uiState.value.password.trim()

        // Lógica de autenticación simulada (Case insensitive para usuario)
        if (inputUser.equals("ADMIN", ignoreCase = true) && inputPass == "1234") {
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

    fun onSeeAllWaitersClick() {
        // Navigate or show more
    }

    fun onTabChange(index: Int) {
        _statisticsState.value = _statisticsState.value.copy(selectedTab = index)
    }

    fun onAddStaffClick() {
        // Logic to add staff
    }

    fun onFilterStaffClick() {
        // Logic to filter staff
    }

    fun onDisableStaff(staffId: String) {
        _staffState.value = _staffState.value.copy(
            staffMembers = _staffState.value.staffMembers.map {
                if (it.id == staffId) it.copy(status = StaffStatus.INACTIVE) else it
            }
        )
    }

    fun onBarNameChange(name: String) {
        _configState.value = _configState.value.copy(barName = name)
    }

    fun onToggleYouTube(enabled: Boolean) {
        _configState.value = _configState.value.copy(allowYouTube = enabled)
    }

    fun onToggleExplicitFilter(enabled: Boolean) {
        _configState.value = _configState.value.copy(explicitFilter = enabled)
    }

    fun updateInitialCredits(delta: Int) {
        val newVal = (_configState.value.initialCredits + delta).coerceIn(1, 99)
        _configState.value = _configState.value.copy(initialCredits = newVal)
    }

    fun updateExpiryMinutes(delta: Int) {
        val newVal = (_configState.value.requestExpiryMinutes + delta).coerceIn(5, 120)
        _configState.value = _configState.value.copy(requestExpiryMinutes = newVal)
    }

    fun onImportCsv() {
        // Logic to import CSV
    }

    fun onSaveConfig() {
        // Logic to save config
    }
}
