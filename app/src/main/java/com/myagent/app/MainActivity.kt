package com.myagent.app

import com.myagent.app.ui.MementoTheme
import com.myagent.app.ui.RootScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat

/**
 * Memento-App 主 Activity — 单一 Activity 架构，Jetpack Compose。
 *
 * 作为 Memento-X 桌面端的移动伴侣，提供：
 * - 素材采集（拍照/相册）
 * - 共享素材库浏览
 * - 任务完成通知接收
 * - 账户管理
 */
class MainActivity : ComponentActivity() {
  private val viewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    WindowCompat.setDecorFitsSystemWindows(window, false)

    setContent {
      val appearanceMode by viewModel.appearanceMode.collectAsState()
      val skinMode by viewModel.skinMode.collectAsState()
      MementoTheme(themeMode = appearanceMode, skin = skinMode) {
        RootScreen(viewModel = viewModel)
      }
    }
  }
}