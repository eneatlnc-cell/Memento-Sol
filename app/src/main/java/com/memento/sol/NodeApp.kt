package com.memento.sol

import android.app.Application
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.memento.sol.account.AccountManager
import com.memento.sol.account.TokenManager
import com.memento.sol.api.MementoApi
import com.memento.sol.asset.AssetSync
import com.memento.sol.capture.CameraCapture
import com.memento.sol.capture.UploadManager
import com.memento.sol.notification.NotificationHandler
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NodeApp : Application() {

  lateinit var accountManager: AccountManager private set
  lateinit var tokenManager: TokenManager private set
  lateinit var api: MementoApi private set
  lateinit var assetSync: AssetSync private set
  lateinit var cameraCapture: CameraCapture private set
  lateinit var uploadManager: UploadManager private set

  /** 响应式登录状态，供 UI 层观察 */
  val isLoggedIn: MutableState<Boolean> = mutableStateOf(false)

  override fun onCreate() {
    super.onCreate()
    instance = this

    NotificationHandler.initChannels(this)
    tokenManager = TokenManager.create(this)

    val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    val client = OkHttpClient.Builder()
      .addInterceptor(logging)
      .connectTimeout(30, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .build()

    val retrofit = Retrofit.Builder()
      .baseUrl(CLOUD_API_BASE)
      .client(client)
      .addConverterFactory(GsonConverterFactory.create())
      .build()

    api = retrofit.create(MementoApi::class.java)
    accountManager = AccountManager(this).also { it.setApi(api) }
    assetSync = AssetSync(this, api)
    cameraCapture = CameraCapture(this)
    uploadManager = UploadManager(api)

    // 启动时恢复登录状态
    isLoggedIn.value = accountManager.isLoggedIn()

    Log.i(TAG, "Memento-Sol v4.0.0 启动完成 isLoggedIn=${isLoggedIn.value}")
  }

  companion object {
    private const val TAG = "NodeApp"
    const val CLOUD_API_BASE = "https://api.memento-x.example.com/"
    lateinit var instance: NodeApp private set
  }
}