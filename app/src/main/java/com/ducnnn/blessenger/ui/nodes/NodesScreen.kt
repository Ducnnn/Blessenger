package com.ducnnn.blessenger.ui.nodes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ducnnn.blessenger.components.glassmorphic

@Composable
fun NodesScreen(
    viewModel: NodeScreenViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 20.dp, vertical = 4.dp)
                .clip(
                    RoundedCornerShape(
                        bottomEnd = 16.dp,
                        bottomStart = 16.dp
                    )
                )
                .glassmorphic(backgroundAlpha = 0.1f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                text = "Device Id",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                text = "Signal Strength",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                text = "Last Time Seen",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.nodes) { Node ->
                NodeRow(Node)
            }
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
            .glassmorphic(backgroundAlpha = 0.2f),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            text = node.deviceName
        )
        Text(
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            text = "${node.rssi}"
        )
        Text(
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
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
            modifier = Modifier.padding(horizontal = 8.dp),
            fontWeight = FontWeight.Bold,
            text = "Device name"
        )
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            fontWeight = FontWeight.Bold,
            text = "rsii"
        )
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            fontWeight = FontWeight.Bold,
            text = "last seen"
        )
    }
}


