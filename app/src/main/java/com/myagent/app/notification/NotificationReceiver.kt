package com.myagent.app.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.myagent.app.MainActivity

/**
 * 推送接收器 — 接收 X 本地调度器任务完成的通知。
 *
 * 支持两种通知来源：
 * 1. FCM（Firebase Cloud Messaging）远程推送
 * 2. 本地定时轮询云端任务状态
 */
class NotificationReceiver : BroadcastReceiver() {

  companion object {
    const val CHANNEL_TASK = "memento_task"
    const val CHANNEL_SYSTEM = "memento_system"
    const val ACTION_TASK_COMPLETED = "com.myagent.app.ACTION_TASK_COMPLETED"
    const val ACTION_TASK_FAILED = "com.myagent.app.ACTION_TASK_FAILED"
    const val EXTRA_TASK_ID = "task_id"
    const val EXTRA_MESSAGE = "message"
  }

  override fun onReceive(context: Context, intent: Intent) {
    when (intent.action) {
      ACTION_TASK_COMPLETED -> {
        val taskId = intent.getStringExtra(EXTRA_TASK_ID) ?: "unknown"
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "视频处理完成"
        NotificationHandler.showTaskCompleted(context, taskId, message)
      }
      ACTION_TASK_FAILED -> {
        val taskId = intent.getStringExtra(EXTRA_TASK_ID) ?: "unknown"
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "任务失败"
        NotificationHandler.showTaskFailed(context, taskId, message)
      }
    }
  }

  /**
   * 初始化通知渠道（在 Application.onCreate 中调用）。
   */
  fun initChannels(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val manager = context.getSystemService(NotificationManager::class.java)
      manager.createNotificationChannel(
        NotificationChannel(
          CHANNEL_TASK,
          "任务通知",
          NotificationManager.IMPORTANCE_HIGH,
        ).apply {
          description = "Memento-X 任务完成/失败通知"
        }
      )
      manager.createNotificationChannel(
        NotificationChannel(
          CHANNEL_SYSTEM,
          "系统通知",
          NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
          description = "系统维护通知"
        }
      )
    }
  }
}