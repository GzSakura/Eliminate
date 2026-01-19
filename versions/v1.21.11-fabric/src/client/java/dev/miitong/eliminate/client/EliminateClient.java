package dev.miitong.eliminate.client;

import dev.miitong.eliminate.client.integration.SodiumOptionsIntegration;
import dev.miitong.eliminate.config.EliminateConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class EliminateClient implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("Eliminate");
    public static final boolean IRIS_LOADED = FabricLoader.getInstance().isModLoaded("iris");
    public static final boolean SODIUM_LOADED = FabricLoader.getInstance().isModLoaded("sodium");
    public static final boolean OPTIFINE_LOADED = FabricLoader.getInstance().isModLoaded("optifabric") || FabricLoader.getInstance().isModLoaded("optifine");
    public static final boolean LITHIUM_LOADED = FabricLoader.getInstance().isModLoaded("lithium");
    public static final boolean PHOSPHOR_LOADED = FabricLoader.getInstance().isModLoaded("phosphor");
    public static final boolean KRYPTON_LOADED = FabricLoader.getInstance().isModLoaded("krypton");
    
    // Culling statistics
    public static int CULLED_COUNT = 0;
    public static int CULLED_VERTICAL = 0;
    public static int CULLED_BACK = 0;
    public static int CULLED_FOV = 0;
    public static int CULLED_MOUNTAIN = 0;
    public static int CULLED_END = 0;
    public static int TOTAL_CHECKED = 0;

    // HUD specific counters to avoid conflict with tick-based action bar
    public static int HUD_CULLED_COUNT = 0;
    public static int HUD_CULLED_VERTICAL = 0;
    public static int HUD_CULLED_BACK = 0;
    public static int HUD_CULLED_FOV = 0;
    public static int HUD_CULLED_MOUNTAIN = 0;
    public static int HUD_CULLED_END = 0;
    public static int HUD_TOTAL_CHECKED = 0;

    // Cache statistics
    public static int CACHE_HITS = 0;
    public static int CACHE_MISSES = 0;
    public static int CACHE_SIZE = 0;

    // Thread pool statistics
    public static int THREAD_POOL_ACTIVE = 0;
    public static int THREAD_POOL_QUEUE = 0;

    // Debug information
    public static boolean debugCachedUnderground = false;
    public static int debugCachedSurfaceY = 0;
    public static String currentDimension = "overworld";

    // Performance metrics
    public static double averageCullingRate = 0.0;
    public static long lastResetTime = System.currentTimeMillis();

    private int tickCounter = 0;

    private static boolean irisApiResolved = false;
    private static Object irisApiInstance;
    private static Method irisIsShadowPassMethod;
    private static int cachedShadowFrameIndex = Integer.MIN_VALUE;
    private static boolean cachedShadowPass = false;

    public static boolean isIrisShadowPass(int frameIndex) {
        if (!IRIS_LOADED) return false;
        if (cachedShadowFrameIndex == frameIndex) {
            if (cachedShadowPass) return true;
            cachedShadowPass = queryIrisShadowPass();
            return cachedShadowPass;
        }

        cachedShadowFrameIndex = frameIndex;
        cachedShadowPass = queryIrisShadowPass();
        return cachedShadowPass;
    }

    public static boolean isRenderingShadowPass() {
        return queryIrisShadowPass();
    }

    private static boolean queryIrisShadowPass() {
        try {
            if (!irisApiResolved) {
                irisApiResolved = true;
                Class<?> irisApiClass = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
                Method getInstance = irisApiClass.getMethod("getInstance");
                irisApiInstance = getInstance.invoke(null);
                irisIsShadowPassMethod = irisApiClass.getMethod("isRenderingShadowPass");
            }
            if (irisApiInstance == null || irisIsShadowPassMethod == null) return false;
            Object result = irisIsShadowPassMethod.invoke(irisApiInstance);
            return result instanceof Boolean && (Boolean) result;
        } catch (Throwable ignored) {
            irisApiResolved = true;
            irisApiInstance = null;
            irisIsShadowPassMethod = null;
            return false;
        }
    }

    @Override
    public void onInitializeClient() {
        EliminateConfig.load();
        
        // Initialize Sodium integration
        SodiumOptionsIntegration.initialize();
        
        // Log compatibility information
        LOGGER.info("Eliminate initialized with compatibility checks:");
        LOGGER.info("- Sodium: {}", SODIUM_LOADED ? "Loaded" : "Not loaded");
        LOGGER.info("- Iris: {}", IRIS_LOADED ? "Loaded" : "Not loaded");
        LOGGER.info("- OptiFine: {}", OPTIFINE_LOADED ? "Loaded" : "Not loaded");
        LOGGER.info("- Lithium: {}", LITHIUM_LOADED ? "Loaded" : "Not loaded");
        LOGGER.info("- Phosphor: {}", PHOSPHOR_LOADED ? "Loaded" : "Not loaded");
        LOGGER.info("- Krypton: {}", KRYPTON_LOADED ? "Loaded" : "Not loaded");
        
        // Apply compatibility adjustments
        applyCompatibilityAdjustments();
        
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (EliminateConfig.getInstance().debugMode && client.player != null) {
                tickCounter++;
                if (tickCounter >= 20) {
                    // Update dimension info
                    if (client.world != null) {
                        String dimensionPath = client.world.getRegistryKey().getValue().getPath();
                        if (dimensionPath.contains("nether")) {
                            currentDimension = "nether";
                        } else if (dimensionPath.contains("end")) {
                            currentDimension = "end";
                        } else {
                            currentDimension = "overworld";
                        }
                    }
                    
                    // Calculate culling rate
                    double cullingRate = TOTAL_CHECKED > 0 ? (double)CULLED_COUNT / TOTAL_CHECKED * 100.0 : 0.0;
                    averageCullingRate = (averageCullingRate * 0.9 + cullingRate * 0.1);
                    
                    // Update thread pool stats
                    try {
                        THREAD_POOL_ACTIVE = dev.miitong.eliminate.client.util.AsyncTaskManager.getActiveCount();
                        THREAD_POOL_QUEUE = dev.miitong.eliminate.client.util.AsyncTaskManager.getQueueSize();
                    } catch (Exception e) {
                        // Ignore if AsyncTaskManager is not initialized
                    }
                    
                    // Manual concatenation to avoid placeholder issues
                    String text = "Eliminate | " +
                        "Total: " + TOTAL_CHECKED + " | " +
                        "Culled: " + CULLED_COUNT + " (" + String.format("%.1f", cullingRate) + "%) | " +
                        "FOV: " + CULLED_FOV + " | " +
                        "Vert: " + CULLED_VERTICAL + " | " +
                        "Mtn: " + CULLED_MOUNTAIN + " | " +
                        "End: " + CULLED_END + " | " +
                        "Dim: " + currentDimension + " | " +
                        "Y: " + (int)client.player.getY() + " (Surf: " + debugCachedSurfaceY + ")";

                    Text actionBarText = Text.literal(text).formatted(Formatting.YELLOW);
                    
                    client.player.sendMessage(actionBarText, true);
                    
                    // Detailed log for debugging
                    LOGGER.info("Eliminate Stats:");
                    LOGGER.info("- Culling: Total={}, Culled={} ({}%), Rate={}%", 
                        TOTAL_CHECKED, CULLED_COUNT, String.format("%.1f", cullingRate), String.format("%.1f", averageCullingRate));
                    LOGGER.info("- Breakdown: FOV={}, Vertical={}, Mountain={}, End={}", 
                        CULLED_FOV, CULLED_VERTICAL, CULLED_MOUNTAIN, CULLED_END);
                    LOGGER.info("- Cache: Hits={}, Misses={}, Size={}", 
                        CACHE_HITS, CACHE_MISSES, CACHE_SIZE);
                    LOGGER.info("- Threads: Active={}, Queue={}", 
                        THREAD_POOL_ACTIVE, THREAD_POOL_QUEUE);
                    LOGGER.info("- Dimension: {}, Y={}, Surface={}, Underground={}", 
                        currentDimension, (int)client.player.getY(), debugCachedSurfaceY, debugCachedUnderground);
                    
                    tickCounter = 0;
                    resetCounters();
                }
            } else {
                resetCounters();
            }
        });
    }

    private void resetCounters() {
        CULLED_COUNT = 0;
        CULLED_BACK = 0;
        CULLED_VERTICAL = 0;
        CULLED_FOV = 0;
        CULLED_MOUNTAIN = 0;
        CULLED_END = 0;
        TOTAL_CHECKED = 0;
        HUD_CULLED_COUNT = 0;
        HUD_CULLED_BACK = 0;
        HUD_CULLED_VERTICAL = 0;
        HUD_CULLED_FOV = 0;
        HUD_CULLED_MOUNTAIN = 0;
        HUD_CULLED_END = 0;
        HUD_TOTAL_CHECKED = 0;
    }

    /**
     * Apply compatibility adjustments based on detected mods
     */
    private void applyCompatibilityAdjustments() {
        EliminateConfig config = EliminateConfig.getInstance();
        
        // Sodium compatibility
        if (SODIUM_LOADED) {
            LOGGER.info("Applying Sodium compatibility adjustments");
            // Enable sync with Sodium for better performance
            if (!config.syncWithSodium) {
                config.syncWithSodium = true;
                EliminateConfig.save();
                LOGGER.info("Enabled 'Sync with Sodium' mode for better performance");
            }
        }
        
        // OptiFine compatibility
        if (OPTIFINE_LOADED) {
            LOGGER.info("Applying OptiFine compatibility adjustments");
            // Reduce thread pool size for OptiFine compatibility
            if (config.threadPoolSize > 4) {
                config.threadPoolSize = 4;
                EliminateConfig.save();
                LOGGER.info("Reduced thread pool size to 4 for OptiFine compatibility");
            }
        }
        
        // Lithium compatibility
        if (LITHIUM_LOADED) {
            LOGGER.info("Applying Lithium compatibility adjustments");
            // Lithium already optimizes chunk loading, so reduce our pre-calculation radius
            if (config.preCalculateRadius > 6) {
                config.preCalculateRadius = 6;
                EliminateConfig.save();
                LOGGER.info("Reduced pre-calculation radius to 6 for Lithium compatibility");
            }
        }
    }
}
