package dev.miitong.eliminate.client.integration;

import dev.miitong.eliminate.client.EliminateClient;
import dev.miitong.eliminate.config.EliminateConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

public class ModMenuIntegration {

    public static ConfigBuilder getConfigBuilder() {
        ConfigBuilder builder = ConfigBuilder.create()
                .setTitle(Text.translatable("config.eliminate.title"))
                .setTransparentBackground(true)
                .setSavingRunnable(EliminateConfig::save);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        EliminateConfig config = EliminateConfig.getInstance();

        config.validate();

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("config.eliminate.category.general"));

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.enabled"), config.enabled)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.enabled = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.enabled.tooltip"))
                .build());

        general.addEntry(entryBuilder.startIntSlider(Text.translatable("config.eliminate.option.cullingDistance"), config.cullingDistance, 8, 256)
                .setDefaultValue(32)
                .setSaveConsumer(newValue -> config.cullingDistance = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.cullingDistance.tooltip"))
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.debugMode"), config.debugMode)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> config.debugMode = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.debugMode.tooltip"))
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.autoOptimize"), config.autoOptimize)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> config.autoOptimize = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.autoOptimize.tooltip"))
                .build());

        ConfigCategory culling = builder.getOrCreateCategory(Text.translatable("config.eliminate.category.culling"));

        culling.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.fovCullingEnabled"), config.fovCullingEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.fovCullingEnabled = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.fovCullingEnabled.tooltip"))
                .build());

        culling.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.aggressiveMountainCulling"), config.aggressiveMountainCulling)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.aggressiveMountainCulling = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.aggressiveMountainCulling.tooltip"))
                .build());

        culling.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.transparencyAwareness"), config.transparencyAwareness)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.transparencyAwareness = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.transparencyAwareness.tooltip"))
                .build());

        culling.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.horizontalCulling"), config.horizontalCulling)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.horizontalCulling = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.horizontalCulling.tooltip"))
                .build());

        culling.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.biomeAwareCulling"), config.biomeAwareCulling)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.biomeAwareCulling = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.biomeAwareCulling.tooltip"))
                .build());

        culling.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.endDimensionOptimization"), config.endDimensionOptimization)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.endDimensionOptimization = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.endDimensionOptimization.tooltip"))
                .build());

        ConfigCategory vertical = builder.getOrCreateCategory(Text.translatable("config.eliminate.category.vertical"));

        vertical.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.verticalCullingEnabled"), config.verticalCullingEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.verticalCullingEnabled = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.verticalCullingEnabled.tooltip"))
                .build());

        vertical.addEntry(entryBuilder.startIntSlider(Text.translatable("config.eliminate.option.verticalCullingDistance"), config.verticalCullingDistance, 8, 128)
                .setDefaultValue(32)
                .setSaveConsumer(newValue -> config.verticalCullingDistance = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.verticalCullingDistance.tooltip"))
                .build());

        vertical.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.verticalCullingUndergroundOnly"), config.verticalCullingUndergroundOnly)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> config.verticalCullingUndergroundOnly = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.verticalCullingUndergroundOnly.tooltip"))
                .build());

        vertical.addEntry(entryBuilder.startIntSlider(Text.translatable("config.eliminate.option.verticalCullingSurfaceMargin"), config.verticalCullingSurfaceMargin, 0, 32)
                .setDefaultValue(8)
                .setSaveConsumer(newValue -> config.verticalCullingSurfaceMargin = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.verticalCullingSurfaceMargin.tooltip"))
                .build());

        ConfigCategory backface = builder.getOrCreateCategory(Text.translatable("config.eliminate.category.backface"));

        backface.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.backfaceCullingEnabled"), config.backfaceCullingEnabled)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> config.backfaceCullingEnabled = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.backfaceCullingEnabled.tooltip"))
                .build());

        backface.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.backfaceCullingAggressive"), config.backfaceCullingAggressive)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> config.backfaceCullingAggressive = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.backfaceCullingAggressive.tooltip"))
                .build());

        backface.addEntry(entryBuilder.startIntSlider(Text.translatable("config.eliminate.option.backfaceCullingDistance"), config.backfaceCullingDistance, 16, 256)
                .setDefaultValue(64)
                .setSaveConsumer(newValue -> config.backfaceCullingDistance = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.backfaceCullingDistance.tooltip"))
                .build());

        ConfigCategory entity = builder.getOrCreateCategory(Text.translatable("config.eliminate.category.entity"));

        entity.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.entityCullingEnabled"), config.entityCullingEnabled)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> config.entityCullingEnabled = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.entityCullingEnabled.tooltip"))
                .build());

        entity.addEntry(entryBuilder.startIntSlider(Text.translatable("config.eliminate.option.entityCullingDistance"), config.entityCullingDistance, 32, 512)
                .setDefaultValue(128)
                .setSaveConsumer(newValue -> config.entityCullingDistance = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.entityCullingDistance.tooltip"))
                .build());

        entity.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.entityCullingIncludeItems"), config.entityCullingIncludeItems)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.entityCullingIncludeItems = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.entityCullingIncludeItems.tooltip"))
                .build());

        entity.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.entityCullingIncludeParticles"), config.entityCullingIncludeParticles)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> config.entityCullingIncludeParticles = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.entityCullingIncludeParticles.tooltip"))
                .build());

        ConfigCategory fog = builder.getOrCreateCategory(Text.translatable("config.eliminate.category.fog"));

        fog.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.fogCullingEnabled"), config.fogCullingEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.fogCullingEnabled = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.fogCullingEnabled.tooltip"))
                .build());

        fog.addEntry(entryBuilder.startIntSlider(Text.translatable("config.eliminate.option.fogCullingDistance"), config.fogCullingDistance, 32, 256)
                .setDefaultValue(96)
                .setSaveConsumer(newValue -> config.fogCullingDistance = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.fogCullingDistance.tooltip"))
                .build());

        fog.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.fogCullingAdaptive"), config.fogCullingAdaptive)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.fogCullingAdaptive = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.fogCullingAdaptive.tooltip"))
                .build());

        ConfigCategory performance = builder.getOrCreateCategory(Text.translatable("config.eliminate.category.performance"));

        performance.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.dynamicCullingDistance"), config.dynamicCullingDistance)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> config.dynamicCullingDistance = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.dynamicCullingDistance.tooltip"))
                .build());

        performance.addEntry(entryBuilder.startIntSlider(Text.translatable("config.eliminate.option.targetFps"), config.targetFps, 30, 300)
                .setDefaultValue(60)
                .setSaveConsumer(newValue -> config.targetFps = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.targetFps.tooltip"))
                .build());

        performance.addEntry(entryBuilder.startIntSlider(Text.translatable("config.eliminate.option.minDynamicDistance"), config.minDynamicDistance, 8, 64)
                .setDefaultValue(16)
                .setSaveConsumer(newValue -> config.minDynamicDistance = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.minDynamicDistance.tooltip"))
                .build());

        performance.addEntry(entryBuilder.startIntSlider(Text.translatable("config.eliminate.option.maxDynamicDistance"), config.maxDynamicDistance, 16, 256)
                .setDefaultValue(64)
                .setSaveConsumer(newValue -> config.maxDynamicDistance = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.maxDynamicDistance.tooltip"))
                .build());

        ConfigCategory advanced = builder.getOrCreateCategory(Text.translatable("config.eliminate.category.advanced"));

        advanced.addEntry(entryBuilder.startIntSlider(Text.translatable("config.eliminate.option.updateSpeed"), config.updateSpeed, 1, 20)
                .setDefaultValue(20)
                .setSaveConsumer(newValue -> config.updateSpeed = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.updateSpeed.tooltip"))
                .build());

        advanced.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.syncWithSodium"), config.syncWithSodium)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> config.syncWithSodium = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.syncWithSodium.tooltip"))
                .build());

        advanced.addEntry(entryBuilder.startIntSlider(Text.translatable("config.eliminate.option.threadPoolSize"), config.threadPoolSize, -1, 32)
                .setDefaultValue(-1)
                .setSaveConsumer(newValue -> config.threadPoolSize = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.threadPoolSize.tooltip"))
                .build());

        advanced.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.preCalculateChunks"), config.preCalculateChunks)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.preCalculateChunks = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.preCalculateChunks.tooltip"))
                .build());

        advanced.addEntry(entryBuilder.startIntSlider(Text.translatable("config.eliminate.option.preCalculateRadius"), config.preCalculateRadius, 1, 32)
                .setDefaultValue(8)
                .setSaveConsumer(newValue -> config.preCalculateRadius = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.preCalculateRadius.tooltip"))
                .build());

        ConfigCategory cache = builder.getOrCreateCategory(Text.translatable("config.eliminate.category.cache"));

        cache.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.cacheOptimizationEnabled"), config.cacheOptimizationEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.cacheOptimizationEnabled = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.cacheOptimizationEnabled.tooltip"))
                .build());

        cache.addEntry(entryBuilder.startIntSlider(Text.translatable("config.eliminate.option.cacheMaxSize"), config.cacheMaxSize, 128, 8192)
                .setDefaultValue(1024)
                .setSaveConsumer(newValue -> config.cacheMaxSize = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.cacheMaxSize.tooltip"))
                .build());

        cache.addEntry(entryBuilder.startIntSlider(Text.translatable("config.eliminate.option.cacheTtl"), config.cacheTtl, 60, 3600)
                .setDefaultValue(300)
                .setSaveConsumer(newValue -> config.cacheTtl = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.cacheTtl.tooltip"))
                .build());

        cache.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.cachePersistent"), config.cachePersistent)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> config.cachePersistent = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.cachePersistent.tooltip"))
                .build());

        ConfigCategory dimension = builder.getOrCreateCategory(Text.translatable("config.eliminate.category.dimension"));

        dimension.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.netherOptimizationEnabled"), config.netherOptimizationEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.netherOptimizationEnabled = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.netherOptimizationEnabled.tooltip"))
                .build());

        dimension.addEntry(entryBuilder.startIntSlider(Text.translatable("config.eliminate.option.netherCeilingCullingDistance"), config.netherCeilingCullingDistance, 64, 256)
                .setDefaultValue(128)
                .setSaveConsumer(newValue -> config.netherCeilingCullingDistance = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.netherCeilingCullingDistance.tooltip"))
                .build());

        dimension.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.netherFloorCullingEnabled"), config.netherFloorCullingEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.netherFloorCullingEnabled = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.netherFloorCullingEnabled.tooltip"))
                .build());

        dimension.addEntry(entryBuilder.startIntSlider(Text.translatable("config.eliminate.option.netherFloorCullingDistance"), config.netherFloorCullingDistance, 32, 128)
                .setDefaultValue(64)
                .setSaveConsumer(newValue -> config.netherFloorCullingDistance = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.netherFloorCullingDistance.tooltip"))
                .build());

        dimension.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.endOptimizationEnabled"), config.endOptimizationEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.endOptimizationEnabled = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.endOptimizationEnabled.tooltip"))
                .build());

        dimension.addEntry(entryBuilder.startIntSlider(Text.translatable("config.eliminate.option.endIslandCullingDistance"), config.endIslandCullingDistance, 48, 256)
                .setDefaultValue(96)
                .setSaveConsumer(newValue -> config.endIslandCullingDistance = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.endIslandCullingDistance.tooltip"))
                .build());

        dimension.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.endVoidCullingEnabled"), config.endVoidCullingEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.endVoidCullingEnabled = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.endVoidCullingEnabled.tooltip"))
                .build());

        dimension.addEntry(entryBuilder.startIntSlider(Text.translatable("config.eliminate.option.endVoidCullingDistance"), config.endVoidCullingDistance, 64, 256)
                .setDefaultValue(128)
                .setSaveConsumer(newValue -> config.endVoidCullingDistance = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.endVoidCullingDistance.tooltip"))
                .build());

        ConfigCategory debug = builder.getOrCreateCategory(Text.translatable("config.eliminate.category.debug"));

        debug.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.debugHudEnabled"), config.debugHudEnabled)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> config.debugHudEnabled = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.debugHudEnabled.tooltip"))
                .build());

        debug.addEntry(entryBuilder.startIntSlider(Text.translatable("config.eliminate.option.debugHudRefreshRate"), config.debugHudRefreshRate, 5, 60)
                .setDefaultValue(20)
                .setSaveConsumer(newValue -> config.debugHudRefreshRate = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.debugHudRefreshRate.tooltip"))
                .build());

        debug.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.debugHudShowCacheStats"), config.debugHudShowCacheStats)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.debugHudShowCacheStats = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.debugHudShowCacheStats.tooltip"))
                .build());

        debug.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.debugHudShowThreadStats"), config.debugHudShowThreadStats)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.debugHudShowThreadStats = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.debugHudShowThreadStats.tooltip"))
                .build());

        debug.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.debugHudShowCullingBreakdown"), config.debugHudShowCullingBreakdown)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.debugHudShowCullingBreakdown = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.debugHudShowCullingBreakdown.tooltip"))
                .build());

        ConfigCategory profiling = builder.getOrCreateCategory(Text.translatable("config.eliminate.category.profiling"));

        profiling.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.performanceProfilingEnabled"), config.performanceProfilingEnabled)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> config.performanceProfilingEnabled = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.performanceProfilingEnabled.tooltip"))
                .build());

        profiling.addEntry(entryBuilder.startIntSlider(Text.translatable("config.eliminate.option.profilingSampleInterval"), config.profilingSampleInterval, 10, 300)
                .setDefaultValue(60)
                .setSaveConsumer(newValue -> config.profilingSampleInterval = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.profilingSampleInterval.tooltip"))
                .build());

        profiling.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.profilingAutoOptimize"), config.profilingAutoOptimize)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> config.profilingAutoOptimize = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.profilingAutoOptimize.tooltip"))
                .build());

        ConfigCategory compatibility = builder.getOrCreateCategory(Text.translatable("config.eliminate.category.compatibility"));

        compatibility.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.compatibilityMode"), config.compatibilityMode)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> config.compatibilityMode = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.compatibilityMode.tooltip"))
                .build());

        compatibility.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.sodiumIntegrationEnabled"), config.sodiumIntegrationEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.sodiumIntegrationEnabled = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.sodiumIntegrationEnabled.tooltip"))
                .build());

        compatibility.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.irisIntegrationEnabled"), config.irisIntegrationEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.irisIntegrationEnabled = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.irisIntegrationEnabled.tooltip"))
                .build());

        compatibility.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.optifineCompatibilityMode"), config.optifineCompatibilityMode)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> config.optifineCompatibilityMode = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.optifineCompatibilityMode.tooltip"))
                .build());

        ConfigCategory experimental = builder.getOrCreateCategory(Text.translatable("config.eliminate.category.experimental"));

        experimental.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.experimentalFeaturesEnabled"), config.experimentalFeaturesEnabled)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> config.experimentalFeaturesEnabled = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.experimentalFeaturesEnabled.tooltip"))
                .build());

        experimental.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.experimentalAsyncRendering"), config.experimentalAsyncRendering)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> config.experimentalAsyncRendering = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.experimentalAsyncRendering.tooltip"))
                .build());

        experimental.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.experimentalPredictiveCulling"), config.experimentalPredictiveCulling)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> config.experimentalPredictiveCulling = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.experimentalPredictiveCulling.tooltip"))
                .build());

        experimental.addEntry(entryBuilder.startIntSlider(Text.translatable("config.eliminate.option.experimentalPredictiveRadius"), config.experimentalPredictiveRadius, 16, 128)
                .setDefaultValue(32)
                .setSaveConsumer(newValue -> config.experimentalPredictiveRadius = newValue)
                .setTooltip(Text.translatable("config.eliminate.option.experimentalPredictiveRadius.tooltip"))
                .build());

        return builder.build();
    }
}
