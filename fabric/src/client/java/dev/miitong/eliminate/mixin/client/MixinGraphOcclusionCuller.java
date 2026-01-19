package dev.miitong.eliminate.mixin.client;

import dev.miitong.eliminate.client.EliminateClient;
import dev.miitong.eliminate.config.EliminateConfig;
import net.minecraft.client.renderer.chunk.GraphOcclusionCuller;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GraphOcclusionCuller.class)
public class MixinGraphOcclusionCuller {

    @Inject(method = "isVisible", at = @At("HEAD"), cancellable = true)
    private void onIsVisible(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (!EliminateConfig.getInstance().enabled) return;

        EliminateClient.TOTAL_CHECKED++;
        EliminateClient.HUD_TOTAL_CHECKED++;

        // 垂直剔除
        if (EliminateConfig.getInstance().enabled) {
            double playerY = EliminateClient.debugCachedSurfaceY;
            double blockY = pos.getY();
            double verticalDistance = Math.abs(blockY - playerY);

            if (verticalDistance > EliminateConfig.getInstance().cullingDistance) {
                EliminateClient.CULLED_VERTICAL++;
                EliminateClient.HUD_CULLED_VERTICAL++;
                cir.setReturnValue(false);
                return;
            }
        }

        // FOV剔除
        if (EliminateConfig.getInstance().fovCullingEnabled) {
            // 这里需要实现基于FOV的视锥体剔除
            // 由于需要访问玩家视角信息，这里暂时留空，后续实现
        }
    }
}