package com.myagent.app

import android.content.Context
import android.util.Log
import com.myagent.app.account.AccountManager
import com.myagent.app.account.TokenManager
import com.myagent.app.asset.AssetSync
import com.myagent.app.notification.NotificationReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * 运行时管理器 — 管理 App 各模块的生命周期。
 *
 * 精简后职责：
 * - 账户状态管理
 * - 素材同步调度
 * - 通知接收
 * - 未来：记忆/人格/搭讪模块初始化
 */
class NodeRuntime(private val context: Context) {

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

  /** 启动时初始化 */
  fun start() {
    Log.i(TAG, "NodeRuntime 启动")

    // 如果已登录，同步素材库
    scope.launch {
      if (AccountManager.isLoggedIn(context)) {
        val token = AccountManager.getToken(context) ?: return@launch
        AssetSync.sync(context, token)
      }
    }
  }

  /** 停止时清理 */
  fun stop() {
    Log.i(TAG, "NodeRuntime 停止")
  }

  /** 登录成功后初始化 */
  fun onLoginSuccess(token: String, refreshToken: String, userId: String, username: String) {
    scope.launch {
      AccountManager.login(context, token, refreshToken, userId, username)
      AssetSync.sync(context, token)
    }
  }

  /** 登出 */
  fun logout() {
    AccountManager.logout(context)
    TokenManager.clearToken(context)
  }

  companion object {
    private const val TAG = "NodeRuntime"
  }
}