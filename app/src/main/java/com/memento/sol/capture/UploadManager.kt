package com.memento.sol.capture

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.memento.sol.api.AssetMetadataResponse
import com.memento.sol.api.MementoApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * 素材元数据上传管理器。
 *
 * 架构约束：素材文件保留在手机本地，仅上传元数据（文件名、类型、时长、缩略图）到云端。
 * 云端返回 asset_id，手机端保存。
 */
class UploadManager(private val api: MementoApi) {

  suspend fun uploadAsset(uri: Uri, context: Context): Result<AssetMetadataResponse> = withContext(Dispatchers.IO) {
    try {
      // 1. 素材持久化到本地 filesDir
      val localFile = persistToLocal(uri, context) ?: return@withContext Result.failure(Exception("文件保存失败"))

      // 2. 提取元数据
      val metadata = extractMetadata(localFile, uri, context)

      // 3. 仅上传元数据到云端
      val response = api.uploadMetadata(metadata)
      if (response.isSuccessful) {
        val body = response.body() ?: return@withContext Result.failure(Exception("响应为空"))
        Log.i(TAG, "元数据上传成功: asset_id=${body.assetId}")
        Result.success(body)
      } else Result.failure(Exception("上传失败: ${response.code()}"))
    } catch (e: Exception) {
      Log.e(TAG, "上传异常: ${e.message}", e)
      Result.failure(e)
    }
  }

  suspend fun uploadWithProgress(uri: Uri, context: Context, onProgress: (Float) -> Unit): Result<AssetMetadataResponse> =
    withContext(Dispatchers.IO) {
      onProgress(0.3f)
      val result = uploadAsset(uri, context)
      onProgress(if (result.isSuccess) 1.0f else 0f)
      result
    }

  /** 持久化素材到本地 filesDir，返回持久化后的文件 */
  private fun persistToLocal(uri: Uri, context: Context): File? {
    return try {
      val inputStream = context.contentResolver.openInputStream(uri) ?: return null
      val ext = context.contentResolver.getType(uri)?.let { mime ->
        when { mime.contains("video") -> "mp4"; mime.contains("image") -> "jpg"; else -> "bin" }
      } ?: "bin"
      val dir = File(context.filesDir, "captures")
      if (!dir.exists()) dir.mkdirs()
      val file = File(dir, "memento_${System.currentTimeMillis()}.${ext}")
      FileOutputStream(file).use { output -> inputStream.copyTo(output) }
      inputStream.close()
      Log.i(TAG, "素材已持久化: ${file.absolutePath}")
      file
    } catch (e: Exception) { Log.e(TAG, "持久化失败: ${e.message}"); null }
  }

  /** 提取素材元数据 */
  private fun extractMetadata(file: File, uri: Uri, context: Context): AssetMetadataRequest {
    val name = file.name
    val type = if (file.extension.lowercase() in setOf("mp4", "mov", "avi")) "video" else "image"
    val sizeBytes = file.length()
    val duration = if (type == "video") getVideoDuration(uri, context) else null
    return AssetMetadataRequest(
      name = name,
      type = type,
      sizeBytes = sizeBytes,
      duration = duration,
      localPath = file.absolutePath,
    )
  }

  /** 获取视频时长（秒） */
  private fun getVideoDuration(uri: Uri, context: Context): Float? {
    return try {
      val cursor = context.contentResolver.query(uri, arrayOf(MediaStore.Video.Media.DURATION), null, null, null)
      cursor?.use {
        if (it.moveToFirst()) {
          val durationMs = it.getLong(it.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
          durationMs / 1000f
        } else null
      }
    } catch (e: Exception) { null }
  }

  companion object { private const val TAG = "UploadManager" }
}

/** 元数据上传请求体 */
data class AssetMetadataRequest(
  val name: String,
  val type: String,
  val sizeBytes: Long,
  val duration: Float?,
  val localPath: String,
)