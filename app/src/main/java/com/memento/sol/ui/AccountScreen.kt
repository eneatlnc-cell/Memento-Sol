package com.memento.sol.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.memento.sol.NodeApp

/**
 * M3 账号页面。
 *
 * 已登录：展示用户信息 + 退出登录
 * 未登录：展示引导 + 登录按钮（理论上会被 MainScreen 拦截，但保留兜底）
 */
@Composable
fun AccountScreen() {
  val app = NodeApp.instance

  Column(
    modifier = Modifier.fillMaxSize().padding(32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text("账号", style = MaterialTheme.typography.headlineMedium)

    Spacer(Modifier.height(40.dp))

    if (app.isLoggedIn.value) {
      // ── 已登录 ──
      Surface(
        modifier = Modifier.size(88.dp),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.primaryContainer,
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            Icons.Outlined.AccountCircle,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.primary,
          )
        }
      }

      Spacer(Modifier.height(20.dp))

      Text(
        "已登录",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
      )

      Spacer(Modifier.height(8.dp))

      val token = app.accountManager.getToken()
      Text(
        token?.let { "UID: ${it.userId}" } ?: "",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )

      Spacer(Modifier.height(40.dp))

      OutlinedButton(
        onClick = {
          app.accountManager.logout()
          app.isLoggedIn.value = false
        },
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = MaterialTheme.shapes.medium,
      ) {
        Icon(Icons.Outlined.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text("退出登录")
      }
    } else {
      // ── 未登录（兜底态） ──
      Icon(
        Icons.Outlined.AccountCircle,
        contentDescription = null,
        modifier = Modifier.size(88.dp),
        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
      )

      Spacer(Modifier.height(16.dp))

      Text(
        "未登录",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )

      Spacer(Modifier.height(8.dp))

      Text(
        "登录 Memento-X 账户以同步素材",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
      )

      Spacer(Modifier.height(32.dp))

      Button(
        onClick = { /* MainScreen 已拦截，此处为兜底 */ },
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = MaterialTheme.shapes.medium,
      ) {
        Text("登录")
      }
    }

    Spacer(Modifier.weight(1f))

    Text(
      "Memento-Sol v4.0.0",
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
    )
  }
}