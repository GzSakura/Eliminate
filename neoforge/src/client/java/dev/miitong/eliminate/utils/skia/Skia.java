package dev.miitong.eliminate.utils.skia;

import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.Paint;
import net.minecraft.client.network.AbstractClientPlayerEntity;

import java.awt.Color;

public class Skia {
    private static Canvas currentCanvas;

    public static void setCanvas(Canvas canvas) {
        currentCanvas = canvas;
    }

    public static void drawText(String text, float x, float y, Color color, Font font) {
        if (currentCanvas == null) return;
        try (Paint paint = new Paint().setColor(color.getRGB())) {
            currentCanvas.drawString(text, x, y, font, paint);
        }
    }

    public static void drawPlayerHead(AbstractClientPlayerEntity player, float x, float y, float width, float height, float scale) {
        // Implementation for drawing player head if needed
    }

    public static void drawSkin(AbstractClientPlayerEntity player, float x, float y, float scale) {
        // Implementation for drawing skin if needed
    }
}
