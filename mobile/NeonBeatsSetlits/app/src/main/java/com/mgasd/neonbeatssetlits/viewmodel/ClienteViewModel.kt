package com.mgasd.neonbeatssetlits.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mgasd.neonbeatssetlits.data.model.*
import com.mgasd.neonbeatssetlits.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// UI-specific wrappers or re-definitions
data class TrackInfo(
    val title: String,
    val artist: String,
    val bpm: Int = 120
)

data class SongItem(
    val id: String,
    val title: String,
    val artist: String,
    val duration: String,
    val isFromBarCatalog: Boolean,
    val isPending: Boolean = false,
    val isRequested: Boolean = false,
    val spotifyId: String? = null,
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

enum class RequestStatus {
    IN_QUEUE, PENDING, REJECTED, PLAYED
}

data class UserRequest(
    val id: String,
    val title: String,
    val artist: String,
    val status: RequestStatus,
    val thumbnailUrl: String? = null
)

enum class OrderStatus {
    PREPARANDO, ENTREGADO, CANCELADO
}

data class OrderHistoryItem(
    val id: String,
    val orderNumber: String,
    val itemsSummary: String,
    val timeAgo: String,
    val amount: String,
    val status: OrderStatus
)

class ClienteViewModel : ViewModel() {
    private val _isFlashlightOn = MutableStateFlow(false)
    val isFlashlightOn = _isFlashlightOn.asStateFlow()

    private val _mesaNumero = MutableStateFlow("04")
    val mesaNumero = _mesaNumero.asStateFlow()

    private val _session = MutableStateFlow<com.mgasd.neonbeatssetlits.data.model.Session?>(null)
    val session = _session.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _pinCode = MutableStateFlow("")
    val pinCode = _pinCode.asStateFlow()

    private val _currentTrack = MutableStateFlow(TrackInfo("Cargando...", "...", 0))
    val currentTrack = _currentTrack.asStateFlow()

    private val _availableCredits = MutableStateFlow(0)
    val availableCredits = _availableCredits.asStateFlow()

    private val _usedCredits = MutableStateFlow(0)
    val usedCredits = _usedCredits.asStateFlow()

    private val _totalCredits = MutableStateFlow(0)
    val totalCredits = _totalCredits.asStateFlow()

    private val _ordersCount = MutableStateFlow(0)
    val ordersCount = _ordersCount.asStateFlow()

    private val _songsCount = MutableStateFlow(0)
    val songsCount = _songsCount.asStateFlow()

    private val _maxCredits = MutableStateFlow(0)
    val maxCredits = _maxCredits.asStateFlow()

    private val _nextTrack = MutableStateFlow(Pair("...", "..."))
    val nextTrack = _nextTrack.asStateFlow()

    // Search related states
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedSearchTab = MutableStateFlow(0) // 0: Catalog, 1: YouTube
    val selectedSearchTab = _selectedSearchTab.asStateFlow()

    private val _searchResults = MutableStateFlow<List<SongItem>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _pendingQueueCount = MutableStateFlow(0)
    val pendingQueueCount = _pendingQueueCount.asStateFlow()

    private val _playQueue = MutableStateFlow<List<QueueItem>>(emptyList())
    val playQueue = _playQueue.asStateFlow()

    private val _userRequests = MutableStateFlow<List<UserRequest>>(emptyList())
    val userRequests = _userRequests.asStateFlow()

    private val _orderHistory = MutableStateFlow<List<OrderHistoryItem>>(emptyList())
    val orderHistory = _orderHistory.asStateFlow()

    init {
        fetchQueue()
    }

    fun fetchQueue() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getQueue()
                if (response.isSuccessful) {
                    val queue = response.body() ?: emptyList()
                    _playQueue.value = queue.mapIndexed { index, request ->
                        QueueItem(
                            id = request.id.toString(),
                            position = (index + 1).toString().padStart(2, '0'),
                            title = request.track.title,
                            artist = request.track.artist,
                            time = request.requested_at.split("T").getOrNull(1)?.take(5) ?: "",
                            requestedBy = "Mesa ${request.id}",
                            isUserRequest = false 
                        )
                    }
                    _pendingQueueCount.value = queue.size
                    
                    queue.firstOrNull { it.status == StatusEnum.PLAYING }?.let { playing ->
                        _currentTrack.value = TrackInfo(playing.track.title, playing.track.artist, 120)
                    }
                }
            } catch (e: Exception) {
                // Error silent
            }
        }
    }

    fun toggleFlashlight() {
        _isFlashlightOn.value = !_isFlashlightOn.value
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
                    _availableCredits.value = newSession?.credits_balance ?: 0
                    _maxCredits.value = newSession?.credits_balance ?: 0
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
        val token = _session.value?.token ?: return
        if (_pinCode.value.length == 6) {
            _isLoading.value = true
            viewModelScope.launch {
                try {
                    val response = RetrofitClient.instance.validatePin(
                        token,
                        ClientPinCodeValidateRequest(_pinCode.value)
                    )
                    if (response.isSuccessful) {
                        val result = response.body()
                        _availableCredits.value = result?.new_balance ?: _availableCredits.value
                        _maxCredits.value = maxOf(_maxCredits.value, _availableCredits.value)
                        _pinCode.value = ""
                    } else {
                        _error.value = "PIN inválido o ya usado"
                    }
                } catch (e: Exception) {
                    _error.value = "Error de conexión"
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        if (query.length >= 3) {
            viewModelScope.launch {
                try {
                    val sessionToken = _session.value?.token ?: return@launch
                    val response = RetrofitClient.instance.searchSpotify(sessionToken, query)
                    if (response.isSuccessful) {
                        val tracks = response.body()?.tracks?.items ?: emptyList()
                        _searchResults.value = tracks.map { track ->
                            SongItem(
                                id = track.id,
                                title = track.name,
                                artist = track.artists.joinToString { it.name },
                                duration = String.format("%d:%02d", (track.duration_ms / 1000) / 60, (track.duration_ms / 1000) % 60),
                                isFromBarCatalog = true,
                                spotifyId = track.id
                            )
                        }
                    }
                } catch (e: Exception) {
                }
            }
        }
    }

    fun onRequestSong(songId: String) {
        val token = _session.value?.token ?: return
        val song = _searchResults.value.find { it.id == songId } ?: return
        
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.requestSong(
                    token,
                    ClientRequestSongRequest(
                        spotify_id = song.spotifyId ?: "",
                        title = song.title,
                        artist = song.artist,
                        duration_ms = 0
                    )
                )
                if (response.isSuccessful) {
                    val request = response.body()
                    if (request != null) {
                        val newUserRequest = UserRequest(
                            id = request.id.toString(),
                            title = request.track.title,
                            artist = request.track.artist,
                            status = when(request.status) {
                                StatusEnum.PENDING -> RequestStatus.PENDING
                                StatusEnum.PLAYING -> RequestStatus.IN_QUEUE
                                StatusEnum.PLAYED -> RequestStatus.PLAYED
                                StatusEnum.SKIPPED -> RequestStatus.REJECTED
                            }
                        )
                        _userRequests.value = _userRequests.value + newUserRequest
                    }
                    _availableCredits.value -= 1
                    _usedCredits.value += 1
                    fetchQueue()
                } else {
                    _error.value = "No tienes créditos suficientes o error al solicitar"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onBackClick() {}
    fun onHelpClick() {}
    fun onEnterOrderCodeClick() {}
    fun onViewQueueClick() {}
    fun onSearchTrackClick() {}
    fun onReloadCreditsClick() {}
    fun onHomeClick() {}
    fun onRequestsClick() {}
    fun onMenuClick() {}
    fun onBillsClick() {}
    fun onProfileClick() {}
    fun onSearchTabChange(index: Int) { _selectedSearchTab.value = index }
}
