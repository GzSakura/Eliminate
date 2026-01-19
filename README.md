# Eliminate (beta.3)

[English](./README_EN.md) | 简体中文

**Eliminate** 是一款专为 Minecraft 高版本（1.21.x）设计的轻量级性能优化模组。它不仅仅是简单的剔除，更是通过深度介入渲染管线，智能地识别并跳过那些你看不到、但仍在消耗 GPU 资源的区块渲染，从而显著提升游戏帧率（FPS）。

## 🚀 为什么选择 Eliminate？

在原版 Minecraft 中，即便区块被地层遮挡，渲染引擎仍可能对其进行计算。Eliminate 引入了**垂直维度智能剔除**以及多项高级算法，专门解决复杂地形下的渲染冗余。

### 💎 核心优化特性

- **垂直区块保留 (Vertical Culling)**：根据玩家当前的 Y 轴高度和地表高度图，智能保留视野范围内的垂直区块，其余部分进行剔除。
- **进阶视野剔除 (FOV Culling)**：基于玩家实时 FOV 动态计算视锥体，支持 110°-180° 自定义角度，并引入了**快速转向平滑处理**，确保高速转头时不闪烁。
- **智能遮挡剔除 (Mountain Culling)**：针对山岳地形深度优化。利用天空可见度 (`SkyVisible`) 与区块自身高度图，精准剔除玩家身处洞穴或厚重地层下方时的无关渲染，解决山岳结构误剔除问题。
- **下界 (Nether) 专属适配**：智能识别下界维度，自动剔除基岩顶层以上的无效渲染，并针对开阔地形动态调整剔除容差。
- **透明度感知系统**：自动检测区块内的水、玻璃、冰等透明方块，确保优化性能的同时绝不产生视觉闪烁。
- **异步区块加载优化**：将区块解析与 NBT 读写移至后台线程，支持并发控制与邻居区块预加载，彻底告别“区块卡顿”。

### 🛠️ 技术亮点

- **高性能渲染统计**：集成 **Skia (Skija)** 引擎渲染 HUD，以极低的开销实时展示剔除统计数据（包含地表高度实时追踪）。
- **极致内存管理**：核心算法使用 **fastutil** 原始类型集合，大幅减少自动装箱（Autoboxing）带来的 GC 压力。
- **无缝兼容性**：深度适配 **Sodium (钠)** 和 **Iris (光影)**。支持 `Sync with Sodium` 模式，实现零延迟的渲染同步。

## 📦 安装说明

1. 确保已安装对应版本的 [Fabric Loader](https://fabricmc.net/)。
2. 下载并安装必备依赖：[Cloth Config API](https://modrinth.com/mod/cloth-config)。
3. 将下载的 `Eliminate-1.21.x-beta.3.jar` 放入 `.minecraft/mods` 目录。
4. (可选) 安装 [Mod Menu](https://modrinth.com/mod/modmenu) 以获得可视化配置界面。

## ⚙️ 编译项目

本项目采用多版本 Gradle 架构，支持 Windows/Server 环境。项目已配置将缓存重定向至 E 盘以节省系统盘空间。

**环境要求：**
- JDK 21 或 JDK 25
- 建议使用 PowerShell 执行构建命令

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
