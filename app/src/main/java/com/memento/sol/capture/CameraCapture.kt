package com.memento.sol.capture

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraCapture(private val context: Context) {
  private var currentPhotoUri: Uri? = null
  private var currentVideoUri: Uri? = null

  fun createPhotoIntent(): Pair<android.content.Intent, Uri> {
    val file = createMediaFile("jpg")
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    currentPhotoUri = uri
    val intent = android.content.Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE).apply {
      putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri)
      addFlags(android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    }
    return intent to uri
  }

  fun createVideoIntent(): Pair<android.content.Intent, Uri> {
    val file = createMediaFile("mp4")
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    currentVideoUri = uri
    val intent = android.content.Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE).apply {
      putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri)
      addFlags(android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    }
    return intent to uri
  }

  fun getCurrentPhotoUri(): Uri? = currentPhotoUri
  fun getCurrentVideoUri(): Uri? = currentVideoUri

  private fun createMediaFile(extension: String): File {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val dir = File(context.cacheDir, "capture")
    if (!dir.exists()) dir.mkdirs()
    return File(dir, "memento_${timestamp}.${extension}")
  }

  companion object { private const val TAG = "CameraCapture" }
}