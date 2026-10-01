package com.ducnnn.blessenger.ui.nodes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ducnnn.blessenger.mesh.BleManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class NodeScreenViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NodeScreenState())
    val uiState: StateFlow<NodeScreenState> = _uiState.asStateFlow()
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        .withZone(ZoneId.systemDefault())
    init {
        observeBleManagerDeviceList()
    }

    private fun observeBleManagerDeviceList() {
        viewModelScope.launch {
            BleManager.leDeviceList.collect { newList ->
                _uiState.update {
                    NodeScreenState(newList.map { item ->

                        NearbyNode(
                            item.deviceName,
                            item.rssi,
                            timeFormatter.format(Instant.ofEpochMilli(item.lastSeenMs)))
                    })
                }
            }
        }
    }
}