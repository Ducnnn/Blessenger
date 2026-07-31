package com.ducnnn.blessenger.mesh

import com.ducnnn.blessenger.ui.chat.BLEMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class NetworkMeshMessage(
    val messageId: String,
    val senderId: String,
    val targetId: String,
    val text: String,
    var ttl: Int
)

object MeshRouter {
    private val mutex = Mutex()
    private val seenMessages = mutableMapOf<String, Long>()

    private val myDeviceId = "MyUserID"

    private val _incomingMessages = MutableSharedFlow<BLEMessage>()
    val incomingMessages = _incomingMessages.asSharedFlow()
    private val scope = CoroutineScope(Dispatchers.Default)

    fun onMessageReceived(networkMessage: NetworkMeshMessage) {
        scope.launch {
            mutex.withLock {
                if (seenMessages.containsKey(networkMessage.messageId)) {
                    return@launch
                }

                seenMessages[networkMessage.messageId] = System.currentTimeMillis()
                cleanUpStaleMessages()
            }
            if (networkMessage.targetId == myDeviceId || networkMessage.targetId == "BROADCAST") {
                deliverToUI(networkMessage)
            }

            if (networkMessage.senderId != myDeviceId &&
                networkMessage.targetId != myDeviceId) {
                forwardMessage(networkMessage)
            }
        }
    }

    private fun forwardMessage(networkMessage: NetworkMeshMessage) {
        if (networkMessage.ttl > 0) {
            val forwardMessage = networkMessage.copy(ttl = networkMessage.ttl - 1)
        }
    }

    private suspend fun deliverToUI(networkMessage: NetworkMeshMessage) {
        val uiMessage = BLEMessage(
            text = networkMessage.text,
            sender = networkMessage.senderId,
            fromCurrentUser =  false
        )

        _incomingMessages.emit(uiMessage)
    }
    private fun cleanUpStaleMessages() {
        val now = System.currentTimeMillis()
        seenMessages.entries.removeAll { now - it.value > 300_000}
    }

}