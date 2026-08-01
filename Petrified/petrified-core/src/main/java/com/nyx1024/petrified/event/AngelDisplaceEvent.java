package com.nyx1024.petrified.event;

import com.nyx1024.petrified.core.PetrifiedAngel;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when an angel displaces a player (sends them back in time).
 */
public class AngelDisplaceEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    
    private final PetrifiedAngel angel;
    private final Player target;
    private Location destination;
    private boolean cancelled;

    public AngelDisplaceEvent(@NotNull PetrifiedAngel angel, @NotNull Player target, @NotNull Location destination) {
        this.angel = angel;
        this.target = target;
        this.destination = destination;
    }

    /**
     * Get the displacing angel.
     * @return The petrified angel.
     */
    @NotNull
    public PetrifiedAngel getAngel() {
        return angel;
    }

    /**
     * Get the displaced player.
     * @return The targeted player.
     */
    @NotNull
    public Player getTarget() {
        return target;
    }

    /**
     * Get the destination location.
     * @return Where the player will be sent.
     */
    @NotNull
    public Location getDestination() {
        return destination;
    }

    /**
     * Set the destination location.
     * @param destination The new destination.
     */
    public void setDestination(@NotNull Location destination) {
        this.destination = destination;
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
