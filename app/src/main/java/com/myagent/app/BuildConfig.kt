package com.myagent.app

/**
 * Memento-App 构建配置。
 *
 * 与 Memento-X 云端共享同一套配置体系。
 */
object BuildConfig {
  /** 应用版本 */
  const val VERSION_NAME = "3.2.0"
  const val VERSION_CODE = 2026070101

  /** 云端 API 基础地址 */
  var cloudApiBase: String = "https://api.memento-x.example.com"

  /** 本地素材库目录名 */
  const val ASSET_CACHE_DIR = "memento_assets"

  /** 是否启用调试日志 */
  var debug: Boolean = false
}