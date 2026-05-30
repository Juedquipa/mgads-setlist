package com.unab.mgads.setlist.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ClienteViewModel : ViewModel() {
    
    private val _isScannerActive = MutableStateFlow(false)
    val isScannerActive = _isScannerActive.asStateFlow()

    fun onScanButtonClick() {
        // Lógica para iniciar el escaneo
        _isScannerActive.value = true
    }
    
    fun resetScannerState() {
        _isScannerActive.value = false
    }
}
