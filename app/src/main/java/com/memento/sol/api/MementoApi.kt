package com.memento.sol.api

import com.google.gson.annotations.SerializedName
import com.memento.sol.account.LoginRequest
import com.memento.sol.asset.AssetEntity
import com.memento.sol.capture.AssetMetadataRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface MementoApi {
  // ── 账号 ──
  @POST("/api/v1/account/login")
  suspend fun login(@Body request: LoginRequest): Response<Token>

  @POST("/api/v1/account/refresh")
  suspend fun refreshToken(@Body body: Map<String, String>): Response<Token>

  // ── 素材元数据 ──
  @POST("/api/v1/asset/metadata")
  suspend fun uploadMetadata(@Body metadata: AssetMetadataRequest): Response<AssetMetadataResponse>

  @GET("/api/v1/asset/list")
  suspend fun listAssets(): Response<List<AssetResponse>>

  // ── 通知 ──
  @POST("/api/v1/notification/register")
  suspend fun registerFcmToken(@Body body: Map<String, String>): Response<Unit>

  // ── 成片 ──
  @GET("/api/v1/workflow/result/{taskId}")
  suspend fun getResult(@Path("taskId") taskId: String): Response<ResultResponse>

  @GET("/api/v1/workflow/result/{taskId}/download")
  @Streaming
  suspend fun downloadResult(@Path("taskId") taskId: String): Response<ResponseBody>
}

// ── 数据模型 ──

data class Token(
  @SerializedName("access_token") val accessToken: String,
  @SerializedName("refresh_token") val refreshToken: String,
  @SerializedName("user_id") val userId: String,
  @SerializedName("expires_in") val expiresIn: Long,
)

data class AssetMetadataResponse(
  @SerializedName("asset_id") val assetId: String,
  @SerializedName("thumbnail_url") val thumbnailUrl: String?,
)

data class AssetResponse(
  @SerializedName("asset_id") val assetId: String,
  @SerializedName("name") val name: String,
  @SerializedName("type") val type: String,
  @SerializedName("duration") val duration: Float?,
  @SerializedName("thumbnail_url") val thumbnailUrl: String?,
  @SerializedName("status") val status: String,
  @SerializedName("is_result") val isResult: Boolean,
  @SerializedName("uploaded_at") val uploadedAt: Long,
)

data class ResultResponse(
  @SerializedName("task_id") val taskId: String,
  @SerializedName("status") val status: String,
  @SerializedName("video_url") val videoUrl: String,
  @SerializedName("thumbnail_url") val thumbnailUrl: String?,
  @SerializedName("duration") val duration: Float,
  @SerializedName("created_at") val createdAt: Long,
)

fun AssetResponse.toEntity(): AssetEntity = AssetEntity(
  id = assetId, assetId = assetId, name = name, localPath = "",
  type = type, status = status, previewUrl = thumbnailUrl,
  uploadedAt = uploadedAt, isResult = isResult,
)