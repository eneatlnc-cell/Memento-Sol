package com.myagent.app.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.myagent.app.MainActivity
import com.myagent.app.R

/**
 * 通知处理器 — 显示本地任务完成/失败通知。
 */
object NotificationHandler {

  private var notificationId = 1000

  /**
   * 显示"任务完成"通知。
   */
  fun showTaskCompleted(context: Context, taskId: String, message: String) {
    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
      putExtra(NotificationReceiver.EXTRA_TASK_ID, taskId)
    }
    val pendingIntent = PendingIntent.getActivity(
      context, taskId.hashCode(), intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    val notification = NotificationCompat.Builder(context, NotificationReceiver.CHANNEL_TASK)
      .setSmallIcon(R.drawable.myagent_logo)
      .setContentTitle("视频处理完成")
      .setContentText(message)
      .setAutoCancel(true)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setContentIntent(pendingIntent)
      .build()

    val manager = context.getSystemService(NotificationManager::class.java)
    manager.notify(notificationId++, notification)
  }

  /**
   * 显示"任务失败"通知。
   */
  fun showTaskFailed(context: Context, taskId: String, error: String) {
    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val pendingIntent = PendingIntent.getActivity(
      context, taskId.hashCode(), intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    val notification = NotificationCompat.Builder(context, NotificationReceiver.CHANNEL_TASK)
      .setSmallIcon(R.drawable.myagent_logo)
      .setContentTitle("任务失败")
      .setContentText(error)
      .setAutoCancel(true)
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setContentIntent(pendingIntent)
      .build()

    val manager = context.getSystemService(NotificationManager::class.java)
    manager.notify(notificationId++, notification)
  }
}