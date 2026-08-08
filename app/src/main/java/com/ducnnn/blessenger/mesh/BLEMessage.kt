package com.ducnnn.blessenger.mesh

import com.ducnnn.blessenger.ui.chat.BLEMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.nio.ByteBuffer

data class NetworkMeshMessage(
    val messageId : String = "",
    val senderId: String,
    val targetId: String,
    val text: String,
    var ttl: Int
) {
    fun toByteArray(): ByteArray {
        val textBytes = text.toByteArray(Charsets.UTF_8)
        val senderIdBytes = senderId.toByteArray(Charsets.UTF_8)
        val targetIdBytes = targetId.toByteArray(Charsets.UTF_8)
        val buffer = ByteBuffer.allocate(senderIdBytes.size + targetIdBytes.size + textBytes.size)
        buffer.put(senderIdBytes)
        buffer.put(targetIdBytes)
        buffer.putInt(ttl)
        buffer.put(textBytes)
        return buffer.array()
    }
    companion object {
        fun fromByteArray(byteArray : ByteArray) : NetworkMeshMessage {
            val buffer = ByteBuffer.wrap(byteArray)

        }
    }
}

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
                networkMessage.targetId != myDeviceId
            ) {
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
            fromCurrentUser = false
        )

        _incomingMessages.emit(uiMessage)
    }

    private fun cleanUpStaleMessages() {
        val now = System.currentTimeMillis()
        seenMessages.entries.removeAll { now - it.value > 300_000 }
    }

}