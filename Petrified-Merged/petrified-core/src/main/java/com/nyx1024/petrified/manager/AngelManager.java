package com.nyx1024.petrified.manager;

import com.nyx1024.petrified.core.PetrifiedAngel;
import com.nyx1024.petrified.config.ConfigManager;
import com.nyx1024.petrified.event.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Manages all active angels in the world.
 */
public class AngelManager {

    private final Plugin plugin;
    private final ConfigManager config;
    private final Map<UUID, PetrifiedAngel> angels = new ConcurrentHashMap<>();
    private final Set<UUID> alertedPlayers = ConcurrentHashMap.newKeySet();

    public AngelManager(@NotNull Plugin plugin, @NotNull ConfigManager config) {
        this.plugin = plugin;
        this.config = config;
    }

    /**
     * Add an angel to management.
     */
    public void addAngel(@NotNull PetrifiedAngel angel) {
        try {
            UUID uuid = UUID.fromString(angel.getUuid());
            angels.put(uuid, angel);
            
            AngelSpawnEvent event = new AngelSpawnEvent(angel, angel.getEntity().getLocation());
            Bukkit.getPluginManager().callEvent(event);
        } catch (IllegalArgumentException e) {
            angels.put(UUID.randomUUID(), angel);
        }
    }

    /**
     * Remove an angel from management.
     */
    public void removeAngel(@NotNull PetrifiedAngel angel) {
        for (Iterator<Map.Entry<UUID, PetrifiedAngel>> it = angels.entrySet().iterator(); it.hasNext();) {
            Map.Entry<UUID, PetrifiedAngel> entry = it.next();
            if (entry.getValue() == angel) {
                it.remove();
                break;
            }
        }
    }

    /**
     * Get an angel by UUID.
     */
    @Nullable
    public PetrifiedAngel getAngel(@NotNull UUID uuid) {
        return angels.get(uuid);
    }

    /**
     * Get all angels.
     */
    @NotNull
    public Collection<PetrifiedAngel> getAllAngels() {
        return Collections.unmodifiableCollection(angels.values());
    }

    /**
     * Get angels near a location.
     */
    @NotNull
    public Collection<PetrifiedAngel> getAngelsNear(@NotNull Location loc, double radius) {
        List<PetrifiedAngel> result = new ArrayList<>();
        for (PetrifiedAngel angel : angels.values()) {
            if (angel.getEntity().getWorld() == loc.getWorld() &&
                angel.getEntity().getLocation().distanceSquared(loc) <= radius * radius) {
                result.add(angel);
            }
        }
        return result;
    }

    /**
     * Get count of locked angels.
     */
    public int getLockedCount() {
        int count = 0;
        for (PetrifiedAngel angel : angels.values()) {
            if (angel.isLocked()) count++;
        }
        return count;
    }

    /**
     * Get total count.
     */
    public int getTotalCount() {
        return angels.size();
    }

    /**
     * Check if max angels reached.
     */
    public boolean isAtMaxCapacity() {
        return angels.size() >= config.getMaxAngels();
    }

    /**
     * Alert nearby angels about a player position.
     */
    public void alertNearbyAngels(@NotNull Location playerLoc, double alertRadius) {
        for (PetrifiedAngel angel : getAngelsNear(playerLoc, alertRadius)) {
            if (!angel.isLocked() && !angel.isPermanentlyTrapped()) {
                angel.setTargetLocation(playerLoc);
            }
        }
    }

    /**
     * Cleanup angels in unloaded chunks.
     */
    public void cleanupUnloadedChunks() {
        for (Iterator<PetrifiedAngel> it = angels.values().iterator(); it.hasNext();) {
            PetrifiedAngel angel = it.next();
            Entity entity = angel.getEntity();
            if (!entity.isValid() || entity.isDead()) {
                it.remove();
            }
        }
    }

    /**
     * Clear all angels (on disable).
     */
    public void clearAll() {
        for (PetrifiedAngel angel : angels.values()) {
            try {
                angel.remove();
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Error removing angel", e);
            }
        }
        angels.clear();
    }
}
