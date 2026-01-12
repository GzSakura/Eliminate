# Eliminate

**Eliminate** is a lightweight Fabric mod designed to optimize rendering performance by culling unnecessary vertical chunks outside the player's field of view.

## Core Features

- **Vertical Chunk Culling**: Automatically filters the rendering of chunks beyond a certain distance above or below the player, significantly reducing GPU load.
- **High-Performance HUD**: Uses the Skia engine to render real-time statistics (culling counts, surface height, etc.).
- **Configuration System**: Integrated with Cloth Config and Mod Menu, supporting in-game customization of culling distance, update frequency, and more.
- **Broad Compatibility**: Fully compatible with Sodium and Iris (Shaders).

## Quick Start

1. Install [Fabric Loader](https://fabricmc.net/).
2. Install dependencies: [Cloth Config API](https://modrinth.com/mod/cloth-config).
3. Download the latest version of Eliminate (beta2).
4. Place the `.jar` file into your `.minecraft/mods` folder.

## Supported Versions

- Minecraft 1.21.10
- Minecraft 1.21.11

## Building the Project

Prerequisites: JDK 21

```bash
# Build all versions
.\gradlew.bat build

# Build only version 1.21.11
.\gradlew.bat :versions:v1.21.11:build
```

## License

This project is licensed under the [GNU General Public License v3.0 (GPLv3)](LICENSE).
