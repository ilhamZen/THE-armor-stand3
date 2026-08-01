package com.nyx1024.petrified.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility for sending formatted messages to players.
 */
public final class MessageUtil {

    private MessageUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Translate color codes in a string.
     * @param text The text with & color codes.
     * @return Translated text with actual colors.
     */
    @NotNull
    public static String colorize(@NotNull String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * Send a message to a player.
     * @param player The player to send to.
     * @param message The message (with color codes).
     */
    public static void send(@Nullable Player player, @NotNull String message) {
        if (player != null && player.isOnline()) {
            player.sendMessage(colorize(message));
        }
    }

    /**
     * Send a message to a command sender.
     * @param sender The sender.
     * @param message The message.
     */
    public static void send(@Nullable CommandSender sender, @NotNull String message) {
        if (sender != null) {
            sender.sendMessage(colorize(message));
        }
    }

    /**
     * Send an action bar message to a player.
     * @param player The player.
     * @param message The message.
     */
    public static void sendActionBar(@Nullable Player player, @NotNull String message) {
        if (player != null && player.isOnline()) {
            try {
                player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                    net.md_5.bungee.api.chat.TextComponent.fromLegacyText(colorize(message)));
            } catch (Exception e) {
                // Fallback for older versions
                send(player, message);
            }
        }
    }

    /**
     * Send a title to a player.
     * @param player The player.
     * @param title The main title.
     * @param subtitle The subtitle.
     * @param fadeIn Fade in ticks.
     * @param stay Stay ticks.
     * @param fadeOut Fade out ticks.
     */
    public static void sendTitle(@Nullable Player player, @NotNull String title, 
                                  @Nullable String subtitle, int fadeIn, int stay, int fadeOut) {
        if (player != null && player.isOnline()) {
            try {
                player.sendTitle(colorize(title), subtitle != null ? colorize(subtitle) : "", 
                    fadeIn, stay, fadeOut);
            } catch (Exception e) {
                send(player, colorize(title));
            }
        }
    }

    /**
     * Broadcast a message to all online players with a permission.
     * @param message The message.
     * @param permission Required permission, or null for all.
     */
    public static void broadcast(@NotNull String message, @Nullable String permission) {
        for (Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            if (permission == null || player.hasPermission(permission)) {
                send(player, message);
            }
        }
    }
}
