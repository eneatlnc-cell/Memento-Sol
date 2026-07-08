package com.memento.sol.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.memento.sol.asset.AssetEntity
import com.memento.sol.asset.AssetList
import com.memento.sol.asset.AssetSync
import kotlinx.coroutines.launch

@Composable
fun AssetListScreen(initialTaskId: String? = null, assetSync: AssetSync) {
  var assets by remember { mutableStateOf<List<AssetEntity>>(emptyList()) }
  var syncState by remember { mutableStateOf<AssetSync.SyncState>(AssetSync.SyncState.Idle) }
  var showResultViewer by remember { mutableStateOf<String?>(initialTaskId) }
  val scope = rememberCoroutineScope()

  LaunchedEffect(Unit) {
    syncState = AssetSync.SyncState.Syncing
    syncState = assetSync.sync()
    assets = assetSync.getLocalAssets()
  }

  LaunchedEffect(initialTaskId) { if (initialTaskId != null) showResultViewer = initialTaskId }

  Column(modifier = Modifier.fillMaxSize()) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
      Text("素材库", style = MaterialTheme.typography.titleLarge)
      TextButton(onClick = { scope.launch { syncState = AssetSync.SyncState.Syncing; syncState = assetSync.sync(); assets = assetSync.getLocalAssets() } }) { Text("刷新") }
    }
    if (syncState is AssetSync.SyncState.Syncing) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    AssetList(assets = assets, onRefresh = { scope.launch { syncState = assetSync.sync(); assets = assetSync.getLocalAssets() } }, onItemClick = { asset -> if (asset.isResult) showResultViewer = asset.assetId })
  }

  showResultViewer?.let { taskId ->
    AlertDialog(
      onDismissRequest = { showResultViewer = null },
      title = { Text("成片预览") },
      text = { Column { Text("任务 ID: $taskId"); Spacer(Modifier.height(8.dp)); Text("视频播放器开发中...", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } },
      confirmButton = { TextButton(onClick = { showResultViewer = null }) { Text("关闭") } },
    )
  }
}

@Composable
fun ResultViewerScreen(taskId: String, onDismiss: () -> Unit) {
  val app = com.memento.sol.NodeApp.instance
  val resultViewer = remember { com.memento.sol.asset.ResultViewer(app, app.api) }
  val scope = rememberCoroutineScope()
  var isDownloading by remember { mutableStateOf(false) }
  var downloadProgress by remember { mutableStateOf(0f) }
  var downloadedFile by remember { mutableStateOf<java.io.File?>(null) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  val context = androidx.compose.ui.platform.LocalContext.current

  Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
    Text("成片预览", style = MaterialTheme.typography.headlineMedium)
    Spacer(Modifier.height(16.dp))
    Text("任务 ID: $taskId", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.height(32.dp))
    if (isDownloading) { CircularProgressIndicator(); Spacer(Modifier.height(8.dp)); Text("下载中... ${(downloadProgress * 100).toInt()}%", style = MaterialTheme.typography.bodySmall); if (downloadProgress > 0) LinearProgressIndicator(progress = { downloadProgress }, modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)) }
    downloadedFile?.let { Text("已下载: ${it.name}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary); Spacer(Modifier.height(16.dp)); Button(onClick = { openVideo(context, it) }) { Text("播放视频") } }
    errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
    Spacer(Modifier.height(24.dp))
    Row { Button(onClick = { scope.launch { isDownloading = true; errorMessage = null; val r = resultViewer.downloadResult(taskId) { downloadProgress = it }; isDownloading = false; r.fold(onSuccess = { downloadedFile = it }, onFailure = { e -> errorMessage = e.message }) } }, enabled = !isDownloading) { Text("下载成片") }; Spacer(Modifier.width(16.dp)); TextButton(onClick = onDismiss) { Text("关闭") } }
  }
}

private fun openVideo(context: android.content.Context, file: java.io.File) {
  val uri = androidx.core.content.FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
  context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW).apply { setDataAndType(uri, "video/*"); addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION) })
}