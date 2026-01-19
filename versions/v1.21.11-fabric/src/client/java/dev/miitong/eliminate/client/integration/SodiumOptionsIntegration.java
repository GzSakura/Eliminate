package dev.miitong.eliminate.client.integration;

import dev.miitong.eliminate.client.EliminateClient;
import dev.miitong.eliminate.config.EliminateConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

public class SodiumOptionsIntegration {

    private static final boolean SODIUM_LOADED = FabricLoader.getInstance().isModLoaded("sodium");

    public static void initialize() {
        if (!SODIUM_LOADED) {
            EliminateClient.LOGGER.info("Sodium not detected, skipping Sodium options integration");
            return;
        }

        try {
            Class<?> sodiumOptionsGuiClass = Class.forName("net.caffeinemc.mods.sodium.client.gui.options.OptionPage");
            Class<?> sodiumOptionFlagClass = Class.forName("net.caffeinemc.mods.sodium.client.gui.options.OptionFlag");
            Class<?> sodiumOptionImplClass = Class.forName("net.caffeinemc.mods.sodium.client.gui.options.OptionImpl");
            
            EliminateClient.LOGGER.info("Sodium detected, initializing options integration");
            
        } catch (ClassNotFoundException e) {
            EliminateClient.LOGGER.warn("Failed to initialize Sodium options integration: {}", e.getMessage());
        }
    }

    public static boolean isSodiumLoaded() {
        return SODIUM_LOADED;
    }
}
