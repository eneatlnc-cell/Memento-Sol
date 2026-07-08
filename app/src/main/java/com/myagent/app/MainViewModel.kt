package com.myagent.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.myagent.app.account.AccountManager
import com.myagent.app.asset.AssetRepository
import com.myagent.app.asset.AssetSync
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 全局 ViewModel — 管理 App 全局状态。
 *
 * 精简后职责：
 * - 账户登录状态
 * - 素材列表缓存
 * - 通知状态
 * - 主题/皮肤
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

  private val context: android.content.Context get() = getApplication()

  // ── 账户 ──
  private val _isLoggedIn = MutableStateFlow(AccountManager.isLoggedIn(context))
  val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

  private val _username = MutableStateFlow(AccountManager.getUserInfo(context).username)
  val username: StateFlow<String> = _username.asStateFlow()

  // ── 素材 ──
  private val _assets = MutableStateFlow<List<AssetRepository.Asset>>(emptyList())
  val assets: StateFlow<List<AssetRepository.Asset>> = _assets.asStateFlow()

  private val _isSyncing = MutableStateFlow(false)
  val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

  // ── 通知 ──
  private val _unreadNotifications = MutableStateFlow(0)
  val unreadNotifications: StateFlow<Int> = _unreadNotifications.asStateFlow()

  // ── 主题 ──
  private val _appearanceMode = MutableStateFlow(AppearanceThemeMode.SYSTEM)
  val appearanceMode: StateFlow<AppearanceThemeMode> = _appearanceMode.asStateFlow()

  // ── 皮肤 ──
  private val _skinMode = MutableStateFlow(SkinMode.DEFAULT)
  val skinMode: StateFlow<SkinMode> = _skinMode.asStateFlow()

  init {
    // 恢复主题设置
    val prefs = SecurePrefs.getInstance(context)
    val savedTheme = prefs.getString("theme_mode", AppearanceThemeMode.SYSTEM.name)
    _appearanceMode.value = try { AppearanceThemeMode.valueOf(savedTheme ?: "SYSTEM") } catch (_: Exception) { AppearanceThemeMode.SYSTEM }
    val savedSkin = prefs.getString("skin_mode", SkinMode.DEFAULT.name)
    _skinMode.value = try { SkinMode.valueOf(savedSkin ?: "DEFAULT") } catch (_: Exception) { SkinMode.DEFAULT }

    // 加载本地缓存素材
    loadCachedAssets()
  }

  // ── 登录 ──

  fun login(token: String, refreshToken: String, userId: String, username: String) {
    viewModelScope.launch {
      AccountManager.login(context, token, refreshToken, userId, username)
      _isLoggedIn.value = true
      _username.value = username
      syncAssets()
    }
  }

  fun logout() {
    NodeApp.instance.runtime.logout()
    _isLoggedIn.value = false
    _username.value = ""
    _assets.value = emptyList()
  }

  // ── 素材同步 ──

  fun syncAssets() {
    viewModelScope.launch {
      _isSyncing.value = true
      val token = AccountManager.getToken(context) ?: return@launch
      AssetSync.sync(context, token)
      loadCachedAssets()
      _isSyncing.value = false
    }
  }

  private suspend fun loadCachedAssets() {
    _assets.value = AssetRepository.loadAssets(context)
  }

  // ── 主题 ──

  fun setTheme(mode: AppearanceThemeMode) {
    _appearanceMode.value = mode
    SecurePrefs.getInstance(context).putString("theme_mode", mode.name)
  }

  fun setSkin(mode: SkinMode) {
    _skinMode.value = mode
    SecurePrefs.getInstance(context).putString("skin_mode", mode.name)
  }
}