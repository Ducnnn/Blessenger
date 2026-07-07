package com.ducnnn.blessenger.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatScreenViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        loadInitialMessages()
    }

    fun onInputTextChanged(newText: String) {
        _uiState.update { currentState ->
            currentState.copy(inputText = newText)
        }
    }

    fun sendMessage() {
        val currentText = _uiState.value.inputText
        if (currentText.isBlank()) return

        val newUserMessage = BLEmessage(
            text = currentText.trim(),
            sender = "MyUserID",
            fromCurrentUser = true
        )
        //TODO(Make BLE sending logic here)
        _uiState.update { currentState ->
            currentState.copy(
                messages = currentState.messages + newUserMessage,
                inputText = ""
            )
        }
    }

    fun changeChatMode(chatMode: ChatMode) {
        _uiState.update { currentState ->
            currentState.copy(chatMode = chatMode)
        }
    }
    private fun loadInitialMessages() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            //TODO(Implement loading message history here)
            //Dummy message history
            val history = listOf(
                BLEmessage(
                    text = "Hello! How can I help you today?",
                    sender = "AnotherUserID",
                    fromCurrentUser = false
                ),
                BLEmessage(
                    text = "Hello! I'm Gleb",
                    sender = "AnotherUserID",
                    fromCurrentUser = false
                ),
                BLEmessage(
                    text = "I sent this message through BLE",
                    sender = "AnotherUserID",
                    fromCurrentUser = false
                )

            )
            _uiState.update { currentState ->
                currentState.copy(
                    messages = history,
                    isLoading = false
                )
            }
        }
    }
}

enum class ChatMode(val value : String) {
    MESH("Mesh group"), P2P("Contacts")
}

data class BLEmessage(
    val text: String,
    val sender: String,
    val fromCurrentUser: Boolean
)

data class ChatUiState(
    val messages: List<BLEmessage> = emptyList(),
    val inputText: String = "",
    val chatMode: ChatMode = ChatMode.MESH,
    val isLoading: Boolean = false
)
