package com.myagent.app.asset

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.myagent.app.asset.AssetRepository.Asset
import com.myagent.app.asset.AssetRepository.AssetType

/**
 * 素材列表 UI — 只读浏览共享素材库。
 *
 * 素材选择在 PC 端（Memento-X）完成，App 端仅提供浏览。
 */
@Composable
fun AssetList(
  assets: List<Asset>,
  modifier: Modifier = Modifier,
  onRefresh: () -> Unit = {},
) {
  if (assets.isEmpty()) {
    AssetEmptyState(modifier = modifier, onRefresh = onRefresh)
  } else {
    LazyColumn(
      modifier = modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      items(assets, key = { it.id }) { asset ->
        AssetCard(asset = asset)
      }
    }
  }
}

@Composable
private fun AssetCard(asset: Asset) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant,
    ),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
        imageVector = assetTypeIcon(asset.type),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp),
      )
      Spacer(modifier = Modifier.width(12.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = asset.name,
          style = MaterialTheme.typography.titleSmall,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          text = asset.type.name.lowercase(),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
  }
}

@Composable
private fun AssetEmptyState(modifier: Modifier, onRefresh: () -> Unit) {
  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = "素材库为空",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "在 PC 端 Memento-X 中管理素材",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Spacer(modifier = Modifier.height(16.dp))
      TextButton(onClick = onRefresh) {
        Text("刷新")
      }
    }
  }
}

private fun assetTypeIcon(type: AssetType): ImageVector = when (type) {
  AssetType.IMAGE -> Icons.Outlined.Image
  AssetType.VIDEO -> Icons.Outlined.Movie
  AssetType.CHARACTER -> Icons.Outlined.Person
  AssetType.BACKGROUND -> Icons.Outlined.Image
  AssetType.UNKNOWN -> Icons.Outlined.Image
}