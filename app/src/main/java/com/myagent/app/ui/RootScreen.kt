package com.myagent.app.ui

import com.myagent.app.MainViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 根路由 — 单一入口，根据登录状态决定显示内容。
 *
 * 与 Memento-3.1.2 不同，不再有欢迎页/激活/模型下载/引导流程。
 * 简化为：未登录 → 登录页，已登录 → 主界面 Shell。
 */
@Composable
fun RootScreen(viewModel: MainViewModel) {
  val isLoggedIn by viewModel.isLoggedIn.collectAsState()

  if (isLoggedIn) {
    ShellScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
  } else {
    LoginScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
  }
}

/**
 * 登录页 — 简洁的账户登录入口。
 *
 * 当前使用占位 UI，后续接入 Memento-X 共享账户系统的登录流程。
 */
@Composable
private fun LoginScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .background(MaterialTheme.colorScheme.background)
      .fillMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      Text(
        text = "Memento",
        style = MaterialTheme.typography.headlineLarge.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 36.sp,
          letterSpacing = 4.sp,
        ),
        color = MaterialTheme.colorScheme.onBackground,
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Memento-X 移动伴侣",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
      )
      Spacer(modifier = Modifier.height(32.dp))
      Button(
        onClick = {
          // 占位：直接模拟登录；后续替换为 Memento-X 账户系统授权流程
          viewModel.login(
            token = "placeholder_token",
            refreshToken = "placeholder_refresh",
            userId = "local_user",
            username = "本地用户",
          )
        },
        shape = RoundedCornerShape(12.dp),
      ) {
        Icon(
          imageVector = Icons.Outlined.Login,
          contentDescription = null,
          modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = "进入 Memento")
      }
    }
  }
}