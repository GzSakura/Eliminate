package dev.miitong.eliminate.client.render;

import dev.miitong.eliminate.client.EliminateClient;
import dev.miitong.eliminate.config.EliminateConfig;
import dev.miitong.eliminate.utils.skia.Skia;
import dev.miitong.eliminate.utils.skia.context.SkiaContext;
import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.Typeface;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Window;

import java.awt.Color;

public class SkiaHudRenderer implements HudRenderCallback {

    private int lastWidth = -1;
    private int lastHeight = -1;
    private final Font font = new Font(Typeface.makeDefault(), 20);
    private final Color textColor = new Color(255, 255, 0);

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        if (!EliminateConfig.getInstance().debugMode) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        
        Window window = client.getWindow();
        int width = window.getFramebufferWidth();
        int height = window.getFramebufferHeight();

        if (width != lastWidth || height != lastHeight) {
            SkiaContext.createSurface(width, height);
            lastWidth = width;
            lastHeight = height;
        }

        SkiaContext.draw(canvas -> {
            drawStats(client);
            drawPlayer(client);
        });
        
        // Reset counters after rendering
        EliminateClient.CULLED_COUNT = 0;
        EliminateClient.CULLED_BACK = 0;
        EliminateClient.CULLED_VERTICAL = 0;
        EliminateClient.TOTAL_CHECKED = 0;
    }

    private void drawStats(MinecraftClient client) {
        float x = 20;
        float y = 50;
        float lineHeight = 25;

        String backStatus = Math.abs(client.player.getRotationVec(1.0F).y) > 0.5 ? "Disabled" : String.valueOf(EliminateClient.CULLED_BACK);

        String[] lines = {
            String.format("Eliminate: Total %d", EliminateClient.TOTAL_CHECKED),
            String.format("Back: %s", backStatus),
            String.format("Vert: %d", EliminateClient.CULLED_VERTICAL),
            String.format("Y: %d (Surf: %d)", (int)client.player.getY(), EliminateClient.debugCachedSurfaceY),
            String.format("Under: %b", EliminateClient.debugCachedUnderground)
        };

        for (String line : lines) {
            Skia.drawText(line, x, y, textColor, font);
            y += lineHeight;
        }
    }

    private void drawPlayer(MinecraftClient client) {
        float x = 20;
        float y = 180;
        float size = 32;
        
        Skia.drawPlayerHead(client.player, x, y, size, size, 4);
        Skia.drawSkin(client.player, x + 50, y, 2.0f);
    }
}
