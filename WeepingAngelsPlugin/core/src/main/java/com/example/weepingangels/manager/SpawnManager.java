package com.example.weepingangels.manager;

import com.example.weepingangels.config.AngelConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Handles natural and commanded spawning of Weeping Angels,
 * including cramped/open detection and cluster spawning.
 */
public class SpawnManager {

    private static final Logger LOGGER = Logger.getLogger("WeepingAngels");

    private final JavaPlugin plugin;
    private final AngelConfig config;
    private final AngelManager angelManager;
    private final Random random = new Random();
    private int spawnTaskId = -1;

    public SpawnManager(JavaPlugin plugin, AngelConfig config, AngelManager angelManager) {
        this.plugin = plugin;
        this.config = config;
        this.angelManager = angelManager;
    }

    public void startNaturalSpawning() {
        if (!config.isNaturalSpawningEnabled()) {
            return;
        }
        int interval = config.getSpawnAttemptIntervalTicks();
        spawnTaskId = Bukkit.getScheduler().runTaskTimer(plugin, this::attemptNaturalSpawn,
                interval, interval).getTaskId();
    }

    public void stopNaturalSpawning() {
        if (spawnTaskId != -1) {
            Bukkit.getScheduler().cancelTask(spawnTaskId);
            spawnTaskId = -1;
        }
    }

    private void attemptNaturalSpawn() {
        if (!config.isEnabled() || !config.isNaturalSpawningEnabled()) {
            return;
        }
        if (angelManager.getActiveCount() >= config.getMaxAngels()) {
            return;
        }
        if (random.nextInt(100) >= config.getSpawnChancePercent()) {
            return;
        }

        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        if (players.isEmpty()) {
            return;
        }

        List<Player> playerList = new ArrayList<>(players);
        Player targetPlayer = playerList.get(random.nextInt(playerList.size()));
        World world = targetPlayer.getWorld();

        Location candidate = findCandidateLocation(targetPlayer, world);
        if (candidate == null) {
            return;
        }

        int lightLevel = world.getBlockAt(candidate).getLightLevel();
        if (lightLevel < config.getMinLightLevel() || lightLevel > config.getMaxLightLevel()) {
            return;
        }

        spawnAtLocation(candidate, world);
    }

    public void spawnAtLocation(Location location, World world) {
        if (angelManager.getActiveCount() >= config.getMaxAngels()) {
            return;
        }

        boolean cramped = isCramped(location, world);
        boolean open = isOpen(location, world);

        if (open) {
            spawnCluster(location, world);
        } else {
            // Cramped or neither: spawn exactly one
            if (isValidSpawnPoint(location, world)) {
                angelManager.spawnAngel(location);
            }
        }
    }

    private void spawnCluster(Location center, World world) {
        int clusterMin = config.getClusterMin();
        int clusterMax = config.getClusterMax();
        int clusterRadius = config.getClusterRadius();
        int count = clusterMin + random.nextInt(Math.max(1, clusterMax - clusterMin + 1));

        int spawned = 0;
        for (int i = 0; i < count; i++) {
            if (angelManager.getActiveCount() >= config.getMaxAngels()) {
                break;
            }
            double offsetX = (random.nextDouble() * 2 - 1) * clusterRadius;
            double offsetZ = (random.nextDouble() * 2 - 1) * clusterRadius;
            Location clusterLoc = center.clone().add(offsetX, 0, offsetZ);
            clusterLoc.setY(center.getY());

            // Snap to ground
            clusterLoc = snapToGround(clusterLoc, world);
            if (clusterLoc != null && isValidSpawnPoint(clusterLoc, world)) {
                angelManager.spawnAngel(clusterLoc);
                spawned++;
            }
        }

        // If no cluster points were valid, try the original
        if (spawned == 0 && isValidSpawnPoint(center, world)) {
            angelManager.spawnAngel(center);
        }
    }

    private Location findCandidateLocation(Player player, World world) {
        int searchRadius = config.getPlayerSearchRadius();
        for (int attempt = 0; attempt < 10; attempt++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double distance = 8 + random.nextDouble() * (searchRadius - 8);
            double offsetX = Math.cos(angle) * distance;
            double offsetZ = Math.sin(angle) * distance;

            Location candidate = player.getLocation().clone().add(offsetX, 0, offsetZ);
            candidate.setY(world.getHighestBlockYAt(candidate.getBlockX(), candidate.getBlockZ()));

            if (candidate.getBlockY() > world.getMinHeight() && candidate.getBlockY() < world.getMaxHeight() - 3) {
                return candidate;
            }
        }
        return null;
    }

    private Location snapToGround(Location loc, World world) {
        int x = loc.getBlockX();
        int z = loc.getBlockZ();
        int highestY = world.getHighestBlockYAt(x, z);
        if (highestY > world.getMinHeight()) {
            return new Location(world, x + 0.5, highestY, z + 0.5, loc.getYaw(), loc.getPitch());
        }
        return null;
    }

    private boolean isValidSpawnPoint(Location loc, World world) {
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        // Ground must be solid
        if (!world.getBlockAt(x, y - 1, z).getType().isSolid()) {
            return false;
        }
        // Two blocks of air above
        if (world.getBlockAt(x, y, z).getType().isSolid()) {
            return false;
        }
        if (world.getBlockAt(x, y + 1, z).getType().isSolid()) {
            return false;
        }
        return true;
    }

    private boolean isCramped(Location loc, World world) {
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        int freeCount = 0;
        int verticalClearance = 0;

        // 3x3x3 region
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = 0; dy <= 2; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    Block block = world.getBlockAt(x + dx, y + dy, z + dz);
                    if (!block.getType().isSolid()) {
                        freeCount++;
                    }
                }
            }
        }

        // Vertical clearance check
        for (int dy = 0; dy < 10; dy++) {
            if (!world.getBlockAt(x, y + dy, z).getType().isSolid()) {
                verticalClearance++;
            } else {
                break;
            }
        }

        return freeCount < config.getCrampedFreeBlockThreshold() || verticalClearance < 2;
    }

    private boolean isOpen(Location loc, World world) {
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        int freeCount = 0;
        int verticalClearance = 0;

        // 5x5x3 region
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = 0; dy <= 2; dy++) {
                for (int dz = -2; dz <= 2; dz++) {
                    Block block = world.getBlockAt(x + dx, y + dy, z + dz);
                    if (!block.getType().isSolid()) {
                        freeCount++;
                    }
                }
            }
        }

        // Vertical clearance check
        for (int dy = 0; dy < 10; dy++) {
            if (!world.getBlockAt(x, y + dy, z).getType().isSolid()) {
                verticalClearance++;
            } else {
                break;
            }
        }

        return freeCount >= config.getOpenFreeBlockThreshold() && verticalClearance >= 3;
    }
}
