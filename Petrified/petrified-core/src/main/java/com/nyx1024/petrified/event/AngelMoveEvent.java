package com.nyx1024.petrified.event;

import com.nyx1024.petrified.core.PetrifiedAngel;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when an angel moves to a new location.
 */
public class AngelMoveEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    
    private final PetrifiedAngel angel;
    private Location from;
    private Location to;
    private boolean cancelled;

    public AngelMoveEvent(@NotNull PetrifiedAngel angel, @NotNull Location from, @NotNull Location to) {
        this.angel = angel;
        this.from = from;
        this.to = to;
    }

    /**
     * Get the angel that is moving.
     * @return The petrified angel.
     */
    @NotNull
    public PetrifiedAngel getAngel() {
        return angel;
    }

    /**
     * Get the origin location.
     * @return The starting location.
     */
    @NotNull
    public Location getFrom() {
        return from;
    }

    /**
     * Get the destination location.
     * @return The target location.
     */
    @NotNull
    public Location getTo() {
        return to;
    }

    /**
     * Set the destination location.
     * @param to The new destination.
     */
    public void setTo(@NotNull Location to) {
        this.to = to;
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
