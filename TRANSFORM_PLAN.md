# Memento-3.1.2 → Memento-App 改造方案

**状态：已完成** ✅

## 目标定位

Memento-App 是 Memento-X 桌面视频编辑工具的**移动伴侣**：

- 与 X 共享同一套素材库和账户体系
- 手机拍照/录视频同步到共享素材库（灵感采集）
- 素材选择在 PC 端（Memento-X）完成
- 接收 X 本地调度器任务完成的推送通知
- 未来：记忆/人格/主动搭讪（占位）

## 架构对比

```
改造前 (端侧AI助手)                    改造后 (移动伴侣)
─────────────────────                ─────────────────
UI → ChatController → LlamaEngine    UI → 采集 → 上传 → 共享素材库
  → StructuredRenderer → 视频输出       → 素材浏览（只读）
  → MemoryManager（本地记忆）            → 通知接收
  → ProactiveTrigger（主动搭话）          → 账户管理
                                       → 未来：记忆/人格/搭讪（占位）
```

## 改造范围

### 移除（~35 个文件/目录）
- `model/` 目录（LlamaEngine, LlamaNative, LocalModelLoader, ModelInstaller, ModelDownloadState, DownloadForegroundService, PersonaManager, Acs3Signer, PresignUrlProvider, DeviceCapability）
- `chat/` 目录（ChatController, ChatModels）
- `multimodal/` 目录（MultiModalDispatcher, StructuredRenderer, VideoFrameExtractor, KeyFrameStore, BitmapToVideoEncoder, VideoConfig）
- `memory/` 目录（MemoryEntity, MemoryDao, MemoryDatabase, MemoryManager）
- `proactive/ProactiveTrigger.kt`
- `activation/` 目录（ActivationManager, AuthApi）
- `ui/chat/` 目录（ChatScreen, ChatInputBar, MessageBubble）
- `ui/` 旧页面（ModelDownloadScreen, ActivationScreen, OnboardingFlow, WelcomeScreen, LingjiSplashScreen, SettingsScreens）
- `cpp/` 目录（JNI/OpenCL stub）
- `jniLibs/` 目录（预编译 .so）
- `benchmark/` 目录

### 新建（14 个文件）

| 模块 | 文件 | 职责 |
|------|------|------|
| 采集 | `capture/CameraCapture.kt` | 拍照/录视频 Intent 创建 |
| 采集 | `capture/GalleryPicker.kt` | 相册选择器 |
| 采集 | `capture/UploadManager.kt` | 上传至共享素材库 |
| 素材 | `asset/AssetRepository.kt` | 共享素材库本地缓存 |
| 素材 | `asset/AssetSync.kt` | 素材同步 |
| 素材 | `asset/AssetList.kt` | 素材列表 UI |
| 通知 | `notification/NotificationReceiver.kt` | 任务完成通知 BroadcastReceiver |
| 通知 | `notification/NotificationHandler.kt` | 本地通知构建 |
| 账户 | `account/AccountManager.kt` | JWT 账户管理 |
| 账户 | `account/TokenManager.kt` | Token 存储/刷新 |
| 未来 | `future/MemoryStorage.kt` | 记忆存储占位 |
| 未来 | `future/PersonaStorage.kt` | 人格存储占位 |
| 未来 | `future/ProactiveReceiver.kt` | 主动搭讪占位 |

### 重写（4 个核心文件）

| 文件 | 变更 |
|------|------|
| `BuildConfig.kt` | 新版本号 3.2.0，云端 API 配置 |
| `NodeApp.kt` | 移除模型初始化，仅 SecurePrefs + 通知渠道 + 账户恢复 |
| `NodeRuntime.kt` | 移除 JNI 引擎，改为账户/素材同步/通知管理 |
| `MainViewModel.kt` | 移除聊天/下载/激活状态，改为账户/素材/通知/主题状态 |

### UI 重写（3 个文件）

| 文件 | 变更 |
|------|------|
| `MainActivity.kt` | 简化 setContent，仅主题 + RootScreen |
| `RootScreen.kt` | 移除 NavHost 六路由，改为登录/主界面二选一 |
| `ShellScreen.kt` | 底部导航：素材/采集/设置 |

### 构建配置更新

| 文件 | 变更 |
|------|------|
| `build.gradle.kts` | 移除 Room/ksp, commonmark, coil, navigation-compose, litertlm, ndk/cmake, jniLibs |
| `libs.versions.toml` | 清除未使用的依赖版本声明 |
| `AndroidManifest.xml` | 移除 DownloadForegroundService, uses-native-library, 添加 NotificationReceiver |
| `strings.xml` | 替换聊天/激活/下载字符串为素材/采集/通知/账户字符串 |

## 保留的设计系统

- `ui/design/ClawTheme.kt` — Claw 设计令牌
- `ui/design/ClawNavigation.kt` — ClawBottomNav 底部导航
- `ui/design/ClawComponents.kt` — 通用组件
- `ui/design/ClawSurfaces.kt` — 表面组件
- `ui/design/ClawPreview.kt` — 预览工具
- `ui/OpenClawTheme.kt` — MementoTheme 统一主题
- `ui/MobileUiTokens.kt` — 移动端令牌
- `AppearanceThemeMode.kt` — 主题模式
- `SkinMode.kt` — 皮肤模式
- `SecurePrefs.kt` — 加密存储