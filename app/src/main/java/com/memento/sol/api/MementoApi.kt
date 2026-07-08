package com.memento.sol.api

import com.google.gson.annotations.SerializedName
import com.memento.sol.account.LoginRequest
import com.memento.sol.asset.AssetEntity
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface MementoApi {
  @POST("/api/v1/account/login")
  suspend fun login(@Body request: LoginRequest): Response<Token>

  @POST("/api/v1/account/refresh")
  suspend fun refreshToken(@Body body: Map<String, String>): Response<Token>

  @Multipart
  @POST("/api/v1/asset/upload")
  suspend fun uploadAsset(@Part file: MultipartBody.Part, @Part("type") type: String): Response<AssetUploadResponse>

  @GET("/api/v1/asset/list")
  suspend fun listAssets(): Response<List<AssetResponse>>

  @GET("/api/v1/result/{taskId}")
  suspend fun getResult(@Path("taskId") taskId: String): Response<ResultResponse>

  @GET("/api/v1/result/download/{taskId}")
  @Streaming
  suspend fun downloadResult(@Path("taskId") taskId: String): Response<ResponseBody>
}

data class Token(
  @SerializedName("access_token") val accessToken: String,
  @SerializedName("refresh_token") val refreshToken: String,
  @SerializedName("user_id") val userId: String,
  @SerializedName("expires_in") val expiresIn: Long,
)

data class AssetUploadResponse(
  @SerializedName("asset_id") val assetId: String,
  @SerializedName("url") val url: String,
  @SerializedName("thumbnail_url") val thumbnailUrl: String?,
)

data class AssetResponse(
  @SerializedName("asset_id") val assetId: String,
  @SerializedName("name") val name: String,
  @SerializedName("type") val type: String,
  @SerializedName("url") val url: String,
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
  id = assetId, assetId = assetId, name = name, localPath = url,
  type = type, status = status, previewUrl = thumbnailUrl,
  uploadedAt = uploadedAt, isResult = isResult,
)