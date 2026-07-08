package com.memento.sol.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.memento.sol.capture.CameraCapture
import com.memento.sol.capture.UploadManager
import kotlinx.coroutines.launch

@Composable
fun CaptureScreen(
  cameraCapture: CameraCapture,
  uploadManager: UploadManager,
  onUploadSuccess: () -> Unit = {},
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  var isUploading by remember { mutableStateOf(false) }
  var uploadMessage by remember { mutableStateOf<String?>(null) }

  val photoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
    if (success) cameraCapture.getCurrentPhotoUri()?.let { uri ->
      scope.launch { isUploading = true; uploadMessage = "上传中..."; val r = uploadManager.uploadAsset(uri, context); isUploading = false; uploadMessage = if (r.isSuccess) "上传成功" else "上传失败"; if (r.isSuccess) onUploadSuccess() }
    }
  }
  val videoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CaptureVideo()) { success ->
    if (success) cameraCapture.getCurrentVideoUri()?.let { uri ->
      scope.launch { isUploading = true; uploadMessage = "上传中..."; val r = uploadManager.uploadAsset(uri, context); isUploading = false; uploadMessage = if (r.isSuccess) "上传成功" else "上传失败"; if (r.isSuccess) onUploadSuccess() }
    }
  }
  val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
    uri?.let {
      scope.launch { isUploading = true; uploadMessage = "上传中..."; val r = uploadManager.uploadAsset(it, context); isUploading = false; uploadMessage = if (r.isSuccess) "上传成功" else "上传失败"; if (r.isSuccess) onUploadSuccess() }
    }
  }

  Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
    Text("素材采集", style = MaterialTheme.typography.headlineMedium)
    Spacer(Modifier.height(32.dp))
    Button(onClick = { val (intent, _) = cameraCapture.createPhotoIntent(); photoLauncher.launch(intent) }, modifier = Modifier.fillMaxWidth().height(56.dp), enabled = !isUploading) { Icon(Icons.Outlined.CameraAlt, null); Spacer(Modifier.width(8.dp)); Text("拍照") }
    Spacer(Modifier.height(16.dp))
    Button(onClick = { val (intent, _) = cameraCapture.createVideoIntent(); videoLauncher.launch(intent) }, modifier = Modifier.fillMaxWidth().height(56.dp), enabled = !isUploading) { Icon(Icons.Outlined.Videocam, null); Spacer(Modifier.width(8.dp)); Text("录视频") }
    Spacer(Modifier.height(16.dp))
    Button(onClick = { galleryLauncher.launch("image/*") }, modifier = Modifier.fillMaxWidth().height(56.dp), enabled = !isUploading) { Icon(Icons.Outlined.PhotoLibrary, null); Spacer(Modifier.width(8.dp)); Text("从相册选择") }
    if (isUploading) { Spacer(Modifier.height(24.dp)); CircularProgressIndicator() }
    uploadMessage?.let { Spacer(Modifier.height(16.dp)); Text(it, style = MaterialTheme.typography.bodyLarge, color = if (it.contains("成功")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error) }
  }
}