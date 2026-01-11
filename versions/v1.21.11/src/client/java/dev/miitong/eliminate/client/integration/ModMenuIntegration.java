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
                    .setDefaultValue(64)
                    .setSaveConsumer(newValue -> config.cullingDistance = newValue)
                    .build());

            general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.eliminate.option.debugMode"), config.debugMode)
                    .setDefaultValue(false)
                    .setSaveConsumer(newValue -> config.debugMode = newValue)
                    .build());

            builder.setSavingRunnable(EliminateConfig::save);

            return builder.build();
        };
    }
}
