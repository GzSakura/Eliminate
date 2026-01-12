package dev.miitong.eliminate.mixin.client;

import dev.miitong.eliminate.client.EliminateClient;
import dev.miitong.eliminate.config.EliminateConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.chunk.occlusion.OcclusionCuller", remap = false)
public class MixinGraphOcclusionCuller {

    @Unique
    private static long lastCacheTime = -1;
    @Unique
    private static int cachedPlayerSurfaceY;
    @Unique
    private static boolean cachedPlayerUnderground;

    @Unique
    private static net.minecraft.client.world.ClientWorld cachedWorld;
    @Unique
    private static final java.util.HashMap<Long, Integer> cachedHeights = new java.util.HashMap<>();
    
    @Unique
    private static int getReliableSurfaceY(net.minecraft.client.world.ClientWorld world, int x, int z) {
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

    @Unique
    private static int lastFrameId = -1;

    @Inject(method = "isWithinFrustum", at = @At("HEAD"), cancellable = true)
    private static void onIsWithinFrustum(net.caffeinemc.mods.sodium.client.render.viewport.Viewport viewport, net.caffeinemc.mods.sodium.client.render.chunk.RenderSection section, CallbackInfoReturnable<Boolean> cir) {
        EliminateConfig config = EliminateConfig.getInstance();
        if (!config.enabled) return;

        int x = section.getChunkX();
        int y = section.getChunkY();
        int z = section.getChunkZ();

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;
        
        // Disable culling during shadow pass to prevent shadow artifacts
        if (EliminateClient.isRenderingShadowPass()) return;

        // Follow Sodium's update frame if enabled
        boolean shouldUpdate;
        if (config.syncWithSodium) {
            // Viewport doesn't have a direct getFrameId() in current Sodium version, 
            // but we can use the world time as a proxy for frame-level sync or 
            // fallback to tick-based if needed. Actually, we can just use the tick-based logic
            // with interval 1 for "sync" since Sodium's culler is usually tick/frame bound anyway.
            long currentTime = client.world.getTime();
            shouldUpdate = (currentTime != lastCacheTime);
            if (shouldUpdate) lastCacheTime = currentTime;
        } else {
            long currentTime = client.world.getTime();
            int interval = Math.max(1, 21 - config.updateSpeed);
            shouldUpdate = (currentTime - lastCacheTime >= interval || lastCacheTime == -1);
            if (shouldUpdate) lastCacheTime = currentTime;
        }

        if (shouldUpdate) {
            int playerX = MathHelper.floor(client.player.getX());
            int playerZ = MathHelper.floor(client.player.getZ());
            cachedPlayerSurfaceY = getReliableSurfaceY(client.world, playerX, playerZ);
            cachedPlayerUnderground = client.player.getEyeY() < (double) (cachedPlayerSurfaceY - 5);
        }
        
        if (config.debugMode) {
             EliminateClient.TOTAL_CHECKED++;
             EliminateClient.HUD_TOTAL_CHECKED++;
             EliminateClient.debugCachedSurfaceY = cachedPlayerSurfaceY;
             EliminateClient.debugCachedUnderground = cachedPlayerUnderground;
         }

         if (cachedWorld != client.world) {
             cachedWorld = client.world;
             cachedHeights.clear();
             lastCacheTime = -1;
             lastFrameId = -1;
         }

         int currentSurfaceY = cachedPlayerSurfaceY;
        boolean isUnderground = cachedPlayerUnderground;

        // Reconstruct Box for distance checks
        double minX = x * 16.0;
        double minY = y * 16.0;
        double minZ = z * 16.0;
        Box box = new Box(minX, minY, minZ, minX + 16.0, minY + 16.0, minZ + 16.0);

        double cameraX = client.player.getX();
        double cameraZ = client.player.getZ();
        double cameraY = client.player.getEyeY();

        // Back Culling
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
                        EliminateClient.HUD_CULLED_COUNT++;
                        EliminateClient.HUD_CULLED_BACK++;
                    }
                    cir.setReturnValue(false); // Occluded/Out -> return false for isWithinFrustum
                    return;
                }
            }
        }

        // Vertical Culling
        long key = (((long) x) & 0xffffffffL) | ((((long) z) & 0xffffffffL) << 32);
        Integer packed = cachedHeights.get(key);
        int surfaceY;
        int floorY;

        if (packed == null) {
            int baseX = x << 4;
            int baseZ = z << 4;
            int[] xs = new int[] { baseX + 2, baseX + 8, baseX + 14 };
            int[] zs = new int[] { baseZ + 2, baseZ + 8, baseZ + 14 };

            int minSurface = Integer.MAX_VALUE;
            int minFloor = Integer.MAX_VALUE;
            for (int tx : xs) {
                for (int tz : zs) {
                    int s = getReliableSurfaceY(client.world, tx, tz);
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

        int cullingDist = config.cullingDistance;
        int cutoffY = floorY - cullingDist;
        boolean shouldCull;
        
        if (!isUnderground) {
            shouldCull = box.maxY < cutoffY;
        } else {
            int halfDist = cullingDist / 2;
            shouldCull = box.minY > cameraY + halfDist || box.maxY < cameraY - halfDist;
        }

        if (shouldCull) {
            if (config.debugMode) {
                EliminateClient.CULLED_COUNT++;
                EliminateClient.CULLED_VERTICAL++;
                EliminateClient.HUD_CULLED_COUNT++;
                EliminateClient.HUD_CULLED_VERTICAL++;
            }
            cir.setReturnValue(false); // Culled -> Not within frustum
        }
    }
}
