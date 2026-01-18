# CMCL Launcher Script

## Problem Fixed

The original issue was that when running `java -jar cmcl.jar -l` from the `D:\Eliminate\neoforge` directory, it failed with "Error: Unable to access jarfile cmcl.jar" because the `cmcl.jar` file is located in `D:\Eliminate\cmcl.jar`, not in the `neoforge` subdirectory.

## Solution

Created `run_cmcl.bat` script in the `neoforge` directory that:
1. Automatically switches to the parent directory (`D:\Eliminate`)
2. Verifies `cmcl.jar` exists
3. Executes the CMCL command with any arguments passed to it

## Usage

From the `D:\Eliminate\neoforge` directory, you can now run:

```batch
run_cmcl.bat -l
```

To list available game versions, or:

```batch
run_cmcl.bat 1.21.1neoforge
```

To launch the 1.21.1neoforge version.

## Script Location

`D:\Eliminate\neoforge\run_cmcl.bat`
