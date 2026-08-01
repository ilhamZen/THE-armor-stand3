package com.nyx1024.petrified.event;

import com.nyx1024.petrified.core.PetrifiedAngel;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called when an angel becomes quantum-locked (observed by a player).
 */
public class AngelLockEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    
    private final PetrifiedAngel angel;
    private final Player observer;
    private boolean cancelled;

    public AngelLockEvent(@NotNull PetrifiedAngel angel, @Nullable Player observer) {
        this.angel = angel;
        this.observer = observer;
    }

    /**
     * Get the angel that is being locked.
     * @return The petrified angel.
     */
    @NotNull
    public PetrifiedAngel getAngel() {
        return angel;
    }

    /**
     * Get the player who observed the angel.
     * @return The observing player, or null if no specific observer.
     */
    @Nullable
    public Player getObserver() {
        return observer;
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
