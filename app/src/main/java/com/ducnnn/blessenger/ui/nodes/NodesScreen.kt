package com.ducnnn.blessenger.ui.nodes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.deeplink.invoke

@Composable
fun NodesScreen(
    viewModel: NodeScreenViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(uiState.nodes) { Node ->
            NodeRow(Node)
        }
    }
}

@Composable
fun NodeRow(node: NearbyNode) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                color = Color(0xff3d9ccc)
            ),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            fontWeight = FontWeight.Bold,
            text = node.deviceName
        )
        Text(
            fontWeight = FontWeight.Bold,
            text = "${node.rssi}"
        )
        Text(
            fontWeight = FontWeight.Bold,
            text = "${node.lastSeen}"
        )
    }
}

@Preview
@Composable
fun NodeRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                color = Color(0xff3d9ccc)
            ),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            fontWeight = FontWeight.Bold,
            text = "Device name"
        )
        Text(
            fontWeight = FontWeight.Bold,
            text = "rsii"
        )
        Text(
            fontWeight = FontWeight.Bold,
            text = "last seen"
        )
    }
}


