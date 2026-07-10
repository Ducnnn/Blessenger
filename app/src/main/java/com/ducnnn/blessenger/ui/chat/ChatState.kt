package com.ducnnn.blessenger.ui.chat

enum class ChatMode(val value : String) {
    MESH("Mesh group"),
    P2P("Contacts")
}

data class BLEMessage(
    val text: String,
    val sender: String,
    val fromCurrentUser: Boolean
)

data class ChatUiState(
    val messages: List<BLEMessage> = emptyList(),
    val inputText: String = "",
    val chatMode: ChatMode = ChatMode.MESH,
    val isLoading: Boolean = false
)
