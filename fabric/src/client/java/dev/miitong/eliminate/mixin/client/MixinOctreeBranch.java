package dev.miitong.eliminate.mixin.client;

import dev.miitong.eliminate.client.EliminateClient;
import dev.miitong.eliminate.config.EliminateConfig;
import net.minecraft.client.renderer.chunk.OctreeBranch;
import net.minecraft.client.renderer.chunk.OctreeNode;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OctreeBranch.class)
public class MixinOctreeBranch {

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void onShouldRender(Box box, CallbackInfoReturnable<Boolean> cir) {
        if (!EliminateConfig.getInstance().enabled) return;

        // 山体/地层激进剔除
        if (EliminateConfig.getInstance().aggressiveMountainCulling && EliminateClient.debugCachedUnderground) {
            // 当玩家在地下时，剔除地表以上的区块
            if (box.maxY > EliminateClient.debugCachedSurfaceY) {
                EliminateClient.CULLED_MOUNTAIN++;
                EliminateClient.HUD_CULLED_MOUNTAIN++;
                cir.setReturnValue(false);
                return;
            }
        }

        // 下界专属适配
        if (EliminateClient.debugCachedUnderground) {
            // 这里可以添加下界专属的剔除逻辑
        }
    }
}