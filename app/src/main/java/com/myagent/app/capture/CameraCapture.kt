package com.myagent.app.capture

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 相机拍摄 — 调用系统相机拍照/录视频，输出临时文件 URI。
 *
 * 返回到共享素材库的图片/视频，素材选择在 PC 端（Memento-X）完成。
 */
object CameraCapture {

  private const val FILE_PROVIDER_AUTHORITY = "com.myagent.app.fileprovider"

  /** 拍摄模式 */
  enum class Mode { PHOTO, VIDEO }

  /**
   * 创建拍照 Intent。
   *
   * @param context 上下文
   * @return Pair<Intent, File> 系统相机 Intent + 临时文件引用
   */
  fun createPhotoIntent(context: Context): Pair<Intent, File> {
    val file = createTempFile(context, "jpg")
    val uri = FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, file)
    val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE).apply {
      putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri)
      addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    }
    return Pair(intent, file)
  }

  /**
   * 创建录视频 Intent。
   */
  fun createVideoIntent(context: Context): Pair<Intent, File> {
    val file = createTempFile(context, "mp4")
    val uri = FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, file)
    val intent = Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE).apply {
      putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri)
      addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    }
    return Pair(intent, file)
  }

  private fun createTempFile(context: Context, extension: String): File {
    val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: context.cacheDir
    return File(dir, "MEMENTO_${stamp}.${extension}")
  }
}