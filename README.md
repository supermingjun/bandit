# 土匪来了 (bandit)

实时监听短信内容，按关键词规则触发分级提醒的纯本地 Android 应用，专为小米澎湃OS（HyperOS 3/4）设计。

## 功能

- **实时短信监控**：普通短信走 `SMS_RECEIVED` 广播；小米免费网络短信/RCS 通过通知监听服务（NotificationListenerService）识别
- **关键词规则**：包含 / 正则 / 通配符三种匹配方式，可限定匹配正文、发件人或两者
- **分级提醒**（每条规则独立开关，可同时开启）：
  - 弱提醒：系统通知栏通知
  - 强提醒：全屏闹钟界面 + 循环响铃（闹钟音量通道，可穿透静音）+ 震动，手动关闭才停
- **权限中心**：集中查看/跳转所有权限授权状态，包括澎湃OS 特有的自启动、后台弹出界面等
- **土匪历史**：本地保存所有命中记录
- **后台保活**：前台服务 + 开机自启 + 电池优化白名单引导
- 纯本地运行，无任何网络请求和数据上传

## 使用场景示例

收到短信"【物业通知】本栋将于今晚 23:00 停水检修，请提前储水"，配置关键词 `停水` + 强提醒，短信到达瞬间全屏响铃。

## 系统要求

- 小米澎湃OS 3（Android 16 / API 36）及以上
- minSdk 36，targetSdk 37，不兼容旧版本 Android

## 首次使用必开权限

应用内"权限中心"可逐项跳转，以下权限对强提醒至关重要：

| 权限 | 作用 |
| --- | --- |
| 接收短信 | 监听普通短信 |
| 通知使用权 | 识别免费网络短信/RCS |
| 通知权限 | 发送提醒通知 |
| 悬浮窗 | 后台直接弹出闹钟界面 |
| 全屏通知 | 锁屏时全屏弹出 |
| 勿扰权限 | 勿扰模式下仍可响铃 |
| 后台弹出界面（小米权限页内） | 澎湃OS 后台弹界面，强提醒必须开启 |
| 忽略电池优化 / 自启动 | 后台保活 |

## 技术栈

- Kotlin + Jetpack Compose（Material Design 3）
- Room 本地持久化，MVVM 架构
- AGP 9（内置 Kotlin）+ Gradle 9.8，Java 17
- compileSdk 37 / minSdk 36 / targetSdk 37

## 构建

```bash
# 调试包
gradle assembleDebug

# 发布包（需自备签名：在项目根目录放置 keystore.properties 和 jks）
gradle assembleRelease
```

`keystore.properties` 模板（已 gitignore，不会入库）：

```properties
storeFile=your-release.jks
storePassword=your-store-password
keyAlias=your-alias
keyPassword=your-key-password
```

## License

[MIT](LICENSE)
