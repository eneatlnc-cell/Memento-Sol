package com.memento.sol.asset

import android.content.Context
import android.util.Log
import com.memento.sol.api.MementoApi
import com.memento.sol.api.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AssetSync(private val context: Context, private val api: MementoApi) {
  private val db: AssetDatabase by lazy {
    androidx.room.Room.databaseBuilder(context.applicationContext, AssetDatabase::class.java, "memento_assets.db").build()
  }

  sealed class SyncState {
    data object Idle : SyncState()
    data object Syncing : SyncState()
    data class Success(val assetCount: Int) : SyncState()
    data class Failed(val error: String) : SyncState()
  }

  suspend fun sync(): SyncState = withContext(Dispatchers.IO) {
    try {
      val response = api.listAssets()
      if (response.isSuccessful) {
        val assets = response.body() ?: emptyList()
        val entities = assets.map { it.toEntity() }
        db.assetDao().deleteAll()
        db.assetDao().insertAll(entities)
        SyncState.Success(entities.size)
      } else SyncState.Failed("同步失败: ${response.code()}")
    } catch (e: Exception) {
      SyncState.Failed(e.message ?: "未知错误")
    }
  }

  suspend fun getLocalAssets(): List<AssetEntity> = withContext(Dispatchers.IO) { db.assetDao().getAll() }
  suspend fun getLocalResults(): List<AssetEntity> = withContext(Dispatchers.IO) { db.assetDao().getResults() }

  companion object { private const val TAG = "AssetSync" }
}