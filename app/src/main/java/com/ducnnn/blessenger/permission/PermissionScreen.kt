package com.ducnnn.blessenger.permission


import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun PermissionScreen(
    onAllPermissionsGranted: () -> Unit,
    viewModel: PermissionViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initialize(context)
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.refreshPermissionStates(context)
    }

    LaunchedEffect(state.allGranted) {
        if (state.allGranted) {
            onAllPermissionsGranted()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract =
            ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        viewModel.onPermissionResults(context, result)
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Permitions",
                style =
                    MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "For proper Blessenger work" +
                        " following permissions are required",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {
                items(state.permissions) { permissionItem ->
                    PermissionCard(permissionItem)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.showRationale) {
                Text(
                    text = "some of required permissions are disabled" +
                            " open settings to turn activate them",
                    style =
                        MaterialTheme.typography.bodyMedium,
                    textAlign =
                        TextAlign.Center,
                    color = MaterialTheme.colorScheme.error,
                    modifier =
                        Modifier.padding(
                            bottom = 8.dp
                        )
                )

                OutlinedButton(
                    onClick = {
                        viewModel.openAppSettings(context)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Open settings")
                }

                Spacer(modifier = Modifier.height(8.dp))

            }

            Button(
                onClick = {
                    val denied = state.permissions
                        .filter { it -> !it.isGranted}
                        .map {it -> it.permission}
                        .toTypedArray()

                    if (denied.isNotEmpty()) {
                        permissionLauncher.launch(denied)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.allGranted
            ) {
                Text(
                    text = if (state.allGranted) "All permissions were received"
                        else "Provide permissions"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

    }

}


@Composable
private fun PermissionCard(item: PermissionItemState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                item.isGranted ->
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                item.isPermanentlyDenied ->
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                else ->
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically

        ) {
            StatusIndicator(
                isGranted = item.isGranted,
                isPermanentlyDenied =
                    item.isPermanentlyDenied
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.displayName,
                    style =
                        MaterialTheme.typography.titleSmall,
                    fontWeight =
                        FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.description,
                    style =
                        MaterialTheme.typography.bodySmall,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )

            }

            Text(
                text = when {
                    item.isGranted -> "✅"
                    item.isPermanentlyDenied -> "❌"
                    else -> "➖"
                },
                style =
                    MaterialTheme.typography.titleMedium,
                color =
                    when {
                        item.isGranted ->
                            MaterialTheme.colorScheme.primary
                        item.isPermanentlyDenied ->
                            MaterialTheme.colorScheme.error
                        else ->
                            MaterialTheme.colorScheme.onSurfaceVariant
                    }
            )
        }
    }
}

@Composable
private fun StatusIndicator(isGranted: Boolean,
                            isPermanentlyDenied: Boolean) {
    val color = when {
        isGranted -> Color(0xFF4CAF50)
        isPermanentlyDenied -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline
    }
    Spacer(
        modifier = Modifier.size(12.dp)
            .clip(CircleShape)
            .background(color)
    )
}
