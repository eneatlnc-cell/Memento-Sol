package com.memento.sol.asset

import android.content.Context
import android.util.Log
import com.memento.sol.api.MementoApi
import com.memento.sol.api.ResultResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ResultViewer(private val context: Context, private val api: MementoApi) {
  suspend fun getResult(taskId: String): Result<ResultResponse> = withContext(Dispatchers.IO) {
    try {
      val response = api.getResult(taskId)
      if (response.isSuccessful) {
        val body = response.body() ?: return@withContext Result.failure(Exception("响应为空"))
        Result.success(body)
      } else Result.failure(Exception("获取成片失败: ${response.code()}"))
    } catch (e: Exception) { Result.failure(e) }
  }

  suspend fun downloadResult(taskId: String, onProgress: (Float) -> Unit = {}): Result<File> = withContext(Dispatchers.IO) {
    try {
      onProgress(0.1f)
      val response = api.downloadResult(taskId)
      if (response.isSuccessful) {
        val body = response.body() ?: return@withContext Result.failure(Exception("响应为空"))
        val dir = File(context.filesDir, "results")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "${taskId}.mp4")
        onProgress(0.3f)
        FileOutputStream(file).use { output -> body.byteStream().use { it.copyTo(output) } }
        onProgress(1.0f)
        Result.success(file)
      } else { onProgress(0f); Result.failure(Exception("下载失败: ${response.code()}")) }
    } catch (e: Exception) { onProgress(0f); Result.failure(e) }
  }

  companion object { private const val TAG = "ResultViewer" }
}