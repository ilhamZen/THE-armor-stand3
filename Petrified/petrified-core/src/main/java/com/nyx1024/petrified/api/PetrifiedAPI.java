package com.nyx1024.petrified.api;

import com.nyx1024.petrified.core.PetrifiedAngel;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

/**
 * Public API for Petrified plugin.
 * Use this to interact with angels programmatically.
 */
public interface PetrifiedAPI {

    /**
     * Get the singleton API instance.
     * @return The API instance, or null if plugin not enabled.
     */
    @Nullable
    static PetrifiedAPI getAPI() {
        try {
            Class<?> clazz = Class.forName("com.nyx1024.petrified.plugin.PetrifiedPlugin");
            Object instance = clazz.getMethod("getInstance").invoke(null);
            if (instance != null) {
                return (PetrifiedAPI) clazz.getMethod("getAPI").invoke(instance);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * Check if the API is available.
     * @return true if the plugin is loaded and API accessible.
     */
    boolean isAvailable();

    /**
     * Get all active angels.
     * @return Collection of all PetrifiedAngel instances.
     */
    @NotNull
    Collection<PetrifiedAngel> getAllAngels();

    /**
     * Get an angel by UUID.
     * @param uuid The angel's UUID.
     * @return The angel, or null if not found.
     */
    @Nullable
    PetrifiedAngel getAngel(@NotNull UUID uuid);

    /**
     * Get angels near a location.
     * @param location The location to check.
     * @param radius The search radius.
     * @return Collection of nearby angels.
     */
    @NotNull
    Collection<PetrifiedAngel> getAngelsNear(@NotNull Location location, double radius);

    /**
     * Get the count of locked angels.
     * @return Number of currently locked angels.
     */
    int getLockedCount();

    /**
     * Get the total count of angels.
     * @return Total number of angels.
     */
    int getTotalCount();

    /**
     * Spawn an angel at a location.
     * @param location The spawn location.
     * @return The spawned angel, or null if failed.
     */
    @Nullable
    PetrifiedAngel spawnAngel(@NotNull Location location);

    /**
     * Remove an angel.
     * @param angel The angel to remove.
     * @return true if removed successfully.
     */
    boolean removeAngel(@NotNull PetrifiedAngel angel);

    /**
     * Register an event listener for petrified events.
     * @param listener The listener to register.
     */
    void registerListener(@NotNull Object listener);

    /**
     * Unregister an event listener.
     * @param listener The listener to unregister.
     */
    void unregisterListener(@NotNull Object listener);
}
