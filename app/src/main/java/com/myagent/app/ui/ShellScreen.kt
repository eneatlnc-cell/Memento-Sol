package com.myagent.app.ui

import com.myagent.app.MainViewModel
import com.myagent.app.asset.AssetList
import com.myagent.app.ui.design.ClawBottomNav
import com.myagent.app.ui.design.ClawNavItem
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * 主界面 Shell — 底部导航栏切换素材、采集、设置三个页面。
 *
 * Memento-App 的核心导航：
 * - 素材：浏览共享素材库（与 Memento-X 桌面端共享）
 * - 采集：拍照/相册采集素材，上传至共享素材库
 * - 设置：账户管理、主题切换、关于
 */
@Composable
fun ShellScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier,
) {
  var selectedTab by rememberSaveable { mutableStateOf("asset") }

  val navItems = listOf(
    ClawNavItem(key = "asset", label = "素材", icon = Icons.Outlined.Folder),
    ClawNavItem(key = "capture", label = "采集", icon = Icons.Outlined.CameraAlt),
    ClawNavItem(key = "settings", label = "设置", icon = Icons.Outlined.Settings),
  )

  Scaffold(
    modifier = modifier,
    bottomBar = {
      ClawBottomNav(
        items = navItems,
        selectedKey = selectedTab,
        onSelect = { selectedTab = it },
      )
    },
  ) { innerPadding ->
    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
      when (selectedTab) {
        "asset" -> {
          val assets by viewModel.assets.collectAsState()
          val isSyncing by viewModel.isSyncing.collectAsState()
          AssetList(
            assets = assets,
            onRefresh = { viewModel.syncAssets() },
          )
        }
        "capture" -> CaptureScreen()
        "settings" -> SettingsScreen(viewModel = viewModel)
      }
    }
  }
}

/**
 * 采集页 — 拍照/相册选择入口（占位，后续接入 CameraCapture/GalleryPicker）。
 */
@Composable
private fun CaptureScreen(
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = "采集功能开发中...",
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
    )
  }
}

/**
 * 设置页 — 账户信息、主题、皮肤、关于。
 */
@Composable
private fun SettingsScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = "设置功能开发中...",
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
    )
  }
}