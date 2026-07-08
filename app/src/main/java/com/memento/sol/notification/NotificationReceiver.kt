package com.memento.sol.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.memento.sol.NodeApp
import com.memento.sol.asset.AssetEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationReceiver : FirebaseMessagingService() {
  private val scope = CoroutineScope(Dispatchers.IO)

  override fun onMessageReceived(message: RemoteMessage) {
    super.onMessageReceived(message)
    val taskId = message.data["task_id"] ?: return
    val status = message.data["status"] ?: "completed"
    val videoUrl = message.data["video_url"] ?: ""

    scope.launch {
      try {
        val app = NodeApp.instance
        val entity = AssetEntity(
          id = taskId, assetId = taskId, name = "成片 - $taskId",
          localPath = videoUrl, type = "video", status = status,
          previewUrl = message.data["thumbnail_url"], uploadedAt = System.currentTimeMillis(), isResult = true,
        )
        app.assetSync.insertResult(entity)
        NotificationHandler.showTaskCompleted(this@NotificationReceiver, taskId, "视频处理完成")
      } catch (e: Exception) {
        Log.e(TAG, "处理通知失败: ${e.message}", e)
      }
    }
  }

  override fun onNewToken(token: String) {
    super.onNewToken(token)
    scope.launch {
      try {
        val app = NodeApp.instance
        app.api.registerFcmToken(mapOf("fcm_token" to token))
        Log.i(TAG, "FCM Token 已注册到云端")
      } catch (e: Exception) {
        Log.e(TAG, "FCM Token 注册失败: ${e.message}", e)
      }
    }
  }

  companion object { private const val TAG = "NotificationReceiver" }
}