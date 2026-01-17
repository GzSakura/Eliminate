package dev.miitong.eliminate.client.util;

import dev.miitong.eliminate.client.EliminateClient;
import dev.miitong.eliminate.config.EliminateConfig;
import dev.miitong.eliminate.client.util.AsyncTaskManager;
import it.unimi.dsi.fastutil.longs.Long2BooleanLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
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
    private static final Long2IntLinkedOpenHashMap cachedHeights = new Long2IntLinkedOpenHashMap();
    private static final Long2BooleanLinkedOpenHashMap cachedTransparency = new Long2BooleanLinkedOpenHashMap();
    private static final int MAX_CACHE_SIZE = 1024;
    private static long lastCacheTime = -1;
    private static int cachedPlayerSurfaceY;
    private static int cachedPlayerCeilingY;
    private static boolean cachedPlayerUnderground;
    private static boolean isNether;
    private static boolean isEnd;
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
    }

    /**
     * Get a cached Box instance to avoid object creation
     */
    public static Box getCachedBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return new Box(minX, minY, minZ, maxX, maxY, maxZ);
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
                String dimensionPath = client.world.getRegistryKey().getValue().getPath();
                isNether = dimensionPath.contains("nether");
                isEnd = dimensionPath.contains("end");
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
            // Consider dynamic FOV
            if (client.player.isSprinting()) {
                fov *= 1.1; // Sprinting increases FOV by 10%
            }
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
            
            // Pre-calculate nearby chunks in background
            if (config.preCalculateChunks) {
                int playerChunkX = playerX >> 4;
                int playerChunkZ = playerZ >> 4;
                int radius = Math.max(1, Math.min(16, config.preCalculateRadius)); // Limit radius between 1-16
                preCalculateNearbyChunks(client.world, playerChunkX, playerChunkZ, radius);
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
            // Calculate center manually to avoid creating temporary Vec3d
            double centerX = (box.minX + box.maxX) * 0.5;
            double centerY = (box.minY + box.maxY) * 0.5;
            double centerZ = (box.minZ + box.maxZ) * 0.5;
            
            // Calculate vector to box center and normalize
            double dx = centerX - cameraX;
            double dy = centerY - cameraY;
            double dz = centerZ - cameraZ;
            double lengthSq = dx * dx + dy * dy + dz * dz;
            double length = Math.sqrt(lengthSq);
            if (length > 0) {
                double normDx = dx / length;
                double normDy = dy / length;
                double normDz = dz / length;
                
                // Calculate dot product manually
                double dot = look.x * normDx + look.y * normDy + look.z * normDz;
                
                if (dot < fovCosineThreshold) {
                    // Use original squared distance
                    if (lengthSq > 256) {
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
                        // Calculate center manually to avoid creating temporary Vec3d
                        double centerX = (box.minX + box.maxX) * 0.5;
                        double centerZ = (box.minZ + box.maxZ) * 0.5;
                        double horizontalDistSq = MathHelper.square(centerX - cameraX) + MathHelper.square(centerZ - cameraZ);
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
        // Move accessed entry to the end (most recently used)
        cachedHeights.getAndMoveToLast(key);
        int surfaceY = cachedHeights.get(key);
        if (surfaceY == -1) {
            if (config.debugMode) {
                EliminateClient.CACHE_MISSES++;
            }
            surfaceY = getReliableSurfaceY(client.world, (chunkX << 4) + 8, (chunkZ << 4) + 8);
            cachedHeights.put(key, surfaceY);
            // Limit cache size - remove oldest entries if necessary
            if (cachedHeights.size() > MAX_CACHE_SIZE) {
                // Remove first entry using iterator
                if (!cachedHeights.isEmpty()) {
                    cachedHeights.remove(cachedHeights.keySet().iterator().next());
                }
            }
        } else {
            if (config.debugMode) {
                EliminateClient.CACHE_HITS++;
            }
        }
        
        // Update cache size
        if (config.debugMode) {
            EliminateClient.CACHE_SIZE = cachedHeights.size() + cachedTransparency.size();
        }

        int cullingDist = config.cullingDistance;
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
        } else if (isEnd && config.endDimensionOptimization) {
            // End specific: more aggressive culling for floating islands
            double centerX = (box.minX + box.maxX) * 0.5;
            double centerY = (box.minY + box.maxY) * 0.5;
            double centerZ = (box.minZ + box.maxZ) * 0.5;
            
            double distSq = MathHelper.square(centerX - cameraX) + MathHelper.square(centerY - cameraY) + MathHelper.square(centerZ - cameraZ);
            
            // More aggressive distance-based culling for End
            if (distSq > MathHelper.square(cullingDist + 48)) {
                if (config.debugMode) {
                    EliminateClient.CULLED_COUNT++;
                    EliminateClient.CULLED_END++;
                    EliminateClient.HUD_CULLED_COUNT++;
                    EliminateClient.HUD_CULLED_END++;
                }
                return true;
            }
            
            // Special culling for End: cull chunks that are too far vertically when not looking up/down
            Vec3d look = client.player.getRotationVec(1.0F);
            double verticalDot = Math.abs(look.y);
            if (verticalDot < 0.3) { // Not looking up or down
                double diffY = Math.abs(centerY - cameraY);
                if (diffY > (double) (cullingDist + 24)) {
                    if (config.debugMode) {
                        EliminateClient.CULLED_COUNT++;
                        EliminateClient.CULLED_END++;
                        EliminateClient.HUD_CULLED_COUNT++;
                        EliminateClient.HUD_CULLED_END++;
                    }
                    return true;
                }
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
        if (cachedTransparency.containsKey(key)) {
            // Move accessed entry to the end (most recently used)
            cachedTransparency.getAndMoveToLast(key);
            return cachedTransparency.get(key);
        }

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
                    if (state.isOf(Blocks.WATER) || state.isOf(Blocks.GLASS) || state.isOf(Blocks.ICE) || 
                        state.isOf(Blocks.GLASS_PANE) || state.isOf(Blocks.SEA_LANTERN) || 
                        state.isOf(Blocks.SLIME_BLOCK) || !state.isOpaque()) {
                        transparent = true;
                        break outer;
                    }
                }
            }
        }

        cachedTransparency.put(key, transparent);
        // Limit cache size - remove oldest entries if necessary
            if (cachedTransparency.size() > MAX_CACHE_SIZE) {
                // Remove first entry using iterator
                if (!cachedTransparency.isEmpty()) {
                    cachedTransparency.remove(cachedTransparency.keySet().iterator().next());
                }
            }
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

        // Smart surface detection algorithm
        // First try MOTION_BLOCKING heightmap for efficiency
        int motionBlockingY = world.getTopY(Heightmap.Type.MOTION_BLOCKING, x, z);
        
        // Then verify with a more accurate check
        int accurateY = findAccurateSurfaceY(world, x, z, motionBlockingY);
        
        // Use the higher value to ensure we don't miss any surface
        return Math.max(motionBlockingY, accurateY);
    }

    /**
     * Find more accurate surface Y by checking actual block states
     */
    private static int findAccurateSurfaceY(ClientWorld world, int x, int z, int startY) {
        int maxY = world.getTopY(Heightmap.Type.WORLD_SURFACE, x, z);
        int minY = world.getBottomY();
        
        // Start from the motion blocking height and check upwards
        for (int y = startY; y <= maxY; y++) {
            BlockState state = world.getBlockState(new BlockPos(x, y, z));
            if (!state.isAir() && !state.isLiquid()) {
                return y;
            }
        }
        
        // If no solid block found above, check below
        for (int y = startY - 1; y >= minY; y--) {
            BlockState state = world.getBlockState(new BlockPos(x, y, z));
            if (!state.isAir() && !state.isLiquid()) {
                return y;
            }
        }
        
        return startY;
    }

    /**
     * Pre-calculate surface heights and transparency for nearby chunks in background threads
     */
    private static void preCalculateNearbyChunks(ClientWorld world, int centerX, int centerZ, int radius) {
        AsyncTaskManager.submit(() -> {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dz * dz > radius * radius) continue;
                    
                    final int chunkX = centerX + dx;
                    final int chunkZ = centerZ + dz;
                    
                    // Pre-calculate surface height
                    long heightKey = (((long) chunkX) & 0xffffffffL) | ((((long) chunkZ) & 0xffffffffL) << 32);
                    if (!cachedHeights.containsKey(heightKey)) {
                        int surfaceY = getReliableSurfaceY(world, (chunkX << 4) + 8, (chunkZ << 4) + 8);
                        synchronized (cachedHeights) {
                            if (!cachedHeights.containsKey(heightKey)) {
                                cachedHeights.put(heightKey, surfaceY);
                                // Limit cache size
                                if (cachedHeights.size() > MAX_CACHE_SIZE) {
                                    // Remove first entry using iterator
                                    if (!cachedHeights.isEmpty()) {
                                        cachedHeights.remove(cachedHeights.keySet().iterator().next());
                                    }
                                }
                            }
                        }
                    }
                    
                    // Pre-calculate transparency for a few Y levels
                    for (int cy = 0; cy < 16; cy++) {
                        long transKey = (((long) chunkX) & 0xffffffL) | ((((long) cy) & 0xffffffL) << 24) | ((((long) chunkZ) & 0xffffffL) << 48);
                        if (!cachedTransparency.containsKey(transKey)) {
                            boolean transparent = isChunkTransparentImpl(world, chunkX, cy, chunkZ);
                            synchronized (cachedTransparency) {
                                if (!cachedTransparency.containsKey(transKey)) {
                                    cachedTransparency.put(transKey, transparent);
                                    // Limit cache size
                                    if (cachedTransparency.size() > MAX_CACHE_SIZE) {
                                        // Remove first entry using iterator
                                        if (!cachedTransparency.isEmpty()) {
                                            cachedTransparency.remove(cachedTransparency.keySet().iterator().next());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * Internal implementation of isChunkTransparent without cache management
     */
    private static boolean isChunkTransparentImpl(ClientWorld world, int cx, int cy, int cz) {
        Chunk chunk = world.getChunk(cx, cz, ChunkStatus.FULL, false);
        if (chunk == null) return false;

        boolean transparent = false;
        int startX = cx << 4;
        int startY = cy << 4;
        int startZ = cz << 4;

        BlockPos.Mutable pos = new BlockPos.Mutable();
        int[] samples = {0, 8, 15};
        outer:
        for (int x : samples) {
            for (int y : samples) {
                for (int z : samples) {
                    pos.set(startX + x, startY + y, startZ + z);
                    BlockState state = chunk.getBlockState(pos);
                    if (state.isOf(Blocks.WATER) || state.isOf(Blocks.GLASS) || state.isOf(Blocks.ICE) || 
                        state.isOf(Blocks.GLASS_PANE) || state.isOf(Blocks.SEA_LANTERN) || 
                        state.isOf(Blocks.SLIME_BLOCK) || !state.isOpaque()) {
                        transparent = true;
                        break outer;
                    }
                }
            }
        }

        return transparent;
    }
}
