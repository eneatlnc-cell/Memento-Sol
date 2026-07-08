# Memento-Sol 用户指南

> 当前版本：v4.0 · 定位：Memento-X 移动端伴侣

## 功能概述

Memento-Sol 是 Memento-X 桌面视频编辑工具的移动伴侣应用，主要功能：

- **灵感采集**：手机拍照/录视频，一键上传到共享素材库
- **素材浏览**：查看共享素材库中的素材（只读）
- **任务通知**：接收 Memento-X 本地调度器完成视频处理的推送通知
- **成片预览**：下载并预览已完成的视频成片
- **账户管理**：登录/管理 Memento 账户

## 与 Memento-X 的关系

```
手机端 (Memento-Sol)              桌面端 (Memento-X)
─────────────────                 ─────────────────
拍照/录视频 → 上传                  选择素材 → 说出需求
接收通知 ← 任务完成                   AI理解意图 → 调度工具
预览成片 ← 下载                     输出4K成片
```

共享：**同一套素材库 + 同一套账户体系**

## 权限说明

- **相机 (CAMERA)**：拍照/录视频采集素材
- **通知 (POST_NOTIFICATIONS)**：接收任务完成推送
- **存储**：缓存素材和成片预览

## 技术规格

| 项目 | 说明 |
|------|------|
| 应用包名 | `com.memento.sol` |
| 最低系统 | Android 8.0 (API 26) |
| 目标系统 | Android 14 (API 34) |
| 开发语言 | Kotlin + Jetpack Compose |
| 网络通信 | Retrofit + OkHttp (HTTPS) |
| 推送服务 | Firebase Cloud Messaging |

*本说明基于 Memento-Sol v4.0 编写，后续版本可能有所调整。*