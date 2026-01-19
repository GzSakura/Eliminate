:: Define paths
set basePath=D:\Eliminate
set buildPath=%basePath%\build
set modsPath=%buildPath%\mods

:: Create directory structure
if not exist "%buildPath%" mkdir "%buildPath%"
if not exist "%modsPath%" mkdir "%modsPath%"
echo Created build/mods directory structure

:: Process v1.21.1-fabric
echo Processing version: v1.21.1-fabric
set versionDir=%basePath%\versions\v1.21.1-fabric
cd "%versionDir%"
echo Running Gradle build for v1.21.1-fabric...
call "%basePath%\gradlew" build --no-daemon -x test
echo Built version: v1.21.1-fabric
:: Find the generated jar file and copy it to modsPath
echo Finding generated jar files...
for /r "%versionDir%\build\libs" %%f in (*.jar) do (
    if not "%%~nf" == "%%~nf-sources" (
        set jarPath=%%f
        set jarFileName=Eliminate-v1.21.1-fabric.jar
        echo Copying %%f to %modsPath%\%jarFileName%
        copy "%%f" "%modsPath%\%jarFileName%" /y
        echo Copied jar file: %jarFileName%
        goto nextVersion1
    )
)
:nextVersion1

:: Process v1.21.10-fabric
echo Processing version: v1.21.10-fabric
set versionDir=%basePath%\versions\v1.21.10-fabric
cd "%versionDir%"
echo Running Gradle build for v1.21.10-fabric...
call "%basePath%\gradlew" build --no-daemon -x test
echo Built version: v1.21.10-fabric
:: Find the generated jar file and copy it to modsPath
echo Finding generated jar files...
for /r "%versionDir%\build\libs" %%f in (*.jar) do (
    if not "%%~nf" == "%%~nf-sources" (
        set jarPath=%%f
        set jarFileName=Eliminate-v1.21.10-fabric.jar
        echo Copying %%f to %modsPath%\%jarFileName%
        copy "%%f" "%modsPath%\%jarFileName%" /y
        echo Copied jar file: %jarFileName%
        goto nextVersion10
    )
)
:nextVersion10

:: Process v1.21.11-fabric
echo Processing version: v1.21.11-fabric
set versionDir=%basePath%\versions\v1.21.11-fabric
cd "%versionDir%"
echo Running Gradle build for v1.21.11-fabric...
call "%basePath%\gradlew" build --no-daemon -x test
echo Built version: v1.21.11-fabric
:: Find the generated jar file and copy it to modsPath
echo Finding generated jar files...
for /r "%versionDir%\build\libs" %%f in (*.jar) do (
    if not "%%~nf" == "%%~nf-sources" (
        set jarPath=%%f
        set jarFileName=Eliminate-v1.21.11-fabric.jar
        echo Copying %%f to %modsPath%\%jarFileName%
        copy "%%f" "%modsPath%\%jarFileName%" /y
        echo Copied jar file: %jarFileName%
        goto nextVersion11
    )
)
:nextVersion11

:: Process v1.21.5-fabric
echo Processing version: v1.21.5-fabric
set versionDir=%basePath%\versions\v1.21.5-fabric
cd "%versionDir%"
echo Running Gradle build for v1.21.5-fabric...
call "%basePath%\gradlew" build --no-daemon -x test
echo Built version: v1.21.5-fabric
:: Find the generated jar file and copy it to modsPath
echo Finding generated jar files...
for /r "%versionDir%\build\libs" %%f in (*.jar) do (
    if not "%%~nf" == "%%~nf-sources" (
        set jarPath=%%f
        set jarFileName=Eliminate-v1.21.5-fabric.jar
        echo Copying %%f to %modsPath%\%jarFileName%
        copy "%%f" "%modsPath%\%jarFileName%" /y
        echo Copied jar file: %jarFileName%
        goto nextVersion5
    )
)
:nextVersion5

:: Check build results
echo Checking build results...
if exist "%modsPath%" (
    echo build/mods directory exists
    dir "%modsPath%\*.jar"
    echo Build successful!
) else (
    echo build/mods directory does not exist, please check the build process.
)

cd "%basePath%"
pause
