# Build Scripts Documentation

## Overview
This directory contains build scripts for the Eliminate mod, supporting both Neoforge and Fabric mod loaders.

## Available Build Scripts

### Individual Build Scripts

#### 1. Neoforge Build Scripts
- **`build_neoforge_standalone.bat`** - Batch script to build Neoforge mod
- **`build_neoforge_standalone.ps1`** - PowerShell script to build Neoforge mod

#### 2. Fabric Build Scripts
- **`build_fabric_standalone.bat`** - Batch script to build Fabric mod
- **`build_fabric_standalone.ps1`** - PowerShell script to build Fabric mod

#### 3. Unified Build Scripts
- **`build_all.bat`** - Batch script to build both Neoforge and Fabric mods
- **`build_all.ps1`** - PowerShell script to build both Neoforge and Fabric mods

## Usage

### Building Neoforge Mod Only

**Using Batch:**
```batch
build_neoforge_standalone.bat
```

**Using PowerShell:**
```powershell
.\build_neoforge_standalone.ps1
```

### Building Fabric Mod Only

**Using Batch:**
```batch
build_fabric_standalone.bat
```

**Using PowerShell:**
```powershell
.\build_fabric_standalone.ps1
```

### Building Both Mods

**Using Batch:**
```batch
build_all.bat
```

**Using PowerShell:**
```powershell
.\build_all.ps1
```

## Build Output

All built JAR files will be placed in the `build\mods` directory.

## Requirements

- Java 21 (for Neoforge)
- Java 17 (for Fabric)
- Gradle (included via gradlew)
- Windows PowerShell or Command Prompt

## Troubleshooting

### Common Issues

1. **"Unable to access jarfile cmcl.jar"**
   - Make sure you're running scripts from the `D:\Eliminate` directory
   - The `cmcl.jar` file should be in the root directory

2. **Build fails with error**
   - Check that Java is installed and in your PATH
   - Verify that the mod directories (`neoforge` and `fabric`) exist
   - Ensure you have sufficient disk space

3. **Gradle download issues**
   - The scripts will automatically download required dependencies
   - Make sure you have an active internet connection

## Project Structure

```
D:\Eliminate\
├── neoforge\           # Neoforge mod source
│   ├── build.gradle
│   ├── gradlew.bat
│   └── src\
├── fabric\             # Fabric mod source
│   ├── build.gradle
│   ├── gradlew.bat
│   └── src\
├── build\              # Build output directory
│   └── mods\           # Built JAR files
└── build_*.ps1/bat     # Build scripts
```

## Notes

- The scripts automatically clean previous builds before building
- All scripts copy the built JARs to a centralized `build\mods` directory
- Both batch and PowerShell versions are provided for user convenience
- Scripts include error checking and user-friendly output messages
