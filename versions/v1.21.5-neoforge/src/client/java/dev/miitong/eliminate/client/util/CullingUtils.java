package dev.miitong.eliminate.client.util;

import dev.miitong.eliminate.client.EliminateClient;
import dev.miitong.eliminate.config.EliminateConfig;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.*;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

public class CullingUtils {
    private static ClientWorld cachedWorld;
    private static final Long2IntOpenHashMap cachedHeights = new Long2IntOpenHashMap();
    private static final Long2BooleanOpenHashMap cachedTransparency = new Long2BooleanOpenHashMap();
    private static final Long2IntOpenHashMap cachedBiomeAdjustments = new Long2IntOpenHashMap();
    private static long lastCacheTime = -1;
    private static int cachedPlayerSurfaceY;
    private static int cachedPlayerCeilingY;
    private static boolean cachedPlayerUnderground;
    private static boolean isNether;
    private static double fovCosineThreshold;
    private static long lastFpsUpdateTime = -1;
    private static double currentFps = 0.0;
    private static int dynamicDistanceAdjusted = 32;

    static {
        cachedHeights.defaultReturnValue(-1);
    }

    public static void resetCache() {
        cachedHeights.clear();
        cachedTransparency.clear();
        cachedBiomeAdjustments.clear();
        cachedWorld = null;
        lastCacheTime = -1;
        lastFpsUpdateTime = -1;
        dynamicDistanceAdjusted = 32;
    }

    public static boolean shouldCull(Box box, int chunkX, int chunkY, int chunkZ) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return false;

        EliminateConfig config = EliminateConfig.getInstance();
        if (!config.enabled) return false;
        
        if (config.debugMode) {
            EliminateClient.TOTAL_CHECKED++;
            EliminateClient.HUD_TOTAL_CHECKED++;
        }

        long currentTime = client.world.getTime();
        boolean shouldUpdate;
        if (config.syncWithSodium) {
            shouldUpdate = (currentTime != lastCacheTime);
        } else {
            int interval = Math.max(1, 21 - config.updateSpeed);
            shouldUpdate = (currentTime - lastCacheTime >= interval || lastCacheTime == -1);
        }

        if (shouldUpdate || cachedWorld != client.world) {
            if (cachedWorld != client.world) {
                resetCache();
                cachedWorld = client.world;
                isNether = client.world.getRegistryKey().getValue().getPath().contains("nether");
            }
            lastCacheTime = currentTime;
            int playerX = MathHelper.floor(client.player.getX());
            int playerZ = MathHelper.floor(client.player.getZ());
            cachedPlayerSurfaceY = getReliableSurfaceY(client.world, playerX, playerZ);
            cachedPlayerUnderground = client.player.getEyeY() < (double) (cachedPlayerSurfaceY - 4);
            
            if (isNether) {
                cachedPlayerCeilingY = 120; // Default Nether ceiling safe margin
            }

            // Calculate FOV threshold: 
            // Default FOV is usually 70. We add a 20-degree safety margin.
            double fov = client.options.getFov().getValue();
            double halfFovRad = Math.toRadians((fov + 20) / 2.0);
            fovCosineThreshold = Math.cos(halfFovRad);

            // Update dynamic culling distance based on FPS
            if (config.dynamicCullingDistance) {
                updateDynamicCullingDistance(client);
            }

            if (config.debugMode) {
                EliminateClient.debugCachedSurfaceY = cachedPlayerSurfaceY;
                EliminateClient.debugCachedUnderground = cachedPlayerUnderground;
            }
        }

        double cameraX = client.player.getX();
        double cameraY = client.player.getEyeY();
        double cameraZ = client.player.getZ();

        // 1. Transparency Awareness (Water, Glass, etc.)
        if (config.transparencyAwareness) {
            if (isChunkTransparent(client.world, chunkX, chunkY, chunkZ)) {
                return false; 
            }
        }
        
        // 2. Horizontal Culling
        if (shouldCullHorizontally(box, chunkX, chunkY, chunkZ, client.world, cameraX, cameraY, cameraZ)) {
            if (config.debugMode) {
                EliminateClient.CULLED_COUNT++;
                EliminateClient.CULLED_FOV++;
                EliminateClient.HUD_CULLED_COUNT++;
                EliminateClient.HUD_CULLED_FOV++;
            }
            return true;
        }

        // 2. FOV Culling
        if (config.fovCullingEnabled) {
            Vec3d look = client.player.getRotationVec(1.0F);
            Vec3d toBox = box.getCenter().subtract(cameraX, cameraY, cameraZ).normalize();
            double dot = look.dotProduct(toBox);
            
            if (dot < fovCosineThreshold) {
                double distSq = box.getCenter().squaredDistanceTo(cameraX, cameraY, cameraZ);
                if (distSq > 256) {
                    if (config.debugMode) {
                        EliminateClient.CULLED_COUNT++;
                        EliminateClient.CULLED_FOV++;
                        EliminateClient.HUD_CULLED_COUNT++;
                        EliminateClient.HUD_CULLED_FOV++;
                    }
                    return true;
                }
            }
        }

