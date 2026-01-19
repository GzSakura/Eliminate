package dev.miitong.eliminate.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.neoforged.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class EliminateConfig {
    private static final File CONFIG_FILE = new File(FMLPaths.CONFIGDIR.get().toFile(), "eliminate.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public boolean enabled = true;
    public int cullingDistance = 32;
    public boolean debugMode = false;
    public int updateSpeed = 20;
    public boolean syncWithSodium = false;
    public boolean fovCullingEnabled = true;
    public boolean aggressiveMountainCulling = true;
    public boolean transparencyAwareness = true;
    public boolean endDimensionOptimization = true;
    public boolean autoOptimize = false;
    public int threadPoolSize = -1;
    public boolean preCalculateChunks = true;
    public int preCalculateRadius = 8;
    public boolean horizontalCulling = true;
    public boolean biomeAwareCulling = true;
    public boolean dynamicCullingDistance = false;
    public int targetFps = 60;
    public int minDynamicDistance = 16;
    public int maxDynamicDistance = 64;

    public boolean verticalCullingEnabled = true;
    public int verticalCullingDistance = 32;
    public boolean verticalCullingUndergroundOnly = false;
    public int verticalCullingSurfaceMargin = 8;

    public boolean backfaceCullingEnabled = false;
    public boolean backfaceCullingAggressive = false;
    public int backfaceCullingDistance = 64;

    public boolean entityCullingEnabled = false;
    public int entityCullingDistance = 128;
    public boolean entityCullingIncludeItems = true;
    public boolean entityCullingIncludeParticles = false;

    public boolean fogCullingEnabled = true;
    public int fogCullingDistance = 96;
    public boolean fogCullingAdaptive = true;

    public boolean chunkUpdateOptimization = true;
    public int chunkUpdateBatchSize = 16;
    public boolean chunkUpdateAsync = true;

    public boolean cacheOptimizationEnabled = true;
    public int cacheMaxSize = 1024;
    public int cacheTtl = 300;
    public boolean cachePersistent = false;

    public boolean netherOptimizationEnabled = true;
    public int netherCeilingCullingDistance = 128;
    public boolean netherFloorCullingEnabled = true;
    public int netherFloorCullingDistance = 64;

    public boolean endOptimizationEnabled = true;
    public int endIslandCullingDistance = 96;
    public boolean endVoidCullingEnabled = true;
    public int endVoidCullingDistance = 128;

    public boolean debugHudEnabled = false;
    public int debugHudRefreshRate = 20;
    public boolean debugHudShowCacheStats = true;
    public boolean debugHudShowThreadStats = true;
    public boolean debugHudShowCullingBreakdown = true;

    public boolean performanceProfilingEnabled = false;
    public int profilingSampleInterval = 60;
    public boolean profilingAutoOptimize = false;

    public boolean compatibilityMode = false;
    public boolean sodiumIntegrationEnabled = true;
    public boolean irisIntegrationEnabled = true;
    public boolean optifineCompatibilityMode = false;

    public boolean experimentalFeaturesEnabled = false;
    public boolean experimentalAsyncRendering = false;
    public boolean experimentalPredictiveCulling = false;
    public int experimentalPredictiveRadius = 32;

    private static EliminateConfig instance;

    public static EliminateConfig getInstance() {
        if (instance == null) {
            load();
        }
        return instance;
    }

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                instance = GSON.fromJson(reader, EliminateConfig.class);
            } catch (IOException e) {
                instance = new EliminateConfig();
            }
        } else {
            instance = new EliminateConfig();
            save();
        }
    }

    public static void save() {
        if (instance == null) instance = new EliminateConfig();
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(instance, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void validate() {
        cullingDistance = Math.max(8, Math.min(256, cullingDistance));
        updateSpeed = Math.max(1, Math.min(20, updateSpeed));
        threadPoolSize = threadPoolSize == -1 ? -1 : Math.max(1, Math.min(32, threadPoolSize));
        preCalculateRadius = Math.max(1, Math.min(32, preCalculateRadius));
        targetFps = Math.max(30, Math.min(300, targetFps));
        minDynamicDistance = Math.max(8, Math.min(64, minDynamicDistance));
        maxDynamicDistance = Math.max(minDynamicDistance, Math.min(256, maxDynamicDistance));
        verticalCullingDistance = Math.max(8, Math.min(128, verticalCullingDistance));
        verticalCullingSurfaceMargin = Math.max(0, Math.min(32, verticalCullingSurfaceMargin));
        backfaceCullingDistance = Math.max(16, Math.min(256, backfaceCullingDistance));
        entityCullingDistance = Math.max(32, Math.min(512, entityCullingDistance));
        fogCullingDistance = Math.max(32, Math.min(256, fogCullingDistance));
        chunkUpdateBatchSize = Math.max(4, Math.min(64, chunkUpdateBatchSize));
        cacheMaxSize = Math.max(128, Math.min(8192, cacheMaxSize));
        cacheTtl = Math.max(60, Math.min(3600, cacheTtl));
        netherCeilingCullingDistance = Math.max(64, Math.min(256, netherCeilingCullingDistance));
        netherFloorCullingDistance = Math.max(32, Math.min(128, netherFloorCullingDistance));
        endIslandCullingDistance = Math.max(48, Math.min(256, endIslandCullingDistance));
        endVoidCullingDistance = Math.max(64, Math.min(256, endVoidCullingDistance));
        debugHudRefreshRate = Math.max(5, Math.min(60, debugHudRefreshRate));
        profilingSampleInterval = Math.max(10, Math.min(300, profilingSampleInterval));
        experimentalPredictiveRadius = Math.max(16, Math.min(128, experimentalPredictiveRadius));
    }
}
