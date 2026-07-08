package com.myagent.app.asset

import android.content.Context
import android.util.Log
import com.myagent.app.asset.AssetRepository.Asset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 云端同步 — 从云端素材库拉取素材列表到本地缓存。
 *
 * 与 Memento-X 共享同一素材库，同步频率由后台控制。
 */
object AssetSync {

  private const val TAG = "AssetSync"

  /**
   * 同步状态。
   */
  sealed class SyncState {
    data object Idle : SyncState()
    data object Syncing : SyncState()
    data class Success(val assetCount: Int) : SyncState()
    data class Failed(val error: String) : SyncState()
  }

  /**
   * 从云端同步素材列表。
   *
   * 当前为占位实现 — 后续接入云端素材库 API。
   *
   * @param context 上下文
   * @param token JWT 认证令牌
   * @return SyncState
   */
  suspend fun sync(context: Context, token: String): SyncState = withContext(Dispatchers.IO) {
    try {
      // TODO: 替换为实际云端 API 调用
      // GET /api/v1/assets?token={token} → JSON 素材列表
      Log.i(TAG, "素材同步: token=${token.take(8)}...")

      // 占位：当前使用本地缓存
      val cached = AssetRepository.loadAssets(context)
      if (cached.isEmpty()) {
        Log.i(TAG, "素材库为空，等待首次同步")
        SyncState.Success(0)
      } else {
        Log.i(TAG, "素材已同步: ${cached.size} 项")
        SyncState.Success(cached.size)
      }
    } catch (e: Exception) {
      Log.e(TAG, "素材同步失败: ${e.message}", e)
      SyncState.Failed(error = e.message ?: "同步失败")
    }
  }
}