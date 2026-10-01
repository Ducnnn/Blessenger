package com.ducnnn.blessenger.ui.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ducnnn.blessenger.db.DatabaseManager
import com.ducnnn.blessenger.mesh.MeshRouter
import com.ducnnn.blessenger.mesh.NetworkMeshMessage
import com.ducnnn.blessenger.user.UserDataManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class ChatScreenViewModel(contactId: String = "ffffffff") : ViewModel() {
    private val _uiState = MutableStateFlow(ChatState())
    val uiState: StateFlow<ChatState> = _uiState.asStateFlow()

    init {
        setContactId(contactId)
        loadInitialMessages(contactId)
        observeIncomingMeshMessages()
    }

    private fun setContactId(contactId: String) {
        _uiState.update { currentState ->
            currentState.copy(
                contactId = contactId
            )
        }
    }

    private fun observeIncomingMeshMessages() {
        viewModelScope.launch {
            MeshRouter.incomingMessages.collect { incomingBleMessage ->
                _uiState.update { currentState ->
                    if (_uiState.value.contactId != "ffffffff") return@collect
                    currentState.copy(
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

        val meshMessage = NetworkMeshMessage(
            targetId = uiState.value.contactId,
            senderId = UserDataManager.getSavedId(),
            messageId = UserDataManager.generateHexId(),
            text = currentText,
            ttl = 7
        )
        Log.d(
            "ChatScreenViewModel", "sendMessage() got message to send with messageId:" +
                    " ${meshMessage.messageId}, targetId:${meshMessage.targetId} with length ${meshMessage.text.length}"
        )
        MeshRouter.addMessageToQueue(meshMessage)

        if (meshMessage.targetId == "ffffffff") {
            _uiState.update { it.copy(messages = it.messages + newUserMessage, inputText = "") }
        } else {
            viewModelScope.launch { DatabaseManager.addMessage(meshMessage) }
            _uiState.update { it.copy(inputText = "") }
        }
    }

    fun changeChatMode(chatMode: ChatMode) {
        _uiState.update { currentState ->
            currentState.copy(chatMode = chatMode)
        }
    }

    private fun loadInitialMessages(contactId: String) {
        if (contactId == "ffffffff") return

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            DatabaseManager.observeMessages(contactId).catch { e ->
                Log.e("ChatScreenViewModel", "observeMessages failed: $e")
                emit(emptyList())
            }.collect { messages ->
                _uiState.update { currentState ->
                    currentState.copy(
                        messages = messages,
                        isLoading = false
                    )
                }

            }
        }
    }
}

