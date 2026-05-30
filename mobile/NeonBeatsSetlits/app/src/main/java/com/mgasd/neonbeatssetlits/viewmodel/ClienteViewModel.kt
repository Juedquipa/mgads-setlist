package com.mgasd.neonbeatssetlits.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class TrackInfo(
    val title: String,
    val artist: String,
    val bpm: Int
)

data class SongItem(
    val id: String,
    val title: String,
    val artist: String,
    val duration: String,
    val isFromBarCatalog: Boolean,
    val isPending: Boolean = false,
    val isRequested: Boolean = false,
    val youtubeId: String? = null,
    val thumbnailUrl: String? = null
)

data class QueueItem(
    val id: String,
    val position: String,
    val title: String,
    val artist: String,
    val time: String,
    val requestedBy: String,
    val isUserRequest: Boolean = false
)

class ClienteViewModel : ViewModel() {
    private val _isFlashlightOn = MutableStateFlow(false)
    val isFlashlightOn = _isFlashlightOn.asStateFlow()

    private val _mesaNumero = MutableStateFlow("04")
    val mesaNumero = _mesaNumero.asStateFlow()

    private val _pinCode = MutableStateFlow("")
    val pinCode = _pinCode.asStateFlow()

    private val _currentTrack = MutableStateFlow(TrackInfo("Cybernetic Pulse", "DJ Synthwave", 138))
    val currentTrack = _currentTrack.asStateFlow()

    private val _availableCredits = MutableStateFlow(4)
    val availableCredits = _availableCredits.asStateFlow()

    private val _maxCredits = MutableStateFlow(4)
    val maxCredits = _maxCredits.asStateFlow()

    private val _nextTrack = MutableStateFlow(Pair("NEON NIGHTS (VIP MIX)", "Mesa 12"))
    val nextTrack = _nextTrack.asStateFlow()

    // Search related states
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedSearchTab = MutableStateFlow(0) // 0: Catalog, 1: YouTube
    val selectedSearchTab = _selectedSearchTab.asStateFlow()

    private val _searchResults = MutableStateFlow<List<SongItem>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _pendingQueueCount = MutableStateFlow(3)
    val pendingQueueCount = _pendingQueueCount.asStateFlow()

    private val _playQueue = MutableStateFlow<List<QueueItem>>(emptyList())
    val playQueue = _playQueue.asStateFlow()

    init {
        // Initial mock data for search
        _searchResults.value = listOf(
            SongItem("1", "Midnight City", "M83 • Hurry Up, We're Dreaming", "4:03", true, isPending = true, isRequested = true),
            SongItem("2", "Starboy", "The Weeknd ft. Daft Punk", "3:50", true),
            SongItem("3", "Daft Punk - Harder, Better, Faster, Stronger (Official Audio)", "DaftPunkVEVO", "5:12", false, youtubeId = "GDpmVUEjP1w")
        )

        // Initial mock data for queue
        _playQueue.value = listOf(
            QueueItem("1", "01", "Midnight Runner", "The Midnight", "2:15", "Table 12"),
            QueueItem("2", "02", "Neon Nights", "Kavinsky", "5:30", "Table 04", isUserRequest = true),
            QueueItem("3", "03", "Digital Love", "Daft Punk", "9:45", "Table 08"),
            QueueItem("4", "04", "Resonance", "HOME", "14:20", "Bar")
        )
    }

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

    fun onEnterOrderCodeClick() {
        // Lógica para ingresar código de pedido
    }

    fun onViewQueueClick() {
        // Lógica para ver la cola
    }

    fun onPinNumberClick(number: String) {
        if (_pinCode.value.length < 6) {
            _pinCode.value += number
        }
    }

    fun onPinDeleteClick() {
        if (_pinCode.value.isNotEmpty()) {
            _pinCode.value = _pinCode.value.dropLast(1)
        }
    }

    fun onPinSubmitClick() {
        if (_pinCode.value.length == 6) {
            // Lógica para validar PIN y activar solicitudes
        }
    }

    fun onSearchTrackClick() {
        // Lógica para buscar pista
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        // Lógica de filtrado o búsqueda API
    }

    fun onSearchTabChange(index: Int) {
        _selectedSearchTab.value = index
    }

    fun onRequestSong(songId: String) {
        // Lógica para solicitar canción
    }

    fun onHomeClick() {}
    fun onRequestsClick() {}
    fun onMenuClick() {}
    fun onBillsClick() {}
    fun onProfileClick() {}
}
