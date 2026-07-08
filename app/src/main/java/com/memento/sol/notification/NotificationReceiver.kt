package com.memento.sol.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
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
    val thumbnailUrl = message.data["thumbnail_url"]

    scope.launch {
      try {
        val entity = AssetEntity(
          id = taskId, assetId = taskId, name = "成片 - $taskId",
          localPath = videoUrl, type = "video", status = status,
          previewUrl = thumbnailUrl, uploadedAt = System.currentTimeMillis(), isResult = true,
        )
        kotlinx.coroutines.runBlocking {
          androidx.room.Room.databaseBuilder(
            applicationContext, com.memento.sol.asset.AssetDatabase::class.java, "memento_assets.db"
          ).build().assetDao().insert(entity)
        }
        NotificationHandler.showTaskCompleted(this@NotificationReceiver, taskId, "视频处理完成")
      } catch (e: Exception) {
        Log.e(TAG, "处理通知失败: ${e.message}", e)
      }
    }
  }

  override fun onNewToken(token: String) {
    super.onNewToken(token)
    Log.i(TAG, "FCM Token 已刷新")
  }

  companion object { private const val TAG = "NotificationReceiver" }
}