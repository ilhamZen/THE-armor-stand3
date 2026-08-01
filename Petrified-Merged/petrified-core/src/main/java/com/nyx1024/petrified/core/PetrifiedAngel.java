package com.nyx1024.petrified.core;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Version-agnostic interface for Angel implementation.
 * All version-specific implementations must implement this contract.
 */
public interface PetrifiedAngel {

    /**
     * Get the underlying ArmorStand entity.
     * @return The ArmorStand representing this angel.
     */
    @NotNull
    ArmorStand getEntity();

    /**
     * Get the unique identifier for this angel.
     * @return The UUID of this angel.
     */
    @NotNull
    String getUuid();

    /**
     * Check if this angel is currently locked (quantum-locked when observed).
     * @return true if locked, false if able to move.
     */
    boolean isLocked();

    /**
     * Set the locked state of this angel.
     * @param locked true to lock, false to unlock.
     */
    void setLocked(boolean locked);

    /**
     * Get the current hit count from pickaxe attacks.
     * @return Number of hits received.
     */
    int getHitCount();

    /**
     * Increment the hit count.
     * @return The new hit count.
     */
    int incrementHitCount();

    /**
     * Reset the hit count to zero.
     */
    void resetHitCount();

    /**
     * Get the last known target location for pathfinding.
     * @return The target location, or null if no target.
     */
    @Nullable
    Location getTargetLocation();

    /**
     * Set the target location for this angel.
     * @param location The location to move toward.
     */
    void setTargetLocation(@Nullable Location location);

    /**
     * Check if this angel is permanently trapped (mirror reflection).
     * @return true if permanently trapped.
     */
    boolean isPermanentlyTrapped();

    /**
     * Set the permanently trapped state.
     * @param trapped true if trapped by reflection.
     */
    void setPermanentlyTrapped(boolean trapped);

    /**
     * Apply the frozen pose to this angel.
     */
    void applyFrozenPose();

    /**
     * Apply the pursuit pose to this angel.
     */
    void applyPursuitPose();

    /**
     * Apply the attack pose to this angel.
     */
    void applyAttackPose();

    /**
     * Teleport this angel to a location safely.
     * @param location The destination location.
     */
    void teleportSafe(@NotNull Location location);

    /**
     * Remove this angel from the world.
     */
    void remove();

    /**
     * Save this angel's data to persistence.
     */
    void save();

    /**
     * Check if a player is looking at this angel.
     * @param player The player to check.
     * @return true if the player has line of sight.
     */
    boolean isPlayerLookingAt(@NotNull Player player);

    /**
     * Get the current pose name.
     * @return The name of the current pose.
     */
    @NotNull
    String getCurrentPose();
}
