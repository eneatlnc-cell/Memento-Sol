package com.memento.sol.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Pin
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.memento.sol.NodeApp
import kotlinx.coroutines.launch

/**
 * M3 全屏登录界面。
 *
 * 拦截未登录用户，提供手机号登录。
 * 注册统一在 Memento-X Web 端完成，App 不提供注册入口。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen() {
  val app = NodeApp.instance
  val scope = rememberCoroutineScope()
  val focusManager = LocalFocusManager.current

  var phone by remember { mutableStateOf("") }
  var code by remember { mutableStateOf("") }
  var isLoading by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var codeSent by remember { mutableStateOf(false) }
  var countdown by remember { mutableStateOf(0) }

  // 倒计时
  if (countdown > 0) {
    LaunchedEffect(countdown) {
      kotlinx.coroutines.delay(1000)
      countdown--
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface,
        ),
      )
    },
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Spacer(Modifier.height(48.dp))

      // ── 品牌头部 ──
      Surface(
        modifier = Modifier.size(72.dp),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.primaryContainer,
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            Icons.Outlined.Lock,
            contentDescription = null,
            modifier = Modifier.size(36.dp),
            tint = MaterialTheme.colorScheme.primary,
          )
        }
      }

      Spacer(Modifier.height(24.dp))

      Text(
        "Memento",
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
      )

      Spacer(Modifier.height(4.dp))

      Text(
        "登录您的账户以继续",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
      )

      Spacer(Modifier.height(40.dp))

      // ── 手机号输入 ──
      OutlinedTextField(
        value = phone,
        onValueChange = { if (it.length <= 11) phone = it.filter { c -> c.isDigit() } },
        label = { Text("手机号") },
        placeholder = { Text("请输入手机号") },
        leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        modifier = Modifier.fillMaxWidth(),
        enabled = !isLoading,
        shape = MaterialTheme.shapes.medium,
      )

      Spacer(Modifier.height(16.dp))

      // ── 验证码输入 ──
      OutlinedTextField(
        value = code,
        onValueChange = { if (it.length <= 6) code = it.filter { c -> c.isDigit() } },
        label = { Text("验证码") },
        placeholder = { Text("请输入验证码") },
        leadingIcon = { Icon(Icons.Outlined.Pin, contentDescription = null) },
        trailingIcon = {
          TextButton(
            onClick = {
              focusManager.clearFocus()
              codeSent = true
              countdown = 60
            },
            enabled = phone.length >= 11 && !isLoading && countdown == 0,
          ) {
            Text(if (countdown > 0) "${countdown}s" else if (codeSent) "重新发送" else "获取验证码")
          }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        modifier = Modifier.fillMaxWidth(),
        enabled = !isLoading,
        shape = MaterialTheme.shapes.medium,
      )

      // ── 错误提示 ──
      AnimatedVisibility(
        visible = errorMessage != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
      ) {
        errorMessage?.let { msg ->
          Surface(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.errorContainer,
          ) {
            Text(
              msg,
              modifier = Modifier.padding(12.dp),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onErrorContainer,
            )
          }
        }
      }

      Spacer(Modifier.height(32.dp))

      // ── 登录按钮 ──
      Button(
        onClick = {
          focusManager.clearFocus()
          errorMessage = null
          isLoading = true
          scope.launch {
            try {
              val result = app.accountManager.login(phone, code)
              result.fold(
                onSuccess = {
                  app.isLoggedIn.value = true
                  isLoading = false
                },
                onFailure = { e ->
                  errorMessage = e.message ?: "登录失败，请重试"
                  isLoading = false
                },
              )
            } catch (e: Exception) {
              errorMessage = e.message ?: "网络错误，请检查连接"
              isLoading = false
            }
          }
        },
        modifier = Modifier.fillMaxWidth().height(52.dp),
        enabled = phone.length >= 11 && code.length >= 4 && !isLoading,
        shape = MaterialTheme.shapes.medium,
      ) {
        if (isLoading) {
          CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            strokeWidth = 2.dp,
            color = MaterialTheme.colorScheme.onPrimary,
          )
          Spacer(Modifier.width(8.dp))
        }
        Text(if (isLoading) "登录中..." else "登录", style = MaterialTheme.typography.titleMedium)
      }

      Spacer(Modifier.height(24.dp))

      // ── 注册引导 ──
      Text(
        "没有账户？请在 Memento-X Web 端注册",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
      )

      Spacer(Modifier.height(24.dp))
    }
  }
}