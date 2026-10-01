package com.ducnnn.blessenger.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ducnnn.blessenger.components.glassmorphic
import com.ducnnn.blessenger.ui.contacts.ContactsScreen

@Composable
fun ChatScreen(
    viewModel: ChatScreenViewModel = viewModel(),
    onContactClick: (String, String) -> Unit = {_, _ ->}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.chatMode == ChatMode.MESH) {
        Messages(
            uiState = uiState,
            onInputTextChanged = viewModel::onInputTextChanged,
            onSendMessage = viewModel::sendMessage
        )
    } else if (uiState.chatMode == ChatMode.P2P) {
        ContactsScreen(
            viewModel = viewModel(),
            onContactClick = onContactClick)
    }
}

@Composable
fun Messages(
    uiState: ChatState,
    onInputTextChanged: (String) -> Unit,
    onSendMessage: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
    ) {
        LazyColumn(
            reverseLayout = true,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(uiState.messages.asReversed()) { message ->
                MessageBubble(message = message)
            }
        }
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.padding(8.dp))
            }
        }
        ChatInput(
            inputText = uiState.inputText,
            onInputTextChanged = onInputTextChanged,
            onSendMessage = onSendMessage
        )
    }
}


@Composable
fun ChatInput(
    inputText: String,
    onInputTextChanged: (String) -> Unit,
    onSendMessage: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = inputText,
            onValueChange = onInputTextChanged,
            modifier = Modifier.weight(1f).glassmorphic(cornerRadius = 30.dp),
            placeholder = { Text("Type a message...") },
            maxLines = 3,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            ),
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            modifier = Modifier.glassmorphic(cornerRadius = 45.dp),
            onClick = onSendMessage,
            enabled = inputText.isNotBlank(),
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send message"
            )
        }
    }
}

@Composable
fun MessageBubble(message: BLEMessage) {
    val alignment = if (message.fromCurrentUser) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .widthIn(max = 280.dp)
                .glassmorphic(backgroundAlpha = 0.2f)
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.Start) {
                Text(
                    text = message.sender,
                    textAlign = TextAlign.Left,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = message.text,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

            }
        }
    }
}
