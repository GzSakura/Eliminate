# Eliminate

**Eliminate** is a lightweight Fabric mod designed to optimize Minecraft client performance by culling invisible vertical chunks.

## Features

- **Vertical Chunk Culling**: Automatically stops rendering chunks that are vertically distant from the player (more than 32 blocks above or below), significantly reducing render load.
- **Performance Boost**: Especially effective in deep underground or high altitude scenarios.

## Usage

1.  Install [Fabric Loader](https://fabricmc.net/).
2.  Download the latest release of Eliminate.
3.  Place the `.jar` file into your `.minecraft/mods` folder.
4.  Launch the game.

### Commands

- `.debug`: Toggles the debug mode. When enabled, it displays culling statistics in the chat and logs, showing how many chunk sections are being skipped per second.

## Building from Source

Prerequisites: JDK 21

1.  Clone the repository:
    ```bash
    git clone https://github.com/GZ-Sakura/Eliminate.git
    cd Eliminate
    ```

2.  Build with Gradle:
    - Windows: `.\gradlew build`
    - Linux/macOS: `./gradlew build`

3.  The compiled artifact will be located in `build/libs/`.

## License

This project is licensed under the GNU General Public License v3.0 (GPLv3).

Permissions of this strong copyleft license are conditioned on making available complete source code of licensed works and modifications, which include larger works using a licensed work, under the same license. Copyright and license notices must be preserved. Contributors provide an express grant of patent rights.

See the [LICENSE](LICENSE) file for details.
