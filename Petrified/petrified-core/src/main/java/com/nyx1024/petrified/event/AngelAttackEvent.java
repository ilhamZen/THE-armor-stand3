package com.nyx1024.petrified.event;

import com.nyx1024.petrified.core.PetrifiedAngel;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when an angel attacks a player.
 */
public class AngelAttackEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    
    private final PetrifiedAngel angel;
    private final Player target;
    private boolean cancelled;

    public AngelAttackEvent(@NotNull PetrifiedAngel angel, @NotNull Player target) {
        this.angel = angel;
        this.target = target;
    }

    /**
     * Get the attacking angel.
     * @return The petrified angel.
     */
    @NotNull
    public PetrifiedAngel getAngel() {
        return angel;
    }

    /**
     * Get the target player.
     * @return The targeted player.
     */
    @NotNull
    public Player getTarget() {
        return target;
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
