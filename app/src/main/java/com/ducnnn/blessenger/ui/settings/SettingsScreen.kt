package com.ducnnn.blessenger.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ducnnn.blessenger.ui.permission.PermissionItemState
import com.ducnnn.blessenger.ui.permission.PermissionViewModel
import com.ducnnn.blessenger.components.glassmorphic
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.TextButton
import androidx.compose.foundation.lazy.items

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel(),
                   permissionViewModel: PermissionViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val permState by permissionViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        permissionViewModel.initialize(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .glassmorphic(cornerRadius = 16.dp)
                .padding(16.dp)
        ) {
            Column {
                Text("My user ID:", color = Color.White.copy(alpha=0.7f))
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = uiState.userId,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Normal
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Permission Management",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(permState.permissions) { item ->
                GlassPermissionCard(
                    item = item,
                ) { permissionViewModel.openAppSettings((context)) }
            }
        }
    }

}

@Composable
fun GlassPermissionCard(
    item: PermissionItemState,
    onClickSettings: () -> Unit,

) {
    val bgColor = if (item.isGranted) Color.Green.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.1f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphic(backgroundAlpha = if (item.isGranted) 0.2f else 0.1f)
            .padding(16.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.displayName,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = if(item.isGranted) "Allowed" else "Denied",
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodySmall
            )
        }
        if (!item.isGranted) {
            TextButton(onClick = onClickSettings) {
                Text("Proceed", color = Color.White)
            }
        } else {
            Text("✅")
        }
    }
}

