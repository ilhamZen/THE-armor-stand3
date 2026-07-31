package com.example.petrified.api;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

/**
 * Version-agnostic interface for Weeper (Petrified) operations.
 * Implementations provide version-specific behavior where needed.
 */
public interface WeeperAPI {

    /**
     * Returns the version identifier string for this implementation.
     */
    String getVersionId();

    /**
     * Applies the frozen stone pose/state to the given monster.
     */
    void applyFrozenState(Monster monster);

    /**
     * Applies the aggressive pursuit state to the given monster.
     */
    void applyPursuitState(Monster monster);

    /**
     * Checks if a player has line of sight to the target location.
     * Uses ray tracing to determine if blocks obstruct the view.
     */
    boolean hasLineOfSight(Player player, Location target);

    /**
     * Performs a safe teleport of the monster to the given location.
     * Returns true if the teleport was successful.
     */
    boolean safeTeleport(Monster monster, Location destination);

    /**
     * Returns whether the current server version is supported by this implementation.
     */
    boolean isCompatible();
    
    /**
     * Spawns a Weeper entity at the given location.
     */
    Monster spawnWeeper(Location location);
    
    /**
     * Spawns lock particles around the weeper.
     */
    default void spawnLockParticles(Monster weeper) {
        Location loc = weeper.getLocation();
        // Use CRACKED_STONE_BRICKS particle for legacy compatibility
        weeper.getWorld().spawnParticle(
            org.bukkit.Particle.BLOCK_CRACK,
            loc,
            15,
            0.3, 0.3, 0.3,
            0,
            org.bukkit.Material.STONE.createBlockData()
        );
    }
    
    /**
     * Safely teleports a player to a random location 300-500 blocks away.
     */
    default void safeTeleportPlayer(Player player, World world, double originX, double originZ) {
        java.util.Random random = new java.util.Random();
        
        double angle = random.nextDouble() * 2 * Math.PI;
        double distance = 300 + random.nextDouble() * 200;
        
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
                break;
            }
        }
        
        player.teleport(safeLoc);
        player.setFallDistance(0);
    }
    
    /**
     * Plays the Weeper scrape sound when it breaks line-of-sight and speeds up.
     */
    default void playScrapeSound(org.bukkit.entity.Entity entity, Location location) {
        if (entity instanceof org.bukkit.entity.Player) {
            ((org.bukkit.entity.Player) entity).playSound(location, "entity.weeper.scrape", 1.0f, 1.0f);
        }
    }
    
    /**
     * Plays the jumpscare screech sound on contact curse teleportation.
     */
    default void playJumpscareSound(org.bukkit.entity.Entity entity, Location location) {
        if (entity instanceof org.bukkit.entity.Player) {
            ((org.bukkit.entity.Player) entity).playSound(location, "entity.weeper.jumpscare", 1.0f, 1.0f);
        }
    }
}
