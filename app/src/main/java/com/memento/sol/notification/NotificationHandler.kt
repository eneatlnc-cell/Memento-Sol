package com.memento.sol.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.memento.sol.MainActivity
import java.util.concurrent.atomic.AtomicInteger

object NotificationHandler {
  private const val CHANNEL_TASK = "memento_task"
  private val notificationId = AtomicInteger(1000)

  fun initChannels(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val manager = context.getSystemService(NotificationManager::class.java)
      manager.createNotificationChannel(
        NotificationChannel(CHANNEL_TASK, "任务通知", NotificationManager.IMPORTANCE_HIGH).apply {
          description = "Memento-X 任务完成/失败通知"
        }
      )
    }
  }

  fun showTaskCompleted(context: Context, taskId: String, message: String) {
    val intent = Intent(context, MainActivity::class.java).apply {
      putExtra("task_id", taskId)
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val pendingIntent = PendingIntent.getActivity(
      context, taskId.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val notification = NotificationCompat.Builder(context, CHANNEL_TASK)
      .setSmallIcon(android.R.drawable.ic_dialog_info)
      .setContentTitle("视频处理完成")
      .setContentText(message)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setContentIntent(pendingIntent)
      .setAutoCancel(true)
      .build()
    context.getSystemService(NotificationManager::class.java).notify(notificationId.incrementAndGet(), notification)
  }

  fun showTaskFailed(context: Context, taskId: String, message: String) {
    val intent = Intent(context, MainActivity::class.java).apply {
      putExtra("task_id", taskId)
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val pendingIntent = PendingIntent.getActivity(
      context, taskId.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val notification = NotificationCompat.Builder(context, CHANNEL_TASK)
      .setSmallIcon(android.R.drawable.ic_dialog_alert)
      .setContentTitle("任务失败")
      .setContentText(message)
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setContentIntent(pendingIntent)
      .setAutoCancel(true)
      .build()
    context.getSystemService(NotificationManager::class.java).notify(notificationId.incrementAndGet(), notification)
  }
}