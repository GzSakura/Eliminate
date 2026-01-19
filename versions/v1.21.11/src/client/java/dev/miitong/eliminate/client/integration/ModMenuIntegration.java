package dev.miitong.eliminate.client.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.miitong.eliminate.config.EliminateConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.MinecraftClient;
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
            int maxRenderDistance = MinecraftClient.getInstance().options.getClampedViewDistance();

            general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.enabled"), config.enabled)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.enabled = newValue)
                    .build());

            general.addEntry(entryBuilder.startIntSlider(Text.translatable("config.eliminate.option.reservedHeight"), config.reservedHeight, 1, maxRenderDistance)
                    .setDefaultValue(2)
                    .setSaveConsumer(newValue -> config.reservedHeight = newValue)
                    .build());

            general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.debugMode"), config.debugMode)
                    .setDefaultValue(false)
                    .setSaveConsumer(newValue -> config.debugMode = newValue)
                    .build());

            general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.fovCullingEnabled"), config.fovCullingEnabled)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.fovCullingEnabled = newValue)
                    .setTooltip(Text.translatable("config.eliminate.option.fovCullingEnabled.tooltip"))
                    .build());

            general.addEntry(entryBuilder.startIntSlider(Text.translatable("config.eliminate.option.fovAngle"), config.fovAngle, 90, 180)
                    .setDefaultValue(110)
                    .setSaveConsumer(newValue -> config.fovAngle = newValue)
                    .setTooltip(Text.translatable("config.eliminate.option.fovAngle.tooltip"))
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

            builder.setSavingRunnable(EliminateConfig::save);

            return builder.build();
        };
    }
}
