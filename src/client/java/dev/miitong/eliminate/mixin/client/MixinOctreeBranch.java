package dev.miitong.eliminate.mixin.client;

import dev.miitong.eliminate.client.EliminateClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.chunk.Octree;
import net.minecraft.util.math.BlockBox;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.render.chunk.Octree$Branch")
public class MixinOctreeBranch {

    @Shadow @Final private BlockBox box;

    @Inject(method = "visit", at = @At("HEAD"), cancellable = true)
    private void onVisit(Octree.Visitor visitor, boolean useCulling, Frustum frustum, int depth, int frameIndex, boolean near, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        double cameraY = client.player.getEyeY();
        double minSafe = cameraY - 512.0;
        double maxSafe = cameraY + 512.0;

        if (dev.miitong.eliminate.client.EliminateClient.DEBUG) {
            dev.miitong.eliminate.client.EliminateClient.TOTAL_CHECKED++;
        }

        if (this.box.getMaxY() < minSafe || this.box.getMinY() > maxSafe) {
            if (dev.miitong.eliminate.client.EliminateClient.DEBUG) {
                dev.miitong.eliminate.client.EliminateClient.CULLED_COUNT++;
            }
            ci.cancel();
        }
    }
}
