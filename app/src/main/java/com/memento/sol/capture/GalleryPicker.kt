package com.memento.sol.capture

import android.util.Log

class GalleryPicker {
  private var selectedUris: List<android.net.Uri> = emptyList()
  fun getSelectedUris(): List<android.net.Uri> = selectedUris
  fun setSelectedUris(uris: List<android.net.Uri>) { selectedUris = uris }
  companion object { private const val TAG = "GalleryPicker" }
}