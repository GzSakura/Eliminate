package dev.miitong.eliminate.client;

import dev.miitong.eliminate.config.EliminateConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EliminateClient implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("Eliminate");
    public static int CULLED_COUNT = 0;
    public static int CULLED_VERTICAL = 0;
    public static int CULLED_BACK = 0;
    public static int CULLED_FOV = 0;
    public static int CULLED_MOUNTAIN = 0;
    public static int TOTAL_CHECKED = 0;

    // HUD specific counters to avoid conflict with tick-based action bar
    public static int HUD_CULLED_COUNT = 0;
    public static int HUD_CULLED_VERTICAL = 0;
    public static int HUD_CULLED_BACK = 0;
    public static int HUD_CULLED_FOV = 0;
    public static int HUD_CULLED_MOUNTAIN = 0;
    public static int HUD_TOTAL_CHECKED = 0;

    public static boolean debugCachedUnderground = false;
    public static int debugCachedSurfaceY = 0;

    @Override
    public void onInitializeClient() {
        EliminateConfig.load();
        LOGGER.info("Eliminate initialized with config: {}", EliminateConfig.getInstance().enabled);
    }
}