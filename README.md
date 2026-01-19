# Eliminate (beta.2)

[English](./_EN.md) | 简体中文

这是一个模组的非官方移植。

**Eliminate** 是一款专为 Minecraft 高版本（1.21.x）设计的轻量级性能优化模组。它不仅仅是简单的剔除，更是通过深度介入渲染管线，智能地识别并跳过那些你看不到、但仍在消耗 GPU 资源的区块渲染，从而显著提升游戏帧率（FPS）。

## 🚀 为什么选择 Eliminate？

在原版 Minecraft 中，即便区块被地层遮挡，渲染引擎仍可能对其进行计算。Eliminate 引入了**垂直维度智能剔除**以及多项高级算法，专门解决复杂地形下的渲染冗余。

### 💎 核心优化特性

- **垂直区块剔除 (Vertical Culling)**：根据玩家当前的 Y 轴高度和地表高度图，自动剔除视野外过高或过深的区块。
- **进阶视野剔除 (FOV Culling)**：相比传统的背面剔除，我们基于玩家实时 FOV 动态计算视锥体，支持 16 格外的精确过滤，不浪费每一帧。
- **山体/地层激进剔除**：当你身处地下深处时，模组会自动识别厚重的地层遮挡，剔除地表以上的无关区块。
- **下界 (Nether) 专属适配**：智能识别下界维度，自动剔除基岩顶层以上的无效渲染，并针对开阔地形动态调整剔除容差。
- **透明度感知系统**：自动检测区块内的水、玻璃、冰等透明方块，确保优化性能的同时绝不产生视觉闪烁。
- **异步区块加载优化**：将区块解析与 NBT 读写移至后台线程，支持并发控制与邻居区块预加载，彻底告别“区块卡顿”。

### 🛠️ 技术亮点

- **高性能渲染统计**：集成 **Skia (Skija)** 引擎渲染 HUD，以极低的开销实时展示剔除统计数据。
- **极致内存管理**：核心算法使用 **fastutil** 原始类型集合，大幅减少自动装箱（Autoboxing）带来的 GC 压力。
- **无缝兼容性**：深度适配 **Sodium (钠)** 和 **Iris (光影)**。支持 `Sync with Sodium` 模式，实现零延迟的渲染同步。

## 📦 安装说明

1. 确保已安装对应版本的 [Fabric Loader](https://fabricmc.net/)。
2. 下载并安装必备依赖：[Cloth Config API](https://modrinth.com/mod/cloth-config)。
3. 将下载的 `Eliminate-1.21.x-beta.2.jar` 放入 `.minecraft/mods` 目录。
4. (可选) 安装 [Mod Menu](https://modrinth.com/mod/modmenu) 以获得可视化配置界面。

## ⚙️ 编译项目

本项目采用多版本 Gradle 架构，支持 Windows/Server 环境。

**环境要求：**
- JDK 21 或更高版本
- 建议配置 `GRADLE_USER_HOME` 到空间充足的分区（如 E 盘）

```powershell
# 构建所有受支持的版本 (1.21.10 & 1.21.11)
.\gradlew.bat build

# 仅构建特定版本
.\gradlew.bat :versions:v1.21.11:build
```

## 📜 开源协议

本项目基于 [GNU General Public License v3.0 (GPLv3)](LICENSE) 协议开源。

---
*优化渲染，不止于快。*
