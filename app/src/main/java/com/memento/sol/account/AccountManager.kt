package com.memento.sol.account

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.memento.sol.api.MementoApi
import com.memento.sol.api.Token
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AccountManager(private val context: Context) {
  private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
  private val gson = Gson()
  private var api: MementoApi? = null

  fun setApi(api: MementoApi) { this.api = api }

  suspend fun login(phone: String, code: String): Result<Token> = withContext(Dispatchers.IO) {
    try {
      val api = this@AccountManager.api ?: return@withContext Result.failure(Exception("API 未初始化"))
      val response = api.login(LoginRequest(phone, code))
      if (response.isSuccessful) {
        val token = response.body() ?: return@withContext Result.failure(Exception("Token 为空"))
        saveToken(token)
        Log.i(TAG, "登录成功: ${token.userId}")
        Result.success(token)
      } else Result.failure(Exception("登录失败: ${response.code()}"))
    } catch (e: Exception) {
      Log.e(TAG, "登录异常: ${e.message}", e)
      Result.failure(e)
    }
  }

  suspend fun refreshToken(): Result<Token> = withContext(Dispatchers.IO) {
    try {
      val api = this@AccountManager.api ?: return@withContext Result.failure(Exception("API 未初始化"))
      val currentToken = getToken() ?: return@withContext Result.failure(Exception("无 Token"))
      val response = api.refreshToken(currentToken.refreshToken)
      if (response.isSuccessful) {
        val token = response.body() ?: return@withContext Result.failure(Exception("Token 为空"))
        saveToken(token)
        Result.success(token)
      } else Result.failure(Exception("刷新失败: ${response.code()}"))
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  fun isLoggedIn(): Boolean = getToken() != null
  fun logout() { prefs.edit().clear().apply() }

  fun getToken(): Token? {
    val json = prefs.getString(KEY_TOKEN, null) ?: return null
    return try { gson.fromJson(json, Token::class.java) } catch (_: Exception) { null }
  }
  private fun saveToken(token: Token) { prefs.edit().putString(KEY_TOKEN, gson.toJson(token)).apply() }

  companion object {
    private const val TAG = "AccountManager"
    private const val PREFS_NAME = "memento_account"
    private const val KEY_TOKEN = "token"
  }
}

data class LoginRequest(val phone: String, val code: String)