package com.memento.sol.asset

import androidx.room.*

@Database(entities = [AssetEntity::class], version = 1, exportSchema = false)
abstract class AssetDatabase : RoomDatabase() {
  abstract fun assetDao(): AssetDao
}

@Entity(tableName = "assets")
data class AssetEntity(
  @PrimaryKey val id: String,
  @ColumnInfo(name = "asset_id") val assetId: String,
  @ColumnInfo(name = "name") val name: String,
  @ColumnInfo(name = "local_path") val localPath: String,
  @ColumnInfo(name = "type") val type: String,
  @ColumnInfo(name = "status") val status: String,
  @ColumnInfo(name = "preview_url") val previewUrl: String?,
  @ColumnInfo(name = "uploaded_at") val uploadedAt: Long,
  @ColumnInfo(name = "is_result") val isResult: Boolean = false,
)

@Dao
interface AssetDao {
  @Query("SELECT * FROM assets ORDER BY uploaded_at DESC")
  suspend fun getAll(): List<AssetEntity>

  @Query("SELECT * FROM assets WHERE is_result = 1 ORDER BY uploaded_at DESC")
  suspend fun getResults(): List<AssetEntity>

  @Query("SELECT * FROM assets WHERE asset_id = :assetId LIMIT 1")
  suspend fun getByAssetId(assetId: String): AssetEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(assets: List<AssetEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(asset: AssetEntity)

  @Query("DELETE FROM assets WHERE asset_id = :assetId")
  suspend fun deleteByAssetId(assetId: String)

  @Query("DELETE FROM assets")
  suspend fun deleteAll()
}