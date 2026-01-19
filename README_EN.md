# Eliminate (beta.3)

简体中文 | English

**Eliminate** is a lightweight, high-performance optimization mod for Minecraft 1.21.x (Fabric). Unlike basic culling mods, Eliminate deeply integrates into the rendering pipeline to intelligently identify and skip chunks that are invisible to the player but still consume GPU resources, providing a significant boost to your FPS.

## 🚀 Why Choose Eliminate?

In vanilla Minecraft, the rendering engine often processes chunks even when they are completely obstructed by terrain or are far above/below the player's view. Eliminate introduces **Intelligent Vertical Culling** and advanced algorithms to eliminate these rendering redundancies in complex environments.

### 💎 Core Optimization Features

- **Vertical Chunk Reserved (Vertical Culling)**: Automatically retains vertical chunks within the player's view range based on current Y-level and surface heightmaps, culling the rest.
- **Advanced FOV Culling**: Goes beyond simple back-face culling by dynamically calculating the view frustum based on real-time FOV. Supports custom angles (110°-180°) and features **Rotation-aware Smoothing** to prevent flickering during fast movements.
- **Intelligent Mountain Culling**: Deeply optimized for mountainous terrain. Uses Sky Visibility (`SkyVisible`) and per-chunk heightmaps to precisely cull irrelevant rendering when players are in caves or under heavy terrain, solving the mountain structure mis-culling issue.
- **Nether-Specific Adaptation**: Recognizes the Nether dimension to cull invisible chunks above the bedrock ceiling and dynamically adjusts culling tolerances for open lava seas.
- **Transparency-Aware System**: Automatically detects transparent blocks (water, glass, ice, etc.) within chunks to ensure peak performance without any visual flickering.
- **Asynchronous Chunk Loading**: Offloads chunk parsing and NBT I/O to background threads with concurrency control and neighbor preloading, effectively eliminating "chunk stutters."

### 🛠️ Technical Highlights

- **High-Performance Stats**: Utilizes the **Skia (Skija)** engine for HUD rendering, displaying real-time culling statistics (including surface height tracking) with near-zero overhead.
- **Optimized Memory Management**: Core algorithms leverage **fastutil** primitive collections to drastically reduce GC pressure caused by autoboxing.
- **Seamless Compatibility**: Built with **Sodium** and **Iris (Shaders)** in mind. Supports `Sync with Sodium` mode for zero-latency rendering updates.

## 📦 Installation

1. Ensure you have the [Fabric Loader](https://fabricmc.net/) installed for the correct Minecraft version.
2. Download and install the required dependency: [Cloth Config API](https://modrinth.com/mod/cloth-config).
3. Place the `Eliminate-1.21.x-beta.3.jar` file into your `.minecraft/mods` directory.
4. (Optional) Install [Mod Menu](https://modrinth.com/mod/modmenu) for an in-game configuration interface.

## ⚙️ Building from Source

This project uses a multi-project Gradle architecture and supports Windows/Server environments. The project is pre-configured to redirect Gradle cache to Drive E to save system disk space.

**Prerequisites:**
- JDK 21 or JDK 25
- PowerShell is recommended for executing build commands.

```powershell
# Build all supported versions (1.21.10 & 1.21.11)
.\gradlew.bat build

# Build a specific version
.\gradlew.bat :versions:v1.21.11:build
```

## 📜 License

This project is licensed under the [GNU General Public License v3.0 (GPLv3)](LICENSE).

---
*Optimizing your vision, beyond just speed.*
