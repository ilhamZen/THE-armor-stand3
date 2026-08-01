package com.nyx1024.petrified.api;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Version-agnostic interface for Petrified Angel/Weeper operations.
 * Implementations provide version-specific behavior where needed.
 * Merged from WeeperAPI (WeepingAngelsPlugin) and PetrifiedAngel (Petrified).
 */
public interface PetrifiedEntityAPI {

    /**
     * Returns the version identifier string for this implementation.
     * @return Version ID like "v1_20_R1" or "v1_21_R1"
     */
    @NotNull
    String getVersionId();

    /**
     * Applies the frozen stone pose/state to the given entity.
     * Used when the angel is quantum-locked (being observed).
     * @param entity The entity to freeze
     */
    void applyFrozenState(@NotNull Monster entity);

    /**
     * Applies the aggressive pursuit state to the given entity.
     * Used when the angel is unlocked and pursuing a target.
     * @param entity The entity to set pursuit state
     */
    void applyPursuitState(@NotNull Monster entity);

    /**
     * Checks if a player has line of sight to the target location.
     * Uses ray tracing to determine if blocks obstruct the view.
     * @param player The player to check LOS from
     * @param target The target location
     * @return true if player has clear line of sight
     */
    boolean hasLineOfSight(@NotNull Player player, @NotNull Location target);

    /**
     * Performs a safe teleport of the entity to the given location.
     * Handles version-specific teleport mechanics.
     * @param entity The entity to teleport
     * @param destination The destination location
     * @return true if teleport was successful
     */
    boolean safeTeleport(@NotNull Monster entity, @NotNull Location destination);

    /**
     * Returns whether the current server version is supported by this implementation.
     * @return true if compatible
     */
    boolean isCompatible();

    /**
     * Spawns a Petrified entity (ArmorStand or Zombie-based Weeper) at the given location.
     * @param location Spawn location
     * @return The spawned monster entity, or null if failed
     */
    @Nullable
    Monster spawnEntity(@NotNull Location location);

    /**
     * Spawns lock particles around the entity to indicate quantum locking.
     * @param entity The entity to show particles for
     */
    default void spawnLockParticles(@NotNull Monster entity) {
        Location loc = entity.getLocation();
        entity.getWorld().spawnParticle(
            Particle.BLOCK_CRACK,
            loc.clone().add(0, 1, 0),
            15,
            0.3, 0.5, 0.3,
            0.1,
            org.bukkit.Material.STONE.createBlockData()
        );
    }

    /**
     * Spawns crack/damage particles on the entity.
     * @param entity The entity
     * @param stage The crack stage (0-2)
     */
    default void spawnCrackParticles(@NotNull Monster entity, int stage) {
        Location loc = entity.getLocation();
        int count = 5 + (stage * 5);
        entity.getWorld().spawnParticle(
            Particle.BLOCK_CRACK,
            loc.clone().add(0, 1, 0),
            count,
            0.3, 0.3, 0.3,
            0.05,
            org.bukkit.Material.COBBLESTONE.createBlockData()
        );
    }

    /**
     * Safely teleports a player to a random location away from origin.
     * Used for the time displacement attack mechanic.
     * @param player The player to teleport
     * @param world The world
     * @param originX Origin X coordinate
     * @param originZ Origin Z coordinate
     * @param minDistance Minimum teleport distance
     * @param maxDistance Maximum teleport distance
     * @return The new location, or null if failed
     */
    @Nullable
    default Location safeTeleportPlayer(@NotNull Player player, @NotNull World world, 
                                        double originX, double originZ,
                                        int minDistance, int maxDistance) {
        java.util.Random random = new java.util.Random();
        
        double angle = random.nextDouble() * 2 * Math.PI;
        double distance = minDistance + random.nextDouble() * (maxDistance - minDistance);
        
        double newX = originX + Math.cos(angle) * distance;
        double newZ = originZ + Math.sin(angle) * distance;
        
        Location safeLoc = new Location(world, newX, player.getLocation().getY(), newZ);
        
        int maxY = world.getMaxHeight();
        int minY = world.getMinHeight();
        
        for (int y = maxY - 1; y >= minY; y--) {
            Location testLoc = new Location(world, safeLoc.getX(), y, safeLoc.getZ());
            org.bukkit.block.Block block = world.getBlockAt(testLoc);
            org.bukkit.block.Block above = world.getBlockAt(testLoc.clone().add(0, 1, 0));
            
            if (!block.getType().isAir() && !block.isLiquid() && 
                above.getType().isAir() && !above.isLiquid()) {
                safeLoc.setY(y + 1);
                player.teleport(safeLoc);
                player.setFallDistance(0);
                return safeLoc;
            }
        }
        
        return null;
    }

    /**
     * Plays the scrape sound when an entity breaks line-of-sight.
     * @param listener The entity that will hear the sound
     * @param location Sound location
     */
    default void playScrapeSound(@NotNull org.bukkit.entity.Entity listener, @NotNull Location location) {
        if (listener instanceof Player) {
            ((Player) listener).playSound(location, "entity.generic.step", 0.8f, 0.5f);
        }
    }

    /**
     * Plays the jumpscare/displacement sound.
     * @param listener The entity that will hear the sound
     * @param location Sound location
     */
    default void playDisplacementSound(@NotNull org.bukkit.entity.Entity listener, @NotNull Location location) {
        if (listener instanceof Player) {
            ((Player) listener).playSound(location, "entity.enderman.teleport", 1.0f, 0.8f);
        }
    }

    /**
     * Applies rotation to face a player.
     * @param entity The entity to rotate
     * @param player The player to face
     */
    default void facePlayer(@NotNull Monster entity, @NotNull Player player) {
        Location monsterLoc = entity.getLocation();
        Location playerLoc = player.getLocation();
        double dx = playerLoc.getX() - monsterLoc.getX();
        double dz = playerLoc.getZ() - monsterLoc.getZ();
        float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        entity.setRotation(yaw, 0);
    }
}
