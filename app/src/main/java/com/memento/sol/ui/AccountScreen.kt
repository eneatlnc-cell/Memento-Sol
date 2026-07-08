package com.memento.sol.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.memento.sol.account.AccountManager

@Composable
fun AccountScreen(accountManager: AccountManager) {
  val isLoggedIn = remember { mutableStateOf(accountManager.isLoggedIn()) }
  var showLoginDialog by remember { mutableStateOf(false) }
  var phone by remember { mutableStateOf("") }
  var code by remember { mutableStateOf("") }

  Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
    Text("账号", style = MaterialTheme.typography.headlineMedium)
    Spacer(Modifier.height(32.dp))

    if (isLoggedIn.value) {
      Icon(Icons.Outlined.AccountCircle, null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary)
      Spacer(Modifier.height(16.dp))
      Text("已登录", style = MaterialTheme.typography.titleMedium)
      Spacer(Modifier.height(32.dp))
      OutlinedButton(onClick = { accountManager.logout(); isLoggedIn.value = false }, modifier = Modifier.fillMaxWidth().height(48.dp)) { Text("退出登录") }
    } else {
      Icon(Icons.Outlined.AccountCircle, null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
      Spacer(Modifier.height(16.dp))
      Text("未登录", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
      Spacer(Modifier.height(8.dp))
      Text("登录 Memento-X 账户以同步素材", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
      Spacer(Modifier.height(32.dp))
      Button(onClick = { showLoginDialog = true }, modifier = Modifier.fillMaxWidth().height(48.dp)) { Text("登录") }
      Spacer(Modifier.height(16.dp))
      TextButton(onClick = { showLoginDialog = true }) { Text("快速登录（开发模式）") }
    }

    Spacer(Modifier.weight(1f))
    Text("Memento-Sol v4.0.0", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
  }

  if (showLoginDialog) {
    AlertDialog(
      onDismissRequest = { showLoginDialog = false },
      title = { Text("登录") },
      text = {
        Column {
          OutlinedTextField(phone, { phone = it }, label = { Text("手机号") }, singleLine = true, modifier = Modifier.fillMaxWidth())
          Spacer(Modifier.height(8.dp))
          OutlinedTextField(code, { code = it }, label = { Text("验证码") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        }
      },
      confirmButton = { Button(onClick = { isLoggedIn.value = true; showLoginDialog = false }) { Text("登录") } },
      dismissButton = { TextButton(onClick = { showLoginDialog = false }) { Text("取消") } },
    )
  }
}