package com.memento.sol.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.memento.sol.NodeApp
import com.memento.sol.asset.AssetEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(initialTaskId: String? = null) {
  var selectedTab by remember { mutableStateOf(if (initialTaskId != null) "asset" else "capture") }
  val app = NodeApp.instance
  val scope = rememberCoroutineScope()

  val navItems = listOf(
    NavItem("capture", "采集", Icons.Outlined.CameraAlt),
    NavItem("asset", "素材库", Icons.Outlined.Folder),
    NavItem("account", "账号", Icons.Outlined.Person),
  )

  Scaffold(
    bottomBar = {
      NavigationBar {
        navItems.forEach { item ->
          NavigationBarItem(
            icon = { Icon(item.icon, contentDescription = item.label) },
            label = { Text(item.label) },
            selected = selectedTab == item.key,
            onClick = { selectedTab = item.key },
          )
        }
      }
    },
  ) { innerPadding ->
    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
      when (selectedTab) {
        "capture" -> CaptureScreen(
          cameraCapture = app.cameraCapture,
          uploadManager = app.uploadManager,
          onUploadSuccess = { scope.launch { app.assetSync.sync() } },
        )
        "asset" -> AssetListScreen(initialTaskId = initialTaskId, assetSync = app.assetSync)
        "account" -> AccountScreen(accountManager = app.accountManager)
      }
    }
  }
}

private data class NavItem(val key: String, val label: String, val icon: ImageVector)