# Eliminate

[English](./README_EN.md) | 简体中文

**Eliminate** 是一个轻量级的 Fabric 模组，旨在通过剔除玩家视野外不必要的垂直区块来优化渲染性能。

## 核心功能

- **垂直区块剔除 (Vertical Culling)**: 自动过滤玩家上方或下方一定距离以外的区块渲染，显著降低显卡负担。
- **高性能 HUD**: 使用 Skia 引擎渲染实时统计数据（剔除数量、地表高度等）。
- **配置系统**: 集成 Cloth Config 和 Mod Menu，支持在游戏内自定义剔除距离、更新频率等。
- **广泛兼容**: 完美兼容 Sodium (钠) 和 Iris (光影)。

## 快速开始

1. 安装 [Fabric Loader](https://fabricmc.net/)。
2. 安装依赖：[Cloth Config API](https://modrinth.com/mod/cloth-config)。
3. 下载最新版本的 Eliminate (beta2)。
4. 将 `.jar` 文件放入 `.minecraft/mods` 文件夹中。

## 支持版本

- Minecraft 1.21.10
- Minecraft 1.21.11

## 编译项目

环境要求: JDK 21

```bash
# 构建所有版本
.\gradlew.bat build

# 仅构建 1.21.11 版本
.\gradlew.bat :versions:v1.21.11:build
```

## 开源协议

本项目采用 [GNU General Public License v3.0 (GPLv3)](LICENSE) 协议。
