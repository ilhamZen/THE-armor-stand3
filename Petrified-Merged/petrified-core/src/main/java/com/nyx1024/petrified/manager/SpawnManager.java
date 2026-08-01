package com.nyx1024.petrified.manager;

import com.nyx1024.petrified.config.ConfigManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

/**
 * Manages natural and manual spawning of angels.
 */
public class SpawnManager {

    private final Plugin plugin;
    private final ConfigManager config;
    private final Random random = new Random();

    public SpawnManager(@NotNull Plugin plugin, @NotNull ConfigManager config) {
        this.plugin = plugin;
        this.config = config;
    }

    /**
     * Find a valid spawn location near a player.
     */
    @Nullable
    public Location findSpawnLocation(@NotNull Location center, double radius) {
        World world = center.getWorld();
        if (world == null) return null;

        int attempts = 50;
        for (int i = 0; i < attempts; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double dist = random.nextDouble() * radius;
            int x = center.getBlockX() + (int) (Math.cos(angle) * dist);
            int z = center.getBlockZ() + (int) (Math.sin(angle) * dist);
            
            Location loc = getSafeSpawnLocation(world, x, z);
            if (loc != null && !isPlayerVisible(center, loc)) {
                return loc;
            }
        }
        return null;
    }

    /**
     * Get a safe spawn location at given X,Z.
     */
    @Nullable
    private Location getSafeSpawnLocation(@NotNull World world, int x, int z) {
        int y = world.getHighestBlockYAt(x, z);
        
        // Check vertical clearance
        int clearance = 0;
        for (int dy = 0; dy < 3; dy++) {
            Block block = world.getBlockAt(x, y + dy, z);
            if (block.getType().isSolid()) break;
            clearance++;
        }
        
        if (clearance < 2) return null;
        
        // Check ground is solid
        Block ground = world.getBlockAt(x, y - 1, z);
        if (!ground.getType().isSolid()) return null;
        
        Location loc = new Location(world, x + 0.5, y, z + 0.5);
        
        // Check light level for natural spawning
        int light = world.getBlockAt(x, y, z).getLightLevel();
        if (light > config.getInt("natural-spawning.light-max", 7)) {
            return null;
        }
        
        return loc;
    }

    /**
     * Check if location is visible from another location.
     */
    private boolean isPlayerVisible(@NotNull Location from, @NotNull Location to) {
        // Simple distance check - more sophisticated LOS in angel logic
        return from.distance(to) < 8;
    }

    /**
     * Count free blocks in area (for cramped/open detection).
     */
    public int countFreeBlocks(@NotNull Location center, int radiusX, int radiusY, int radiusZ) {
        int count = 0;
        World world = center.getWorld();
        if (world == null) return 0;
        
        for (int dx = -radiusX; dx <= radiusX; dx++) {
            for (int dy = 0; dy < radiusY; dy++) {
                for (int dz = -radiusZ; dz <= radiusZ; dz++) {
                    Block block = world.getBlockAt(
                        center.getBlockX() + dx,
                        center.getBlockY() + dy,
                        center.getBlockZ() + dz
                    );
                    if (!block.getType().isSolid() && block.getType() != Material.AIR) {
                        count++;
                    } else if (block.getType() == Material.AIR) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    /**
     * Determine if area is cramped.
     */
    public boolean isCramped(@NotNull Location loc) {
        int free = countFreeBlocks(loc, 1, 3, 1);
        return free < config.getInt("spawning.cramped-threshold", 15);
    }

    /**
     * Determine if area is open.
     */
    public boolean isOpen(@NotNull Location loc) {
        int free = countFreeBlocks(loc, 2, 3, 2);
        int clearance = getVerticalClearance(loc);
        return free >= config.getInt("spawning.open-threshold", 40) && clearance >= 3;
    }

    /**
     * Get vertical clearance at location.
     */
    public int getVerticalClearance(@NotNull Location loc) {
        World world = loc.getWorld();
        if (world == null) return 0;
        
        int clearance = 0;
        for (int dy = 0; dy < 5; dy++) {
            Block block = world.getBlockAt(
                loc.getBlockX(),
                loc.getBlockY() + dy,
                loc.getBlockZ()
            );
            if (!block.getType().isSolid()) {
                clearance++;
            } else {
                break;
            }
        }
        return clearance;
    }

    /**
     * Generate cluster spawn locations.
     */
    @NotNull
    public List<Location> generateCluster(@NotNull Location center, int min, int max, double radius) {
        List<Location> locations = new ArrayList<>();
        int count = min + random.nextInt(max - min + 1);
        
        for (int i = 0; i < count; i++) {
            Location loc = findSpawnLocation(center, radius);
            if (loc != null) {
                locations.add(loc);
            }
        }
        
        return locations;
    }
}