        // 3. Aggressive Mountain Culling / Nether Ceiling Culling
        if (config.aggressiveMountainCulling) {
            if (isNether) {
                // If in Nether, cull chunks above ceiling if player is below
                if (cameraY < 110 && box.minY > 128) {
                    if (config.debugMode) {
                        EliminateClient.CULLED_COUNT++;
                        EliminateClient.CULLED_MOUNTAIN++;
                        EliminateClient.HUD_CULLED_COUNT++;
                        EliminateClient.HUD_CULLED_MOUNTAIN++;
                    }
                    return true;
                }
            } else if (cachedPlayerUnderground) {
                int thickness = cachedPlayerSurfaceY - (int)cameraY;
                if (thickness > 12) {
                    if (box.minY > (double) (cachedPlayerSurfaceY + 4)) {
                        double horizontalDistSq = MathHelper.square(box.getCenter().x - cameraX) + MathHelper.square(box.getCenter().z - cameraZ);
                        if (horizontalDistSq > 1024) {
                            if (config.debugMode) {
                                EliminateClient.CULLED_COUNT++;
                                EliminateClient.CULLED_MOUNTAIN++;
                                EliminateClient.HUD_CULLED_COUNT++;
                                EliminateClient.HUD_CULLED_MOUNTAIN++;
                            }
                            return true;
                        }
                    }
                }
            }
        }

        // 4. Standard Vertical Culling
        long key = (((long) chunkX) & 0xffffffffL) | ((((long) chunkZ) & 0xffffffffL) << 32);
        int surfaceY = cachedHeights.get(key);
        if (surfaceY == -1) {
            surfaceY = getReliableSurfaceY(client.world, (chunkX << 4) + 8, (chunkZ << 4) + 8);
            cachedHeights.put(key, surfaceY);
        }

        // Get effective culling distance with dynamic adjustment and biome awareness
        int cullingDist = config.dynamicCullingDistance ? dynamicDistanceAdjusted : config.cullingDistance;
        
        // Apply biome-specific adjustment
        int biomeAdjustment = getBiomeAdjustment(client.world, (chunkX << 4) + 8, (chunkZ << 4) + 8);
        cullingDist = Math.max(8, cullingDist + biomeAdjustment);
        
        if (isNether) {
            // Nether specific: cull if too far above or below
            double diffY = Math.abs(box.getCenter().y - cameraY);
            if (diffY > (double) (cullingDist + 32)) {
                if (config.debugMode) {
                    EliminateClient.CULLED_COUNT++;
                    EliminateClient.CULLED_VERTICAL++;
                    EliminateClient.HUD_CULLED_COUNT++;
                    EliminateClient.HUD_CULLED_VERTICAL++;
                }
                return true;
            }
            return false;
        }

        if (!cachedPlayerUnderground) {
            if (box.maxY < (double) (surfaceY - cullingDist)) {
                if (config.debugMode) {
                    EliminateClient.CULLED_COUNT++;
                    EliminateClient.CULLED_VERTICAL++;
                    EliminateClient.HUD_CULLED_COUNT++;
                    EliminateClient.HUD_CULLED_VERTICAL++;
                }
                return true;
            }
        } else {
            double diffY = Math.abs(box.getCenter().y - cameraY);
            if (diffY > (double) cullingDist) {
                if (config.debugMode) {
                    EliminateClient.CULLED_COUNT++;
                    EliminateClient.CULLED_VERTICAL++;
                    EliminateClient.HUD_CULLED_COUNT++;
                    EliminateClient.HUD_CULLED_VERTICAL++;
                }
                return true;
            }
            if (cameraY < (double) (surfaceY - 8) && box.minY > (double) (surfaceY + 2)) {
                if (config.debugMode) {
                    EliminateClient.CULLED_COUNT++;
                    EliminateClient.CULLED_VERTICAL++;
                    EliminateClient.HUD_CULLED_COUNT++;
                    EliminateClient.HUD_CULLED_VERTICAL++;
                }
                return true;
            }
        }

