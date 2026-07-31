package com.ducnnn.blessenger.ui.nodes


data class NearbyNode(
    val deviceName: String,
    val rssi : Int,
    val lastSeen : Long
)
data class NodeScreenState(
    val nodes : List<NearbyNode> = emptyList()
)
