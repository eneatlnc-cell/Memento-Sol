package com.myagent.app.account

import android.content.Context
import android.util.Log
import com.myagent.app.SecurePrefs

/**
 * Token 管理器 — JWT 令牌的存储、刷新与过期检测。
 *
 * 与 Memento-X 云端共享同一套 JWT 认证体系。
 * 当前为占位实现，后续接入 Ktor HTTP 客户端实现 Token 刷新。
 */
object TokenManager {

  private const val TAG = "TokenManager"
  private const val PREF_TOKEN = "auth_token"
  private const val PREF_REFRESH_TOKEN = "refresh_token"
  private const val PREF_EXPIRES_AT = "token_expires_at"

  /**
   * 存储 Token。
   */
  fun storeToken(context: Context, token: String, refreshToken: String, expiresIn: Long) {
    val expiresAt = System.currentTimeMillis() + expiresIn * 1000
    SecurePrefs.getInstance(context).apply {
      putString(PREF_TOKEN, token)
      putString(PREF_REFRESH_TOKEN, refreshToken)
      putLong(PREF_EXPIRES_AT, expiresAt)
    }
    Log.i(TAG, "Token 已存储，过期时间: $expiresAt")
  }

  /**
   * 获取有效的 Token（自动刷新过期 Token）。
   */
  suspend fun getValidToken(context: Context): String? {
    val prefs = SecurePrefs.getInstance(context)
    val token = prefs.getString(PREF_TOKEN, "") ?: return null
    val expiresAt = prefs.getLong(PREF_EXPIRES_AT, 0)

    if (System.currentTimeMillis() > expiresAt - 60_000) {
      // Token 即将过期，尝试刷新
      val refreshToken = prefs.getString(PREF_REFRESH_TOKEN, "") ?: return null
      return refreshToken(context, refreshToken)
    }
    return token
  }

  /**
   * 刷新 Token（占位，后续接入 Ktor HTTP 客户端）。
   */
  private suspend fun refreshToken(context: Context, refreshToken: String): String? {
    // TODO: 使用 Ktor 客户端调用云端 /api/v1/account/refresh 接口
    Log.i(TAG, "Token 刷新（占位）")
    return null
  }

  /**
   * 清除 Token。
   */
  fun clearToken(context: Context) {
    SecurePrefs.getInstance(context).apply {
      remove(PREF_TOKEN)
      remove(PREF_REFRESH_TOKEN)
      remove(PREF_EXPIRES_AT)
    }
  }
}