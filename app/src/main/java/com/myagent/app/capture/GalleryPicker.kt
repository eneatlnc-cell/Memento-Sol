package com.myagent.app.capture

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts

/**
 * 相册选择 — 从系统相册选择图片或视频。
 *
 * 只负责拉起系统选择器，素材选择由 PC 端完成。
 */
object GalleryPicker {

  /**
   * 创建图片选择 Intent（单选）。
   */
  fun createImagePickerIntent(): Intent {
    return Intent(Intent.ACTION_PICK).apply {
      type = "image/*"
    }
  }

  /**
   * 创建视频选择 Intent（单选）。
   */
  fun createVideoPickerIntent(): Intent {
    return Intent(Intent.ACTION_PICK).apply {
      type = "video/*"
    }
  }

  /**
   * 创建多图片选择 launcher contract。
   */
  fun multiImagePicker() = ActivityResultContracts.PickVisualMedia()

  /**
   * 创建多视频选择 launcher contract。
   */
  fun multiVideoPicker() = ActivityResultContracts.PickVisualMedia()
}