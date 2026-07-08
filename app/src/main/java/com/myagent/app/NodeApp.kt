package com.myagent.app

import android.app.Application
import android.util.Log
import com.myagent.app.notification.NotificationReceiver
import com.myagent.app.account.AccountManager

/**
 * Memento-App Application — 极简初始化。
 *
 * 只做最少的全局初始化：
 * - SecurePrefs 加密存储
 * - 通知渠道注册
 * - 账户状态恢复
 */
class NodeApp : Application() {

  lateinit var runtime: NodeRuntime
    private set

  override fun onCreate() {
    super.onCreate()
    instance = this

    // 1. 加密存储
    SecurePrefs.init(this)

    // 2. 通知渠道
    NotificationReceiver().initChannels(this)

    // 3. 运行时
    runtime = NodeRuntime(this)

    // 4. 账户恢复
    val userInfo = AccountManager.getUserInfo(this)
    if (userInfo.isLoggedIn) {
      Log.i(TAG, "用户已登录: ${userInfo.username}")
    } else {
      Log.i(TAG, "用户未登录，等待扫码/输入")
    }

    Log.i(TAG, "Memento-App v${BuildConfig.VERSION_NAME} 启动完成")
  }

  companion object {
    private const val TAG = "NodeApp"
    lateinit var instance: NodeApp
      private set
  }
}