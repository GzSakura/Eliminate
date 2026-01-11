package dev.miitong.eliminate.mixin.client;

import dev.miitong.eliminate.client.EliminateClient;
import dev.miitong.eliminate.config.EliminateConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.Octree;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.render.chunk.Octree$Leaf")
public class MixinOctreeLeaf {

    private static net.minecraft.client.world.ClientWorld cachedWorld;
    private static final java.util.HashMap<Long, Integer> cachedHeights = new java.util.HashMap<>();

    private static int cachedPlayerFrameIndex = Integer.MIN_VALUE;
    private static int cachedPlayerSurfaceY;
    private static boolean cachedPlayerUnderground;

    @Shadow @Final private ChunkBuilder.BuiltChunk chunk;

    private int getReliableSurfaceY(net.minecraft.client.world.ClientWorld world, int x, int z) {
        // Force scan from world top to ensure we find the true surface
        // Check if chunk is loaded first to avoid scanning void
        if (world.getChunk(x >> 4, z >> 4, net.minecraft.world.chunk.ChunkStatus.FULL, false) == null) {
             return world.getBottomY();
        }

        int bottomY = world.getBottomY();
        net.minecraft.util.math.BlockPos.Mutable pos = new net.minecraft.util.math.BlockPos.Mutable(x, bottomY, z);

        // Safety check: if the bottom of the world is Air, it's likely a Void world or Floating Islands.
        // In this case, we cannot safely assume "underground" exists, so we return bottomY to disable vertical culling for this column.
        if (world.getBlockState(pos).isAir()) {
            return bottomY;
        }

        int startY = bottomY + world.getHeight() - 1;
        pos.setY(startY);
        
        for (int y = startY; y > bottomY; y--) {
            pos.setY(y);
            net.minecraft.block.BlockState state = world.getBlockState(pos);
            if (state.blocksMovement() && !state.isLiquid()) {
                return y;
            }
        }
        return bottomY;
    }

    @Inject(method = "visit", at = @At("HEAD"), cancellable = true)
    private void onVisit(Octree.Visitor visitor, boolean useCulling, Frustum frustum, int depth, int frameIndex, boolean near, CallbackInfo ci) {
        EliminateConfig config = EliminateConfig.getInstance();
        if (!config.enabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        if (client.world == null) return;
        if (!useCulling) return;

        if (EliminateClient.isIrisShadowPass(frameIndex)) return;

        if (config.debugMode) EliminateClient.TOTAL_CHECKED++;

        if (cachedWorld != client.world) {
            cachedWorld = client.world;
            cachedHeights.clear();
            cachedPlayerFrameIndex = Integer.MIN_VALUE;
        }

        Box box = this.chunk.getBoundingBox();

        double cameraX = client.player.getX();
        double cameraZ = client.player.getZ();
        double cameraY = client.player.getEyeY();

        if (!near) {
            Vec3d look = client.player.getRotationVec(1.0F);
            if (Math.abs(look.y) <= 0.5) {
                double forwardX = look.x;
                double forwardZ = look.z;
            double forwardLen = Math.hypot(forwardX, forwardZ);
            if (forwardLen >= 1.0E-6) {
                forwardX /= forwardLen;
                forwardZ /= forwardLen;

                double testX = forwardX >= 0.0 ? box.maxX : box.minX;
                double testZ = forwardZ >= 0.0 ? box.maxZ : box.minZ;
                double dxTest = testX - cameraX;
                double dzTest = testZ - cameraZ;
                double maxDot = dxTest * forwardX + dzTest * forwardZ;
                double testDist = Math.hypot(dxTest, dzTest);
                if (testDist >= 1.0E-6 && maxDot < -0.35 * testDist) {
                    if (config.debugMode) {
                        EliminateClient.CULLED_COUNT++;
                        EliminateClient.CULLED_BACK++;
                    }
                    ci.cancel();
                    return;
                }
            }
            }
        }

        int chunkX = ((int) box.minX) >> 4;
        int chunkZ = ((int) box.minZ) >> 4;
        long key = (((long) chunkX) & 0xffffffffL) | ((((long) chunkZ) & 0xffffffffL) << 32);

        Integer packed = cachedHeights.get(key);
        int surfaceY;
        int floorY;
        if (packed == null) {
            int baseX = chunkX << 4;
            int baseZ = chunkZ << 4;

            int[] xs = new int[] { baseX + 2, baseX + 8, baseX + 14 };
            int[] zs = new int[] { baseZ + 2, baseZ + 8, baseZ + 14 };

            int minSurface = Integer.MAX_VALUE;
            int minFloor = Integer.MAX_VALUE;
            for (int x : xs) {
                for (int z : zs) {
                    int s = getReliableSurfaceY(client.world, x, z);
                    // Use reliable scan for floor as well since heightmaps are unreliable
                    int f = s; 
                    if (s < minSurface) minSurface = s;
                    if (f < minFloor) minFloor = f;
                }
            }

            surfaceY = minSurface;
            floorY = minFloor;
            int packedSurface = ((short) surfaceY) & 0xffff;
            int packedFloor = (((short) floorY) & 0xffff) << 16;
            cachedHeights.put(key, packedSurface | packedFloor);
        } else {
            surfaceY = (short) (packed & 0xffff);
            floorY = (short) ((packed >>> 16) & 0xffff);
        }

        boolean shouldCull;

        int cullingDist = config.cullingDistance;
        int cutoffY = floorY - cullingDist;

        if (cachedPlayerFrameIndex != frameIndex) {
            cachedPlayerFrameIndex = frameIndex;
            int playerX = MathHelper.floor(client.player.getX());
            int playerZ = MathHelper.floor(client.player.getZ());
            cachedPlayerSurfaceY = getReliableSurfaceY(client.world, playerX, playerZ);
            cachedPlayerUnderground = cameraY < (double) (cachedPlayerSurfaceY - 5);
            
            if (config.debugMode) {
                EliminateClient.debugCachedSurfaceY = cachedPlayerSurfaceY;
                EliminateClient.debugCachedUnderground = cachedPlayerUnderground;
            }
        }

        if (!cachedPlayerUnderground) {
            shouldCull = box.maxY < cutoffY;
        } else {
            int halfDist = cullingDist / 2;
            shouldCull = box.minY > cameraY + (double) halfDist || box.maxY < cameraY - (double) halfDist;
        }

        if (shouldCull) {
            if (config.debugMode) {
                EliminateClient.CULLED_COUNT++;
                EliminateClient.CULLED_VERTICAL++;
            }
            ci.cancel();
        }
    }
}
