# MineLZU Mod

Minecraft 26.3 的建筑方块模组，Kotlin 编写，用 Architectury API 同时支持
**NeoForge 26.3.0.95** 与 **Fabric**（Loader 0.19.5 / Fabric API 0.165.2+26.3）。

方块状态、方块模型、物品模型、方块标签等 JSON 全部由 datagen 生成，
`common/src/generated/resources` 为生成产物；语言文件与纹理是手写资源。

## 目录

| 路径 | 内容 |
| --- | --- |
| `common/` | 全部游戏逻辑、注册表、资源、datagen provider |
| `fabric/` | Fabric 入口与 `fabric.mod.json` |
| `neoforge/` | NeoForge 入口、datagen 触发、`neoforge.mods.toml` |
| `docs/` | 开发文档 |
| `common/src/generated/resources/` | datagen 产物，不要手改 |

## 常用命令

```powershell
.\gradlew.bat build                 # 构建两个平台的 jar
.\gradlew.bat :neoforge:runData     # 重新生成 common/src/generated/resources
.\gradlew.bat :neoforge:runClient
.\gradlew.bat :fabric:runClient
.\gradlew.bat :neoforge:runServer
```

产物：`neoforge/build/libs/minelzu-neoforge-26.3+1.0.0.jar`、
`fabric/build/libs/minelzu-fabric-26.3+1.0.0.jar`。

## 文档

| 文档 | 内容 |
| --- | --- |
| [docs/开发与构建.md](docs/开发与构建.md) | 环境版本、命令、构建脚本要点、常见错误 |
| [docs/多平台架构.md](docs/多平台架构.md) | common/fabric/neoforge 的划分、注册流程、loom 配置 |
| [docs/资源数据生成.md](docs/资源数据生成.md) | datagen 结构、元素模型 DSL、新增方块步骤 |
| [docs/方块清单.md](docs/方块清单.md) | 23 个方块、碰撞箱与标签 |
| [docs/移植说明.md](docs/移植说明.md) | 从 Fabric 1.21.11 移植的映射与差异 |