        return false;
    }

    private static boolean isChunkTransparent(ClientWorld world, int cx, int cy, int cz) {
        long key = (((long) cx) & 0xffffffL) | ((((long) cy) & 0xffffffL) << 24) | ((((long) cz) & 0xffffffL) << 48);
        if (cachedTransparency.containsKey(key)) return cachedTransparency.get(key);

        Chunk chunk = world.getChunk(cx, cz, ChunkStatus.FULL, false);
        if (chunk == null) return false;

        // Quick sampling: check center and corners of the chunk section
        boolean transparent = false;
        int startX = cx << 4;
        int startY = cy << 4;
        int startZ = cz << 4;

        // Check a few points in the 16x16x16 section
        BlockPos.Mutable pos = new BlockPos.Mutable();
        int[] samples = {0, 8, 15};
        outer:
        for (int x : samples) {
            for (int y : samples) {
                for (int z : samples) {
                    pos.set(startX + x, startY + y, startZ + z);
                    BlockState state = chunk.getBlockState(pos);
                    if (state.isOf(Blocks.WATER) || state.isOf(Blocks.GLASS) || state.isOf(Blocks.ICE) || !state.isOpaque()) {
                        transparent = true;
                        break outer;
                    }
                }
            }
        }

        cachedTransparency.put(key, transparent);
        return transparent;
    }

    private static void updateDynamicCullingDistance(MinecraftClient client) {
        long currentTime = client.world.getTime();
        if (currentTime - lastFpsUpdateTime >= 20) { // Update every second
            currentFps = client.fpsDebugString.split(" ")[0];
            try {
                currentFps = Double.parseDouble(currentFps);
            } catch (NumberFormatException e) {
                currentFps = 60.0;
            }
            lastFpsUpdateTime = currentTime;
            
            EliminateConfig config = EliminateConfig.getInstance();
            double fpsRatio = currentFps / config.targetFps;
            
            // Adjust distance based on FPS ratio
            // When FPS is low (< target), increase culling distance
            // When FPS is high (> target), decrease culling distance
            dynamicDistanceAdjusted = (int) Math.round(
                config.cullingDistance + 
                (config.cullingDistance * (1.0 - fpsRatio) * 2.0)
            );
            
            // Clamp to configured limits
            dynamicDistanceAdjusted = Math.max(config.minDynamicDistance, dynamicDistanceAdjusted);
            dynamicDistanceAdjusted = Math.min(config.maxDynamicDistance, dynamicDistanceAdjusted);
        }
    }
    
    private static boolean shouldCullHorizontally(Box box, int chunkX, int chunkY, int chunkZ, ClientWorld world, double cameraX, double cameraY, double cameraZ) {
        EliminateConfig config = EliminateConfig.getInstance();
        if (!config.horizontalCulling) return false;
        
        // Check if chunk is on the same horizontal plane but blocked by terrain
        // For underground chunks, check if they're on the same level but too far horizontally
        if (cachedPlayerUnderground) {
            double horizontalDist = Math.sqrt(MathHelper.square(box.getCenter().x - cameraX) + MathHelper.square(box.getCenter().z - cameraZ));
            int currentChunkY = (int) Math.floor(cameraY / 16.0);
            
            // Only cull if same chunk Y level and too far horizontally
            if (currentChunkY == chunkY) {
                int effectiveDistance = config.dynamicCullingDistance ? dynamicDistanceAdjusted : config.cullingDistance;
                return horizontalDist > (double) (effectiveDistance + 16);
            }
        }
        return false;
    }
    
    private static int getBiomeAdjustment(ClientWorld world, int x, int z) {
        EliminateConfig config = EliminateConfig.getInstance();
        if (!config.biomeAwareCulling) return 0;
        
        long chunkKey = (((long) (x >> 4)) << 32) | ((long) (z >> 4) & 0xffffffffL);
        if (cachedBiomeAdjustments.containsKey(chunkKey)) {
            return cachedBiomeAdjustments.get(chunkKey);
        }
        
        int adjustment = 0;
        Chunk chunk = world.getChunk(x >> 4, z >> 4, ChunkStatus.FULL, false);
        if (chunk != null) {
            RegistryKey<Biome> biomeKey = chunk.getBiomeForNoiseGen(x & 15, 0, z & 15).getKey().orElse(null);
            if (biomeKey != null) {
                String biomeId = biomeKey.getValue().getPath();
                
                // Adjust culling distance based on biome type
                if (biomeId.contains("plains") || biomeId.contains("desert") || biomeId.contains("beach")) {
                    // Flat biomes - more aggressive culling
                    adjustment = -8;
                } else if (biomeId.contains("forest") || biomeId.contains("jungle") || biomeId.contains("swamp")) {
                    // Medium complexity biomes
                    adjustment = 0;
                } else if (biomeId.contains("mountains") || biomeId.contains("hills") || biomeId.contains("mesa")) {
                    // Complex biomes - more conservative culling
                    adjustment = 8;
                } else if (biomeId.contains("ocean") || biomeId.contains("river") || biomeId.contains("lake")) {
                    // Water biomes - more aggressive culling
                    adjustment = -12;
                }
            }
        }
        
        cachedBiomeAdjustments.put(chunkKey, adjustment);
        return adjustment;
    }
    
    public static int getReliableSurfaceY(ClientWorld world, int x, int z) {
        if (world.getChunk(x >> 4, z >> 4, ChunkStatus.FULL, false) == null) {
             return world.getBottomY();
        }

        // Use MOTION_BLOCKING heightmap for efficiency
        return world.getTopY(Heightmap.Type.MOTION_BLOCKING, x, z);
    }
}
