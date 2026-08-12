package com.ducnnn.blessenger.mesh

import java.nio.ByteBuffer

data class NetworkMeshMessage(
    val messageId: String,
    val senderId: String,
    val targetId: String,
    val text: String,
    var ttl: Byte
) {
    fun toByteArray(): ByteArray {
        val textBytes = text.toByteArray(Charsets.UTF_8)
        val buffer = ByteBuffer.allocate(13 + textBytes.size)
        buffer.put(messageId.hexToByteArray())
        buffer.put(senderId.hexToByteArray())
        buffer.put(targetId.hexToByteArray())
        buffer.put(ttl)
        buffer.put(textBytes)
        return buffer.array()
    }

    override fun toString(): String {
        return """
            messageId: $messageId
            senderId: $senderId
            targetId: $targetId
            text: $text
            ttl: $ttl
        """.trimIndent()
    }

    companion object {
        fun fromByteArray(byteArray: ByteArray): NetworkMeshMessage {
            val buffer = ByteBuffer.wrap(byteArray)
            val messageID = buffer.getInt().toHexString()
            val senderID = buffer.getInt().toHexString()
            val targetID = buffer.getInt().toHexString()
            val ttl = buffer.get()
            val textBytes = ByteArray(buffer.remaining())
            buffer.get(textBytes)
            val text = String(textBytes, Charsets.UTF_8)

            return NetworkMeshMessage(
                messageId = messageID,
                senderId = senderID,
                targetId = targetID,
                text = text,
                ttl = ttl
            )
        }
    }
}