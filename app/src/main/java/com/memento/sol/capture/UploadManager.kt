package com.memento.sol.capture

import android.content.Context
import android.net.Uri
import android.util.Log
import com.memento.sol.api.AssetUploadResponse
import com.memento.sol.api.MementoApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class UploadManager(private val api: MementoApi) {
  suspend fun uploadAsset(uri: Uri, context: Context): Result<AssetUploadResponse> = withContext(Dispatchers.IO) {
    try {
      val file = uriToFile(uri, context) ?: return@withContext Result.failure(Exception("文件转换失败"))
      val requestBody = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
      val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
      val type = if (file.extension.lowercase() in setOf("mp4", "mov", "avi")) "video" else "image"
      val response = api.uploadAsset(part, type)
      if (response.isSuccessful) {
        val body = response.body() ?: return@withContext Result.failure(Exception("响应为空"))
        Log.i(TAG, "上传成功: asset_id=${body.assetId}")
        Result.success(body)
      } else Result.failure(Exception("上传失败: ${response.code()}"))
    } catch (e: Exception) {
      Log.e(TAG, "上传异常: ${e.message}", e)
      Result.failure(e)
    }
  }

  suspend fun uploadWithProgress(uri: Uri, context: Context, onProgress: (Float) -> Unit): Result<AssetUploadResponse> =
    withContext(Dispatchers.IO) {
      onProgress(0.1f)
      val result = uploadAsset(uri, context)
      onProgress(if (result.isSuccess) 1.0f else 0f)
      result
    }

  private fun uriToFile(uri: Uri, context: Context): File? {
    return try {
      val inputStream = context.contentResolver.openInputStream(uri) ?: return null
      val ext = context.contentResolver.getType(uri)?.let { mime ->
        when { mime.contains("video") -> "mp4"; mime.contains("image") -> "jpg"; else -> "bin" }
      } ?: "bin"
      val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.$ext")
      FileOutputStream(file).use { output -> inputStream.copyTo(output) }
      inputStream.close()
      file
    } catch (e: Exception) { Log.e(TAG, "URI 转文件失败: ${e.message}"); null }
  }

  companion object { private const val TAG = "UploadManager" }
}