package com.ducnnn.blessenger.ui.contacts

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ducnnn.blessenger.components.glassmorphic

@Composable
fun ContactsScreen(
    viewModel: ContactsScreenViewModel,
    onContactClick: (String, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when {
        uiState.showAddContactDialog -> {
            AddContactDialog(
                id = uiState.newContactId,
                name = uiState.newContactName,
                error = uiState.addError,
                onIdChanged = viewModel::onNewContactIdChanged,
                onNameChanged = viewModel::onNewContactNameChanged,
                onConfirm = viewModel::addContact,
                onDismiss = viewModel::dismissAddContactDialog
            )
        }

        uiState.contactPendingDeletion != null -> {
            DeleteContactDialog(
                onConfirm = viewModel::deleteContact,
                onDismiss = viewModel::dismissDeleteContactDialog
            )
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(uiState.contacts.entries.toList()) { entry ->
                ContactRow(
                    id = entry.key,
                    contactName = entry.value,
                    onClick = onContactClick,
                    onLongClick = viewModel::showDeleteContactDialog
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = viewModel::showAddContactDialog,
            modifier = Modifier
                .glassmorphic(),
            colors = ButtonColors(
                Color.Transparent,
                contentColor = Color.White,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = Color.White
            )
        ) {
            Text("Add Contact")
        }
    }
}


@Composable
fun ContactRow(
    id: String,
    contactName: String,
    onClick: (String, String) -> Unit,
    onLongClick: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .height(80.dp)
            .width(300.dp)
            .glassmorphic(backgroundAlpha = 0.4f)
            .combinedClickable(
                onClick = { onClick(id, contactName) },
                onLongClick = { onLongClick(id) }
            )

    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                fontSize = 24.sp,
                text = contactName,
            )
            Text(
                fontSize = 12.sp,
                text = id,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactDialog(
    id: String, name: String, error: String?,
    onIdChanged: (String) -> Unit, onNameChanged: (String) -> Unit,
    onConfirm: (String, String) -> Unit, onDismiss: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        ),

        ) {
        Surface(
            modifier = Modifier
                .glassmorphic()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Add Contact")
                OutlinedTextField(
                    value = id,
                    onValueChange = onIdChanged,
                    placeholder = { Text("Id") },
                    maxLines = 1,
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChanged,
                    placeholder = { Text("Name") },
                    maxLines = 1,
                )
                Row {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    TextButton(onClick = { onConfirm(id, name) }) { Text("Add") }
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteContactDialog(
    onConfirm: () -> Unit, onDismiss: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        ),

        ) {
        Surface(modifier = Modifier.glassmorphic()) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    "Are you sure you want to delete contact?",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    TextButton(onClick = onConfirm) {
                        Text(
                            "Delete",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

    }
}