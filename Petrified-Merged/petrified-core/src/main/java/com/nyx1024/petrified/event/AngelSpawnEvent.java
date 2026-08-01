package com.nyx1024.petrified.event;

import com.nyx1024.petrified.core.PetrifiedAngel;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when an angel spawns.
 */
public class AngelSpawnEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    
    private final PetrifiedAngel angel;
    private final Location spawnLocation;
    private boolean cancelled;

    public AngelSpawnEvent(@NotNull PetrifiedAngel angel, @NotNull Location spawnLocation) {
        this.angel = angel;
        this.spawnLocation = spawnLocation;
    }

    /**
     * Get the spawned angel.
     * @return The petrified angel.
     */
    @NotNull
    public PetrifiedAngel getAngel() {
        return angel;
    }

    /**
     * Get the spawn location.
     * @return The location where the angel spawned.
     */
    @NotNull
    public Location getSpawnLocation() {
        return spawnLocation;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
