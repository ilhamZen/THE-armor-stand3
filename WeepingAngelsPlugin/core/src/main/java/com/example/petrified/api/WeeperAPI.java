package com.example.petrified.api;

import org.bukkit.Location;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

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
}
