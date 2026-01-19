package dev.miitong.eliminate.mixin.client;

import dev.miitong.eliminate.client.EliminateClient;
import dev.miitong.eliminate.config.EliminateConfig;
import net.minecraft.client.renderer.chunk.OctreeLeaf;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OctreeLeaf.class)
public class MixinOctreeLeaf {

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void onShouldRender(Box box, CallbackInfoReturnable<Boolean> cir) {
        if (!EliminateConfig.getInstance().enabled) return;

        // 垂直剔除（与MixinGraphOcclusionCuller保持一致）
        if (EliminateConfig.getInstance().enabled) {
            double playerY = EliminateClient.debugCachedSurfaceY;
            double blockY = box.getCenter().getY();
            double verticalDistance = Math.abs(blockY - playerY);

            if (verticalDistance > EliminateConfig.getInstance().cullingDistance) {
                EliminateClient.CULLED_VERTICAL++;
                EliminateClient.HUD_CULLED_VERTICAL++;
                cir.setReturnValue(false);
                return;
            }
        }

        // 山体/地层激进剔除（与MixinOctreeBranch保持一致）
        if (EliminateConfig.getInstance().aggressiveMountainCulling && EliminateClient.debugCachedUnderground) {
            if (box.maxY > EliminateClient.debugCachedSurfaceY) {
                EliminateClient.CULLED_MOUNTAIN++;
                EliminateClient.HUD_CULLED_MOUNTAIN++;
                cir.setReturnValue(false);
                return;
            }
        }
    }
}