package com.ducnnn.blessenger.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ducnnn.blessenger.mesh.BleManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.ducnnn.blessenger.mesh.MeshRouter
import com.ducnnn.blessenger.mesh.NetworkMeshMessage
import com.ducnnn.blessenger.user.UserDataManager


class ChatScreenViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ChatState())
    val uiState: StateFlow<ChatState> = _uiState.asStateFlow()

    init {
        loadInitialMessages()
        observeIncomingMeshMessages()
    }

    private fun observeIncomingMeshMessages() {
        viewModelScope.launch {
            MeshRouter.incomingMessages.collect {
                incomingBleMessage ->
                _uiState.update {
                    currentState -> currentState.copy(
                        messages = currentState.messages + incomingBleMessage
                    )
                }
            }
        }
    }

    fun onInputTextChanged(newText: String) {
        _uiState.update { currentState ->
            currentState.copy(inputText = newText)
        }
    }

    fun sendMessage() {
        val currentText = _uiState.value.inputText
        if (currentText.isBlank()) return

        val newUserMessage = BLEMessage(
            text = currentText.trim(),
            sender = UserDataManager.getSavedId(),
            fromCurrentUser = true
        )
        //TODO(Make BLE sending logic here)
        val meshMessage = NetworkMeshMessage(
            targetId = "SampleId",
            senderId = UserDataManager.getSavedId(),
            messageId = "",
            text = currentText,
            ttl = 1
        )
        BleManager.startMessageAdvertising(meshMessage)

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
                BLEMessage(
                    text = "Hello! How can I help you today?",
                    sender = "AnotherUserID",
                    fromCurrentUser = false
                ),
                BLEMessage(
                    text = "Hello! I'm Gleb",
                    sender = "AnotherUserID",
                    fromCurrentUser = false
                ),
                BLEMessage(
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

