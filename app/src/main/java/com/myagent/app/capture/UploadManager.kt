package com.myagent.app.capture

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * 上传管理器 — 将手机采集的素材上传到共享素材库。
 *
 * 上传目标：云端素材库（与 Memento-X 共享同一素材库）。
 * 素材上传后，PC 端可在 Memento-X 中引用 asset_id 进行编辑。
 */
object UploadManager {

  private const val TAG = "UploadManager"

  /**
   * 上传状态。
   */
  sealed class UploadState {
    data object Idle : UploadState()
    data class Uploading(val progress: Float, val current: Int, val total: Int) : UploadState()
    data class Success(val assetId: String, val assetUrl: String) : UploadState()
    data class Failed(val error: String) : UploadState()
  }

  /**
   * 上传文件到素材库。
   *
   * 当前为占位实现 — 后续接入云端素材库 API。
   *
   * @param context 上下文
   * @param uri 文件 URI
   * @param fileName 文件名
   * @param onProgress 进度回调 (0.0 ~ 1.0)
   * @return UploadState
   */
  suspend fun upload(
    context: Context,
    uri: Uri,
    fileName: String,
    onProgress: (Float) -> Unit = {},
  ): UploadState = withContext(Dispatchers.IO) {
    try {
      // 占位：将文件复制到本地缓存目录
      // 未来替换为：HTTP Multipart 上传到云端素材库 API
      onProgress(0.5f)
      val cacheDir = File(context.cacheDir, "upload_cache")
      cacheDir.mkdirs()
      val dest = File(cacheDir, fileName)
      context.contentResolver.openInputStream(uri)?.use { input ->
        FileOutputStream(dest).use { output ->
          input.copyTo(output, bufferSize = 8192)
        }
      }
      onProgress(1.0f)

      // TODO: 替换为实际云端上传
      val assetId = "asset_${System.currentTimeMillis()}"
      Log.i(TAG, "素材已缓存: ${dest.absolutePath} → assetId=$assetId")

      UploadState.Success(assetId = assetId, assetUrl = dest.absolutePath)
    } catch (e: Exception) {
      Log.e(TAG, "上传失败: ${e.message}", e)
      UploadState.Failed(error = e.message ?: "未知错误")
    }
  }
}