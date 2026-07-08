package com.myagent.app.account

import android.content.Context
import android.util.Log
import com.myagent.app.SecurePrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 账户管理器 — 与 Memento-X 共享同一套 JWT 账户体系。
 *
 * 支持：
 * - 登录/注册（JWT 认证）
 * - Token 刷新
 * - 账户信息查询
 * - 与 Memento-X 桌面端共享账户
 */
object AccountManager {

  private const val TAG = "AccountManager"
  private const val PREF_USER_ID = "user_id"
  private const val PREF_USERNAME = "username"
  private const val PREF_TOKEN = "auth_token"
  private const val PREF_REFRESH_TOKEN = "refresh_token"

  /** 当前用户信息 */
  data class UserInfo(
    val userId: String,
    val username: String,
    val isLoggedIn: Boolean,
  )

  /**
   * 登录 — 使用 JWT Token 认证。
   *
   * @param context 上下文
   * @param token JWT 访问令牌
   * @param refreshToken 刷新令牌
   * @param userId 用户 ID
   * @param username 用户名
   */
  suspend fun login(
    context: Context,
    token: String,
    refreshToken: String,
    userId: String,
    username: String,
  ): Boolean = withContext(Dispatchers.IO) {
    try {
      SecurePrefs.getInstance(context).apply {
        putString(PREF_TOKEN, token)
        putString(PREF_REFRESH_TOKEN, refreshToken)
        putString(PREF_USER_ID, userId)
        putString(PREF_USERNAME, username)
      }
      Log.i(TAG, "登录成功: $username ($userId)")
      true
    } catch (e: Exception) {
      Log.e(TAG, "登录失败: ${e.message}")
      false
    }
  }

  /**
   * 登出。
   */
  fun logout(context: Context) {
    SecurePrefs.getInstance(context).apply {
      remove(PREF_TOKEN)
      remove(PREF_REFRESH_TOKEN)
      remove(PREF_USER_ID)
      remove(PREF_USERNAME)
    }
    Log.i(TAG, "已登出")
  }

  /**
   * 获取当前用户信息。
   */
  fun getUserInfo(context: Context): UserInfo {
    val prefs = SecurePrefs.getInstance(context)
    return UserInfo(
      userId = prefs.getString(PREF_USER_ID, "") ?: "",
      username = prefs.getString(PREF_USERNAME, "") ?: "",
      isLoggedIn = !prefs.getString(PREF_TOKEN, "").isNullOrBlank(),
    )
  }

  /**
   * 获取当前 Token。
   */
  fun getToken(context: Context): String? {
    return SecurePrefs.getInstance(context).getString(PREF_TOKEN, "")
  }

  /**
   * 是否已登录。
   */
  fun isLoggedIn(context: Context): Boolean {
    return !SecurePrefs.getInstance(context).getString(PREF_TOKEN, "").isNullOrBlank()
  }
}