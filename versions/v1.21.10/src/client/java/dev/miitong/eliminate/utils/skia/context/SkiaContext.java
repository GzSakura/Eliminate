package dev.miitong.eliminate.utils.skia.context;

import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.ColorAlphaType;
import io.github.humbleui.skija.ColorInfo;
import io.github.humbleui.skija.ColorSpace;
import io.github.humbleui.skija.ColorType;
import io.github.humbleui.skija.ImageInfo;
import io.github.humbleui.skija.Surface;

import java.util.function.Consumer;

public class SkiaContext {
    private static Surface surface;
    private static Canvas canvas;

    public static void createSurface(int width, int height) {
        if (surface != null) {
            surface.close();
        }
        surface = Surface.makeRasterN32Premul(width, height);
        canvas = surface.getCanvas();
    }

    public static void draw(Consumer<Canvas> callback) {
        if (canvas != null) {
            canvas.clear(0);
            callback.accept(canvas);
        }
    }

    public static Surface getSurface() {
        return surface;
    }
}
