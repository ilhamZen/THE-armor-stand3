package com.nyx1024.petrified.event;

import com.nyx1024.petrified.core.PetrifiedAngel;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called when an angel dies (shatters after enough pickaxe hits).
 */
public class AngelDeathEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    
    private final PetrifiedAngel angel;
    private final Player killer;
    private final int hitCount;

    public AngelDeathEvent(@NotNull PetrifiedAngel angel, @Nullable Player killer, int hitCount) {
        this.angel = angel;
        this.killer = killer;
        this.hitCount = hitCount;
    }

    /**
     * Get the dying angel.
     * @return The petrified angel.
     */
    @NotNull
    public PetrifiedAngel getAngel() {
        return angel;
    }

    /**
     * Get the player who killed the angel.
     * @return The killer, or null if no specific killer.
     */
    @Nullable
    public Player getKiller() {
        return killer;
    }

    /**
     * Get the final hit count.
     * @return Number of hits that killed the angel.
     */
    public int getHitCount() {
        return hitCount;
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
