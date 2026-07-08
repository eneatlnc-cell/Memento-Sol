# Memento-Sol

> Memento-X 的移动端伴侣应用 — 灵感采集、素材同步、任务通知

## 定位

Memento-Sol 是 Memento-X 桌面视频编辑工具的移动伴侣：

- 与 Memento-X 共享同一套素材库和账户体系
- 手机拍照/录视频同步到共享素材库（灵感采集）
- 接收 Memento-X 本地调度器任务完成的推送通知
- 预览/下载已完成的视频成片

## 技术栈

| 组件 | 选型 |
|------|------|
| 语言 | Kotlin |
| UI | Jetpack Compose |
| 网络 | Retrofit + OkHttp |
| 图片加载 | Coil |
| 推送 | Firebase Messaging |
| 本地存储 | Room |
| 相机 | CameraX |

## 项目结构

```
Memento-Sol/
├── app/src/main/java/com/memento/sol/
│   ├── account/       # 账户管理（JWT Token）
│   ├── api/           # Memento-X API 客户端
│   ├── asset/         # 素材浏览/同步
│   ├── capture/       # 相机采集/相册选择/上传
│   ├── notification/  # 推送通知
│   ├── ui/            # 界面
│   └── legacy/        # 未来功能占位
├── fastlane/          # Play Store 发布
└── scripts/           # 工具脚本
```

## 构建

```bash
cd apps/android
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

## 版本

当前版本：4.0.0