package dev.miitong.eliminate.client.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.miitong.eliminate.config.EliminateConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.translatable("config.eliminate.title"));

            ConfigCategory general = builder.getOrCreateCategory(Text.translatable("config.eliminate.category.general"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            EliminateConfig config = EliminateConfig.getInstance();

            general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.enabled"), config.enabled)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.enabled = newValue)
                    .build());

            general.addEntry(entryBuilder.startIntSlider(Text.translatable("config.eliminate.option.cullingDistance"), config.cullingDistance, 0, 256)
                    .setDefaultValue(32)
                    .setSaveConsumer(newValue -> config.cullingDistance = newValue)
                    .build());

            general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.debugMode"), config.debugMode)
                    .setDefaultValue(false)
                    .setSaveConsumer(newValue -> config.debugMode = newValue)
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

            advanced.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.fovCullingEnabled"), config.fovCullingEnabled)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.fovCullingEnabled = newValue)
                    .setTooltip(Text.translatable("config.eliminate.option.fovCullingEnabled.tooltip"))
                    .build());

            advanced.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.aggressiveMountainCulling"), config.aggressiveMountainCulling)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.aggressiveMountainCulling = newValue)
                    .setTooltip(Text.translatable("config.eliminate.option.aggressiveMountainCulling.tooltip"))
                    .build());

            advanced.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.transparencyAwareness"), config.transparencyAwareness)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.transparencyAwareness = newValue)
                    .setTooltip(Text.translatable("config.eliminate.option.transparencyAwareness.tooltip"))
                    .build());
            
            // New features
            general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.horizontalCulling"), config.horizontalCulling)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.horizontalCulling = newValue)
                    .setTooltip(Text.translatable("config.eliminate.option.horizontalCulling.tooltip"))
                    .build());
            
            general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.biomeAwareCulling"), config.biomeAwareCulling)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.biomeAwareCulling = newValue)
                    .setTooltip(Text.translatable("config.eliminate.option.biomeAwareCulling.tooltip"))
                    .build());
            
            advanced.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.dynamicCullingDistance"), config.dynamicCullingDistance)
                    .setDefaultValue(false)
                    .setSaveConsumer(newValue -> config.dynamicCullingDistance = newValue)
                    .setTooltip(Text.translatable("config.eliminate.option.dynamicCullingDistance.tooltip"))
                    .build());
            
            advanced.addEntry(entryBuilder.startIntSlider(Text.translatable("config.eliminate.option.targetFps"), config.targetFps, 30, 120)
                    .setDefaultValue(60)
                    .setSaveConsumer(newValue -> config.targetFps = newValue)
                    .setTooltip(Text.translatable("config.eliminate.option.targetFps.tooltip"))
                    .build());
            
            advanced.addEntry(entryBuilder.startIntSlider(Text.translatable("config.eliminate.option.minDynamicDistance"), config.minDynamicDistance, 8, 64)
                    .setDefaultValue(16)
                    .setSaveConsumer(newValue -> config.minDynamicDistance = newValue)
                    .setTooltip(Text.translatable("config.eliminate.option.minDynamicDistance.tooltip"))
                    .build());
            
            advanced.addEntry(entryBuilder.startIntSlider(Text.translatable("config.eliminate.option.maxDynamicDistance"), config.maxDynamicDistance, 16, 128)
                    .setDefaultValue(64)
                    .setSaveConsumer(newValue -> config.maxDynamicDistance = newValue)
                    .setTooltip(Text.translatable("config.eliminate.option.maxDynamicDistance.tooltip"))
                    .build());

            builder.setSavingRunnable(EliminateConfig::save);

            return builder.build();
        };
    }
}
