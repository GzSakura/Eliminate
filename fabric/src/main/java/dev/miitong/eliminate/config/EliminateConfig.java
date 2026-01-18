package dev.miitong.eliminate.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

@Config(name = "eliminate")
public class EliminateConfig implements ConfigData {

    private static EliminateConfig INSTANCE;

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("general")
    public General general = new General();

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("culling")
    public Culling culling = new Culling();

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("vertical")
    public Vertical vertical = new Vertical();

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("backface")
    public Backface backface = new Backface();

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("entity")
    public Entity entity = new Entity();

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("fog")
    public Fog fog = new Fog();

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("performance")
    public Performance performance = new Performance();

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("advanced")
    public Advanced advanced = new Advanced();

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("cache")
    public Cache cache = new Cache();

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("dimension")
    public Dimension dimension = new Dimension();

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("debug")
    public Debug debug = new Debug();

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("profiling")
    public Profiling profiling = new Profiling();

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("compatibility")
    public Compatibility compatibility = new Compatibility();

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("experimental")
    public Experimental experimental = new Experimental();

    public static class General {
        @ConfigEntry.Gui.Tooltip
        public boolean enabled = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 8, max = 256)
        public int cullingDistance = 32;

        @ConfigEntry.Gui.Tooltip
        public boolean debugMode = false;

        @ConfigEntry.Gui.Tooltip
        public boolean autoOptimize = false;
    }

    public static class Culling {
        @ConfigEntry.Gui.Tooltip
        public boolean fovCullingEnabled = true;

        @ConfigEntry.Gui.Tooltip
        public boolean aggressiveMountainCulling = true;

        @ConfigEntry.Gui.Tooltip
        public boolean transparencyAwareness = true;

        @ConfigEntry.Gui.Tooltip
        public boolean horizontalCulling = true;

        @ConfigEntry.Gui.Tooltip
        public boolean biomeAwareCulling = true;

        @ConfigEntry.Gui.Tooltip
        public boolean endDimensionOptimization = true;
    }

    public static class Vertical {
        @ConfigEntry.Gui.Tooltip
        public boolean verticalCullingEnabled = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 8, max = 128)
        public int verticalCullingDistance = 32;

        @ConfigEntry.Gui.Tooltip
        public boolean verticalCullingUndergroundOnly = false;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 32)
        public int verticalCullingSurfaceMargin = 8;
    }

    public static class Backface {
        @ConfigEntry.Gui.Tooltip
        public boolean backfaceCullingEnabled = false;

        @ConfigEntry.Gui.Tooltip
        public boolean backfaceCullingAggressive = false;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 16, max = 256)
        public int backfaceCullingDistance = 64;
    }

    public static class Entity {
        @ConfigEntry.Gui.Tooltip
        public boolean entityCullingEnabled = false;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 32, max = 512)
        public int entityCullingDistance = 128;

        @ConfigEntry.Gui.Tooltip
        public boolean entityCullingIncludeItems = true;

        @ConfigEntry.Gui.Tooltip
        public boolean entityCullingIncludeParticles = false;
    }

    public static class Fog {
        @ConfigEntry.Gui.Tooltip
        public boolean fogCullingEnabled = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 32, max = 256)
        public int fogCullingDistance = 96;

        @ConfigEntry.Gui.Tooltip
        public boolean fogCullingAdaptive = true;
    }

    public static class Performance {
        @ConfigEntry.Gui.Tooltip
        public boolean dynamicCullingDistance = false;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 30, max = 300)
        public int targetFps = 60;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 8, max = 64)
        public int minDynamicDistance = 16;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 16, max = 256)
        public int maxDynamicDistance = 64;
    }

    public static class Advanced {
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 20)
        public int updateSpeed = 20;

        @ConfigEntry.Gui.Tooltip
        public boolean syncWithSodium = false;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = -1, max = 32)
        public int threadPoolSize = -1;

        @ConfigEntry.Gui.Tooltip
        public boolean preCalculateChunks = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 32)
        public int preCalculateRadius = 8;
    }

    public static class Cache {
        @ConfigEntry.Gui.Tooltip
        public boolean cacheOptimizationEnabled = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 128, max = 8192)
        public int cacheMaxSize = 1024;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 60, max = 3600)
        public int cacheTtl = 300;

        @ConfigEntry.Gui.Tooltip
        public boolean cachePersistent = false;
    }

    public static class Dimension {
        @ConfigEntry.Gui.Tooltip
        public boolean netherOptimizationEnabled = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 64, max = 256)
        public int netherCeilingCullingDistance = 128;

        @ConfigEntry.Gui.Tooltip
        public boolean netherFloorCullingEnabled = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 32, max = 128)
        public int netherFloorCullingDistance = 64;

        @ConfigEntry.Gui.Tooltip
        public boolean endOptimizationEnabled = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 48, max = 256)
        public int endIslandCullingDistance = 96;

        @ConfigEntry.Gui.Tooltip
        public boolean endVoidCullingEnabled = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 64, max = 256)
        public int endVoidCullingDistance = 128;
    }

    public static class Debug {
        @ConfigEntry.Gui.Tooltip
        public boolean debugHudEnabled = false;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 5, max = 60)
        public int debugHudRefreshRate = 20;

        @ConfigEntry.Gui.Tooltip
        public boolean debugHudShowCacheStats = true;

        @ConfigEntry.Gui.Tooltip
        public boolean debugHudShowThreadStats = true;

        @ConfigEntry.Gui.Tooltip
        public boolean debugHudShowCullingBreakdown = true;
    }

    public static class Profiling {
        @ConfigEntry.Gui.Tooltip
        public boolean performanceProfilingEnabled = false;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 10, max = 300)
        public int profilingSampleInterval = 60;

        @ConfigEntry.Gui.Tooltip
        public boolean profilingAutoOptimize = false;
    }

    public static class Compatibility {
        @ConfigEntry.Gui.Tooltip
        public boolean compatibilityMode = false;

        @ConfigEntry.Gui.Tooltip
        public boolean sodiumIntegrationEnabled = true;

        @ConfigEntry.Gui.Tooltip
        public boolean irisIntegrationEnabled = true;

        @ConfigEntry.Gui.Tooltip
        public boolean optifineCompatibilityMode = false;
    }

    public static class Experimental {
        @ConfigEntry.Gui.Tooltip
        public boolean experimentalFeaturesEnabled = false;

        @ConfigEntry.Gui.Tooltip
        public boolean experimentalAsyncRendering = false;

        @ConfigEntry.Gui.Tooltip
        public boolean experimentalPredictiveCulling = false;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 16, max = 128)
        public int experimentalPredictiveRadius = 32;
    }

    public static void load() {
        AutoConfig.register(EliminateConfig.class, GsonConfigSerializer::new);
        INSTANCE = AutoConfig.getConfigHolder(EliminateConfig.class).getConfig();
    }

    public static EliminateConfig getInstance() {
        if (INSTANCE == null) {
            load();
        }
        return INSTANCE;
    }
}
