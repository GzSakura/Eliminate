package dev.miitong.eliminate.client;

import dev.miitong.eliminate.config.EliminateConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.loading.FMLLoader;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

@Mod.EventBusSubscriber(modid = "eliminate", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EliminateClient {

    public static final Logger LOGGER = LoggerFactory.getLogger("Eliminate");
    public static final boolean IRIS_LOADED = FMLLoader.getLoadingModList().getModFileById("iris") != null;
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

    private static int tickCounter = 0;

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

    public static void onInitializeClient(FMLClientSetupEvent event) {
        EliminateConfig.load();
        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event1) -> {
            if (EliminateConfig.getInstance().debugMode && event1.getEntity() != null) {
                tickCounter++;
                if (tickCounter >= 20) {
                    // Manual concatenation to avoid placeholder issues
                    String text = Text.translatable("hud.eliminate.actionbar").getString() + 
                        Text.translatable("hud.eliminate.total").getString() + TOTAL_CHECKED + " | " +
                        Text.translatable("hud.eliminate.fov").getString() + CULLED_FOV + " | " +
                        Text.translatable("hud.eliminate.vert").getString() + CULLED_VERTICAL + " | " +
                        Text.translatable("hud.eliminate.mountain").getString() + CULLED_MOUNTAIN + " | " +
                        Text.translatable("hud.eliminate.y_info").getString() + (int)event1.getEntity().getY() + " (Surf: " + debugCachedSurfaceY + ") | " +
                        Text.translatable("hud.eliminate.underground").getString() + debugCachedUnderground;

                    Text actionBarText = Text.literal(text).formatted(Formatting.YELLOW);
                    
                    event1.getEntity().sendMessage(actionBarText, true);
                    
                    LOGGER.info(actionBarText.getString());
                    
                    tickCounter = 0;
                    CULLED_COUNT = 0;
                    CULLED_BACK = 0;
                    CULLED_VERTICAL = 0;
                    TOTAL_CHECKED = 0;
                }
            } else {
                CULLED_COUNT = 0;
                CULLED_BACK = 0;
                CULLED_VERTICAL = 0;
                TOTAL_CHECKED = 0;
                HUD_CULLED_COUNT = 0;
                HUD_CULLED_BACK = 0;
                HUD_CULLED_VERTICAL = 0;
                HUD_TOTAL_CHECKED = 0;
            }
        });
    }
}
