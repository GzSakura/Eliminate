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
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.client.util.Window;

import java.awt.Color;

public class SkiaHudRenderer implements HudRenderCallback {

    private int lastWidth = -1;
    private int lastHeight = -1;
    private final Font font;

    public SkiaHudRenderer() {
        Typeface typeface;
        // Try to load a font that supports Chinese on Windows
        typeface = Typeface.makeFromName("Microsoft YaHei", io.github.humbleui.skija.FontStyle.NORMAL);
        if (typeface == null) {
            typeface = Typeface.makeFromName("SimSun", io.github.humbleui.skija.FontStyle.NORMAL);
        }
        if (typeface == null) {
            typeface = Typeface.makeDefault();
        }
        this.font = new Font(typeface, SkiaStyles.FONT_SIZE);
    }

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
        
        // Reset HUD counters after rendering
        EliminateClient.HUD_CULLED_COUNT = 0;
        EliminateClient.HUD_CULLED_BACK = 0;
        EliminateClient.HUD_CULLED_VERTICAL = 0;
        EliminateClient.HUD_TOTAL_CHECKED = 0;
    }

    private void drawStats(MinecraftClient client) {
        float x = SkiaStyles.STATS_X;
        float y = SkiaStyles.STATS_Y;
        float lineHeight = SkiaStyles.LINE_HEIGHT;

        String disabledStr = I18n.translate("hud.eliminate.disabled");
        String backStatus = Math.abs(client.player.getRotationVec(1.0F).y) > 0.5 ? disabledStr : String.valueOf(EliminateClient.HUD_CULLED_BACK);

        String[] lines = {
            I18n.translate("hud.eliminate.total") + EliminateClient.HUD_TOTAL_CHECKED,
            I18n.translate("hud.eliminate.back") + backStatus,
            I18n.translate("hud.eliminate.vert") + EliminateClient.HUD_CULLED_VERTICAL,
            I18n.translate("hud.eliminate.y_info") + (int)client.player.getY() + " (Surf: " + EliminateClient.debugCachedSurfaceY + ")",
            I18n.translate("hud.eliminate.underground") + EliminateClient.debugCachedUnderground
        };

        for (String line : lines) {
            Skia.drawText(line, x, y, SkiaStyles.TEXT_COLOR, font);
            y += lineHeight;
        }
    }

    private void drawPlayer(MinecraftClient client) {
        float x = SkiaStyles.PLAYER_X;
        float y = SkiaStyles.PLAYER_Y;
        float size = SkiaStyles.PLAYER_HEAD_SIZE;
        
        Skia.drawPlayerHead(client.player, x, y, size, size, 4);
        Skia.drawSkin(client.player, x + 50, y, SkiaStyles.SKIN_SCALE);
    }
}
