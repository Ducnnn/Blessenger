package com.ducnnn.blessenger.ui.nodes


data class NearbyNode(
    val deviceName: String,
    val rssi : Int,
    val lastSeen : String
)
data class NodeScreenState(
    val nodes : List<NearbyNode> = emptyList(),
    val addContactName : String = "",
    val addContactId : String = "",
    val showAddContactDialog: Boolean = false
)
