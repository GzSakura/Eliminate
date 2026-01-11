package dev.miitong.eliminate.mixin.client;

import dev.miitong.eliminate.client.EliminateClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.chunk.Octree;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.Vec3d;
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
        if (!useCulling) return;

        if (EliminateClient.isIrisShadowPass(frameIndex)) return;

        if (EliminateClient.DEBUG) EliminateClient.TOTAL_CHECKED++;

        if (near) return;

        double cameraX = client.player.getX();
        double cameraZ = client.player.getZ();

        Vec3d look = client.player.getRotationVec(1.0F);
        // Relaxed threshold to 0.5 to prevent holes when looking down
        if (Math.abs(look.y) > 0.5) return;

        double forwardX = look.x;
        double forwardZ = look.z;
        double forwardLen = Math.hypot(forwardX, forwardZ);
        if (forwardLen < 1.0E-6) return;
        forwardX /= forwardLen;
        forwardZ /= forwardLen;

        double testX = forwardX >= 0.0 ? this.box.getMaxX() : this.box.getMinX();
        double testZ = forwardZ >= 0.0 ? this.box.getMaxZ() : this.box.getMinZ();
        double dxTest = testX - cameraX;
        double dzTest = testZ - cameraZ;
        double maxDot = dxTest * forwardX + dzTest * forwardZ;

        double testDist = Math.hypot(dxTest, dzTest);
        if (testDist < 1.0E-6) return;
        if (maxDot < -0.35 * testDist) {
            if (EliminateClient.DEBUG) {
                EliminateClient.CULLED_COUNT++;
                EliminateClient.CULLED_BACK++;
            }
            ci.cancel();
        }
    }
}
