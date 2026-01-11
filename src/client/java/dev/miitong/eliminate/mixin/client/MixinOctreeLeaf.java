package dev.miitong.eliminate.mixin.client;

import dev.miitong.eliminate.client.EliminateClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.Octree;
import net.minecraft.util.math.BlockBox;
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
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        double cameraY = client.player.getEyeY();
        // Smart Culling Logic
        // We use the world heightmap to distinguish between "Caves" and "Surface".
        
        Box box = this.chunk.getBoundingBox();

        // Optimization: Fast fail for chunks way above the player (Sky)
        // Reduced threshold from 64 to 32 for more aggressive culling
        if (box.minY > cameraY + 32.0) {
            if (EliminateClient.DEBUG) EliminateClient.CULLED_COUNT++;
            ci.cancel();
            return;
        }

        // Sample 5 points (Center + 4 Corners) to get the true lowest surface height in this chunk.
        // This prevents "holes" on cliffs and allows us to use a tighter safety buffer.
        int minX = (int) box.minX;
        int maxX = (int) box.maxX;
        int minZ = (int) box.minZ;
        int maxZ = (int) box.maxZ;
        
        int y1 = client.world.getTopY(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING, minX, minZ);
        int y2 = client.world.getTopY(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING, maxX, minZ);
        int y3 = client.world.getTopY(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING, minX, maxZ);
        int y4 = client.world.getTopY(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING, maxX, maxZ);
        int y5 = client.world.getTopY(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING, (minX + maxX) / 2, (minZ + maxZ) / 2);
        
        int surfaceY = Math.min(y1, Math.min(y2, Math.min(y3, Math.min(y4, y5))));
        int playerY = (int) cameraY;
        
        boolean shouldCull = false;

        if (playerY >= surfaceY - 5) {
            // Mode 1: Surface / Flying
            // Cull deep caves. 
            // Since we found the MINIMUM surface height, we can be aggressive.
            // Buffer reduced from 16 to 8 blocks.
            if (box.maxY < surfaceY - 8) {
                shouldCull = true;
            }
        } else {
            // Mode 2: Underground / Caving
            // Cull surface (chunks well above player)
            // Reduced threshold from 32 to 24
            if (box.minY > playerY + 24) {
                shouldCull = true;
            }
            // Cull deep void (chunks well below player)
            // Reduced threshold from 32 to 24
            if (box.maxY < playerY - 24) {
                shouldCull = true;
            }
        }

        if (shouldCull) {
             if (EliminateClient.DEBUG) {
                 EliminateClient.CULLED_COUNT++;
             }
             ci.cancel();
        }
    }
}
