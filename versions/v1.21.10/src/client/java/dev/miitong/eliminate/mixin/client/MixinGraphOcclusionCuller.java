package dev.miitong.eliminate.mixin.client;

import dev.miitong.eliminate.client.EliminateClient;
import dev.miitong.eliminate.client.util.CullingUtils;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.chunk.occlusion.GraphOcclusionCuller", remap = false)
public class MixinGraphOcclusionCuller {

    @Inject(method = "isOccluded", at = @At("HEAD"), cancellable = true)
    public void onIsOccluded(int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (EliminateClient.isRenderingShadowPass()) return;

        double minX = x * 16.0;
        double minY = y * 16.0;
        double minZ = z * 16.0;
        Box box = new Box(minX, minY, minZ, minX + 16.0, minY + 16.0, minZ + 16.0);

        if (CullingUtils.shouldCull(box, x, y, z)) {
            cir.setReturnValue(true);
        }
    }
}
