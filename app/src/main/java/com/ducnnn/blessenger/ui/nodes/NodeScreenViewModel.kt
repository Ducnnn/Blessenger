package com.ducnnn.blessenger.ui.nodes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ducnnn.blessenger.mesh.BleManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds


class NodeScreenViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NodeScreenState())
    val uiState: StateFlow<NodeScreenState> = _uiState.asStateFlow()
    init {
        observeBleManagerDeviceList()
    }
    private fun observeBleManagerDeviceList() {
        viewModelScope.launch {
            BleManager.leDeviceList.collect { newList ->
                _uiState.update {
                    NodeScreenState(newList.map { item ->
                        NearbyNode(item.device.address, item.rssi, item.lastSeenMs.milliseconds.inWholeMinutes)
                    })
                }
            }
        }
    }
}