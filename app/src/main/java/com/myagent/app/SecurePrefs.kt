package com.myagent.app

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

/**
 * 偏好设置 — 加密存储 + 明文存储混合。
 *
 * 敏感数据（Token 等）使用 EncryptedSharedPreferences 加密存储。
 * 非敏感配置（主题、外观等）使用明文 SharedPreferences。
 */
class SecurePrefs(context: Context) {

  private val appContext = context.applicationContext

  private val plainPrefs: SharedPreferences =
    appContext.getSharedPreferences(PLAIN_PREFS_NAME, Context.MODE_PRIVATE)

  private val masterKey by lazy {
    MasterKey.Builder(appContext)
      .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
      .build()
  }
  private val securePrefs: SharedPreferences by lazy {
    EncryptedSharedPreferences.create(
      appContext,
      SECURE_PREFS_NAME,
      masterKey,
      EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
      EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )
  }

  // --- Instance ID ---
  private val _instanceId = MutableStateFlow(loadOrCreateInstanceId())
  val instanceId: StateFlow<String> = _instanceId

  // --- Display Name ---
  private val _displayName = MutableStateFlow(
    plainPrefs.getString(DISPLAY_NAME_KEY, null)?.trim().orEmpty()
      .ifEmpty { DeviceNames.bestDefaultNodeName(appContext) },
  )
  val displayName: StateFlow<String> = _displayName

  fun setDisplayName(value: String) {
    val trimmed = value.trim()
    plainPrefs.edit { putString(DISPLAY_NAME_KEY, trimmed) }
    _displayName.value = trimmed
  }

  // --- Appearance Theme ---
  private val _appearanceThemeMode = MutableStateFlow(
    AppearanceThemeMode.fromRawValue(plainPrefs.getString(APPEARANCE_THEME_MODE_KEY, null)),
  )
  val appearanceThemeMode: StateFlow<AppearanceThemeMode> = _appearanceThemeMode

  fun setAppearanceThemeMode(mode: AppearanceThemeMode) {
    plainPrefs.edit { putString(APPEARANCE_THEME_MODE_KEY, mode.rawValue) }
    _appearanceThemeMode.value = mode
  }

  // --- Onboarding ---
  private val _onboardingCompleted = MutableStateFlow(
    plainPrefs.getBoolean(ONBOARDING_COMPLETED_KEY, false),
  )
  val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted

  fun setOnboardingCompleted(value: Boolean) {
    plainPrefs.edit { putBoolean(ONBOARDING_COMPLETED_KEY, value) }
    _onboardingCompleted.value = value
  }

  // --- Welcome ---
  private val _welcomeCompleted = MutableStateFlow(
    plainPrefs.getBoolean(WELCOME_COMPLETED_KEY, false),
  )
  val welcomeCompleted: StateFlow<Boolean> = _welcomeCompleted

  fun setWelcomeCompleted() {
    plainPrefs.edit { putBoolean(WELCOME_COMPLETED_KEY, true) }
    _welcomeCompleted.value = true
  }

  // --- Generic Secure Storage ---

  /** 读取加密存储的值（返回 null 如果不存在） */
  fun getString(key: String): String? = securePrefs.getString(key, null)

  /** 读取加密存储的值（带默认值） */
  fun getString(key: String, default: String): String? =
    securePrefs.getString(key, default)

  /** 写入加密存储 */
  fun putString(key: String, value: String) {
    securePrefs.edit { putString(key, value) }
  }

  /** 写入加密存储（Long） */
  fun putLong(key: String, value: Long) {
    securePrefs.edit { putLong(key, value) }
  }

  /** 读取加密存储的 Long 值 */
  fun getLong(key: String, default: Long): Long =
    securePrefs.getLong(key, default)

  /** 移除加密存储的键 */
  fun remove(key: String) {
    securePrefs.edit { remove(key) }
  }

  // --- Generic Plain Storage ---

  /** 读取明文存储的值 */
  fun getPlainString(key: String, default: String? = null): String? =
    plainPrefs.getString(key, default)

  /** 写入明文存储 */
  fun putPlainString(key: String, value: String) {
    plainPrefs.edit { putString(key, value) }
  }

  private fun loadOrCreateInstanceId(): String {
    val existing = plainPrefs.getString(INSTANCE_ID_KEY, null)?.trim()
    if (!existing.isNullOrBlank()) return existing
    val fresh = UUID.randomUUID().toString()
    plainPrefs.edit { putString(INSTANCE_ID_KEY, fresh) }
    return fresh
  }

  companion object {
    private const val PLAIN_PREFS_NAME = "memento.app"
    private const val SECURE_PREFS_NAME = "memento.app.secure"

    private const val DISPLAY_NAME_KEY = "node.displayName"
    private const val APPEARANCE_THEME_MODE_KEY = "appearance.themeMode"
    private const val ONBOARDING_COMPLETED_KEY = "onboarding.completed"
    private const val WELCOME_COMPLETED_KEY = "welcome.completed"
    private const val INSTANCE_ID_KEY = "node.instanceId"

    @Volatile
    private var instance: SecurePrefs? = null

    /** 初始化单例（Application.onCreate 中调用） */
    fun init(context: Context) {
      if (instance == null) {
        synchronized(this) {
          if (instance == null) {
            instance = SecurePrefs(context)
          }
        }
      }
    }

    /** 获取单例实例 */
    fun getInstance(context: Context): SecurePrefs {
      return instance ?: synchronized(this) {
        instance ?: SecurePrefs(context).also { instance = it }
      }
    }
  }
}