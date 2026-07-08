package com.myagent.app.asset

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * 素材本地存储 — 管理素材列表的本地缓存。
 *
 * 与 Memento-X 共享同一素材库，素材选择在 PC 端完成。
 * App 端仅提供素材浏览（只读）和灵感采集（上传）。
 */
object AssetRepository {

  private const val TAG = "AssetRepository"
  private const val CACHE_FILE = "asset_cache.json"

  /**
   * 素材数据模型。
   */
  data class Asset(
    val id: String,
    val name: String,
    val type: AssetType,
    val url: String,
    val thumbnailUrl: String? = null,
    val createdAt: Long = 0,
    val sizeBytes: Long = 0,
  )

  enum class AssetType { IMAGE, VIDEO, CHARACTER, BACKGROUND, UNKNOWN }

  /**
   * 从本地缓存加载素材列表。
   */
  suspend fun loadAssets(context: Context): List<Asset> = withContext(Dispatchers.IO) {
    try {
      val file = File(context.cacheDir, CACHE_FILE)
      if (!file.exists()) return@withContext emptyList()
      val json = JSONArray(file.readText())
      (0 until json.length()).map { i ->
        parseAsset(json.getJSONObject(i))
      }
    } catch (e: Exception) {
      Log.e(TAG, "加载素材缓存失败: ${e.message}")
      emptyList()
    }
  }

  /**
   * 保存素材列表到本地缓存。
   */
  suspend fun saveAssets(context: Context, assets: List<Asset>) = withContext(Dispatchers.IO) {
    try {
      val json = JSONArray()
      assets.forEach { asset ->
        json.put(
          JSONObject().apply {
            put("id", asset.id)
            put("name", asset.name)
            put("type", asset.type.name.lowercase())
            put("url", asset.url)
            asset.thumbnailUrl?.let { put("thumbnail_url", it) }
            put("created_at", asset.createdAt)
            put("size_bytes", asset.sizeBytes)
          }
        )
      }
      val file = File(context.cacheDir, CACHE_FILE)
      file.writeText(json.toString(2))
    } catch (e: Exception) {
      Log.e(TAG, "保存素材缓存失败: ${e.message}")
    }
  }

  private fun parseAsset(obj: JSONObject): Asset {
    val typeStr = obj.optString("type", "unknown")
    return Asset(
      id = obj.optString("id", ""),
      name = obj.optString("name", "未命名"),
      type = try { AssetType.valueOf(typeStr.uppercase()) } catch (_: Exception) { AssetType.UNKNOWN },
      url = obj.optString("url", ""),
      thumbnailUrl = obj.optString("thumbnail_url", null),
      createdAt = obj.optLong("created_at", 0),
      sizeBytes = obj.optLong("size_bytes", 0),
    )
  }
}