package com.example.weepingangels.api;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;

/**
 * Version-agnostic interface for Weeping Angel operations.
 * Implementations provide version-specific behavior where needed.
 */
public interface WeepingAngelAPI {

    /**
     * Returns the version identifier string for this implementation.
     */
    String getVersionId();

    /**
     * Applies the frozen stone pose to the given armor stand.
     */
    void applyFrozenPose(ArmorStand stand);

    /**
     * Applies the aggressive pursuit pose to the given armor stand.
     */
    void applyPursuitPose(ArmorStand stand);

    /**
     * Checks if a player has line of sight to the target location.
     * Uses ray tracing to determine if blocks obstruct the view.
     */
    boolean hasLineOfSight(Player player, Location target);

    /**
     * Performs a safe teleport of the armor stand to the given location.
     * Returns true if the teleport was successful.
     */
    boolean safeTeleport(ArmorStand stand, Location destination);

    /**
     * Returns whether the current server version is supported by this implementation.
     */
    boolean isCompatible();
}
