package dev.miitong.eliminate.mixin.client;

import dev.miitong.eliminate.client.EliminateClient;
import dev.miitong.eliminate.client.util.CullingUtils;
import dev.miitong.eliminate.config.EliminateConfig;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.chunk.Octree;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.Box;
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
        if (!useCulling) return;
        if (near) return;
        if (EliminateClient.isIrisShadowPass(frameIndex)) return;

        Box box = new Box(this.box.getMinX(), this.box.getMinY(), this.box.getMinZ(), this.box.getMaxX(), this.box.getMaxY(), this.box.getMaxZ());
        int x = this.box.getMinX() >> 4;
        int y = this.box.getMinY() >> 4;
        int z = this.box.getMinZ() >> 4;

        if (CullingUtils.shouldCull(box, x, y, z)) {
            ci.cancel();
        }
    }
}
