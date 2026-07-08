package com.memento.sol.asset

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun AssetList(
  assets: List<AssetEntity>,
  modifier: Modifier = Modifier,
  onRefresh: () -> Unit = {},
  onItemClick: (AssetEntity) -> Unit = {},
) {
  if (assets.isEmpty()) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("素材库为空", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))
        Text("拍照或从相册选择素材上传", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onRefresh) { Text("刷新") }
      }
    }
  } else {
    LazyColumn(
      modifier = modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      items(assets, key = { it.id }) { asset ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          onClick = { onItemClick(asset) },
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        ) {
          Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = if (asset.type == "video") Icons.Outlined.Movie else Icons.Outlined.Image,
              contentDescription = null,
              tint = if (asset.isResult) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(40.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(asset.name, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
                if (asset.isResult) { Spacer(Modifier.width(8.dp)); SuggestionChip(onClick = {}, label = { Text("成片", style = MaterialTheme.typography.labelSmall) }) }
              }
              Text(statusLabel(asset.status), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
          }
        }
      }
    }
  }
}

private fun statusLabel(status: String): String = when (status) {
  "syncing" -> "同步中..."; "synced" -> "已同步"; "failed" -> "同步失败"; else -> status
}