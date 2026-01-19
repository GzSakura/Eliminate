package dev.miitong.eliminate.mixin.client;

import dev.miitong.eliminate.client.EliminateClient;
import dev.miitong.eliminate.client.util.CullingUtils;
import dev.miitong.eliminate.config.EliminateConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.Octree;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.render.chunk.Octree$Leaf")
public class MixinOctreeLeaf {

    @Shadow @Final private ChunkBuilder.BuiltChunk chunk;

    @Inject(method = "visit", at = @At("HEAD"), cancellable = true)
    private void onVisit(Octree.Visitor visitor, boolean useCulling, Frustum frustum, int depth, int frameIndex, boolean near, CallbackInfo ci) {
        if (!useCulling) return;
        if (EliminateClient.isIrisShadowPass(frameIndex)) return;

        Box box = this.chunk.getBoundingBox();
        int x = (int) (box.minX / 16.0);
        int y = (int) (box.minY / 16.0);
        int z = (int) (box.minZ / 16.0);

        if (CullingUtils.shouldCull(box, x, y, z)) {
            ci.cancel();
        }
    }
}
