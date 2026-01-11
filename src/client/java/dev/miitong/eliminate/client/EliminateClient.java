package dev.miitong.eliminate.client;

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
    public static boolean DEBUG = false;
    public static int CULLED_COUNT = 0;
    public static int CULLED_VERTICAL = 0;
    public static int CULLED_BACK = 0;
    public static int TOTAL_CHECKED = 0;

    public static boolean debugCachedUnderground = false;
    public static int debugCachedSurfaceY = 0;

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
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (DEBUG && client.player != null) {
                tickCounter++;
                if (tickCounter >= 20) {
                    float pitch = client.player.getPitch();
                    String backStatus = Math.abs(client.player.getRotationVec(1.0F).y) > 0.5 ? "Disabled" : String.valueOf(CULLED_BACK);
                    
                    String message = String.format("Eliminate: Total %d | Back %s | Vert %d | Y: %d (Surf: %d) | Under: %b | Pitch: %.2f", 
                        TOTAL_CHECKED, backStatus, CULLED_VERTICAL, 
                        (int)client.player.getY(), debugCachedSurfaceY, debugCachedUnderground, pitch);
                    
                    client.player.sendMessage(Text.literal(message).formatted(Formatting.YELLOW), true);
                    
                    LOGGER.info(message);
                    
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
            }
        });
    }
}
