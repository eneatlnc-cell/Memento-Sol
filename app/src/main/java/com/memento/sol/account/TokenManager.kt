package com.memento.sol.account

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class TokenManager(private val prefs: SharedPreferences) {
  fun getToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)
  fun saveToken(token: String) { prefs.edit().putString(KEY_ACCESS_TOKEN, token).apply() }
  fun clearToken() { prefs.edit().remove(KEY_ACCESS_TOKEN).apply() }

  companion object {
    private const val TAG = "TokenManager"
    private const val PREFS_NAME = "memento_token"
    private const val KEY_ACCESS_TOKEN = "access_token"
    fun create(context: Context): TokenManager {
      val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
      return TokenManager(prefs)
    }
  }
}