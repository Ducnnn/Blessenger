package com.ducnnn.blessenger.mesh

import com.ducnnn.blessenger.ui.chat.BLEMessage
import com.ducnnn.blessenger.user.UserDataManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration.Companion.milliseconds

object MeshRouter {
    private val mutex = Mutex()
    private val seenMessages = mutableMapOf<String, Long>()
    private val myDeviceId = UserDataManager.getSavedId()
    private val _incomingMessages = MutableSharedFlow<BLEMessage>()
    val incomingMessages = _incomingMessages.asSharedFlow()
    private val scope = CoroutineScope(Dispatchers.Default)
    private var messageQueue: ArrayDeque<NetworkMeshMessage> = ArrayDeque()
    private var isEmptying: Boolean = false

    fun onMessageReceived(networkMessage: NetworkMeshMessage) {
        scope.launch {
            mutex.withLock {
                if (seenMessages.containsKey(networkMessage.messageId) || myDeviceId == networkMessage.senderId) {
                    return@launch
                }

                seenMessages[networkMessage.messageId] = System.currentTimeMillis()
                cleanUpStaleMessages()
            }
            if (networkMessage.targetId == myDeviceId || networkMessage.targetId == "ffffffff") {
                //Deliver message to cache group chat
                deliverToUI(networkMessage)
            }

            if (networkMessage.senderId != myDeviceId &&
                networkMessage.targetId != myDeviceId
            ) {
                forwardMessage(networkMessage)
            }
        }
    }

    fun notifyMessageSent(messageId: String) {
        seenMessages[messageId] = System.currentTimeMillis()
    }

    private fun forwardMessage(networkMessage: NetworkMeshMessage) {
        if (networkMessage.ttl > 0) {
            val forwardMessage = networkMessage.copy(ttl = (networkMessage.ttl - 1).toByte())
            addMessageToQueue(forwardMessage)
        }
    }

    private suspend fun deliverToUI(networkMessage: NetworkMeshMessage) {
        val uiMessage = BLEMessage(
            text = networkMessage.text,
            sender = networkMessage.senderId,
            fromCurrentUser = false
        )
        _incomingMessages.emit(uiMessage)
    }

    private fun cleanUpStaleMessages() {
        val now = System.currentTimeMillis()
        seenMessages.entries.removeAll { now - it.value > 300_000 }
    }

    fun addMessageToQueue(message: NetworkMeshMessage) {
        messageQueue.addLast(message)
    }

    fun getMessageFromQueue(): NetworkMeshMessage {
        return messageQueue.first()
    }

    suspend fun sendQueue() {
        if (isEmptying || messageQueue.isEmpty()) return
        isEmptying = true
        while (messageQueue.isNotEmpty()) {
            val message = getMessageFromQueue()
            BleManager.startMessageAdvertising(message)
            delay(15000.milliseconds)
        }
        isEmptying = false
    }
}