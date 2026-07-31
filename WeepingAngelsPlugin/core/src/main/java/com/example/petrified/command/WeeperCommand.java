package com.example.petrified.command;

import com.example.petrified.config.WeeperConfig;
import com.example.petrified.manager.WeeperManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Command handler for the Petrified plugin.
 */
public class WeeperCommand implements CommandExecutor, TabCompleter {

    private static final Logger LOGGER = Logger.getLogger("Petrified");

    private final WeeperManager weeperManager;
    private final WeeperConfig config;

    public WeeperCommand(WeeperManager weeperManager, WeeperConfig config) {
        this.weeperManager = weeperManager;
        this.config = config;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("petrified.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "spawn":
                handleSpawn(sender, args);
                break;
            case "remove":
                handleRemove(sender, args);
                break;
            case "list":
                handleList(sender);
                break;
            case "reload":
                handleReload(sender);
                break;
            default:
                sendHelp(sender);
                break;
        }

        return true;
    }

    private void handleSpawn(CommandSender sender, String[] args) {
        int count = 1;
        if (args.length >= 2) {
            try {
                count = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid number: " + args[1]);
                return;
            }
        }

        Location spawnLoc;
        if (sender instanceof Player player) {
            spawnLoc = player.getLocation();
        } else {
            if (args.length < 5) {
                sender.sendMessage(ChatColor.RED + "Console usage: /weeperspawn <count> <world> <x> <y> <z>");
                return;
            }
            String worldName = args[2];
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                sender.sendMessage(ChatColor.RED + "World not found: " + worldName);
                return;
            }
            double x = Double.parseDouble(args[3]);
            double y = Double.parseDouble(args[4]);
            double z = args.length >= 6 ? Double.parseDouble(args[5]) : 0;
            spawnLoc = new Location(world, x, y, z);
        }

        int spawned = 0;
        for (int i = 0; i < count; i++) {
            if (weeperManager.spawnWeeper(spawnLoc.clone().add(i * 2, 0, 0)) != null) {
                spawned++;
            }
        }

        sender.sendMessage(ChatColor.GREEN + "Spawned " + spawned + " Weeper(s) at " + 
                spawnLoc.getBlockX() + ", " + spawnLoc.getBlockY() + ", " + spawnLoc.getBlockZ());
    }

    private void handleRemove(CommandSender sender, String[] args) {
        if (args.length >= 2 && args[1].equalsIgnoreCase("all")) {
            int removed = 0;
            for (World world : Bukkit.getWorlds()) {
                removed += weeperManager.removeAllWeepers(world);
            }
            sender.sendMessage(ChatColor.GREEN + "Removed " + removed + " Weeper(s) from all worlds.");
        } else if (sender instanceof Player player) {
            int removed = weeperManager.removeAllWeepers(player.getWorld());
            sender.sendMessage(ChatColor.GREEN + "Removed " + removed + " Weeper(s) from " + player.getWorld().getName());
        } else {
            sender.sendMessage(ChatColor.RED + "Console must specify a world or use 'all'.");
        }
    }

    private void handleList(CommandSender sender) {
        int total = 0;
        StringBuilder sb = new StringBuilder();
        for (World world : Bukkit.getWorlds()) {
            int count = 0;
            for (org.bukkit.entity.Entity entity : world.getEntities()) {
                if (weeperManager.isWeeper(entity)) {
                    count++;
                }
            }
            if (count > 0) {
                sb.append(world.getName()).append(": ").append(count).append(", ");
                total += count;
            }
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 2);
        } else {
            sb.append("No Weepers active");
        }
        sender.sendMessage(ChatColor.AQUA + "Active Weepers: " + total + " (" + sb + ")");
    }

    private void handleReload(CommandSender sender) {
        config.reload();
        sender.sendMessage(ChatColor.GREEN + "Petrified configuration reloaded.");
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_PURPLE + "--- Petrified Commands ---");
        sender.sendMessage(ChatColor.GRAY + "/weeperspawn [count] - Spawn Weepers at your location");
        sender.sendMessage(ChatColor.GRAY + "/weeperspawn <count> <world> <x> <y> <z> - Spawn at coordinates");
        sender.sendMessage(ChatColor.GRAY + "/weepersremove [all|world] - Remove Weepers");
        sender.sendMessage(ChatColor.GRAY + "/weeperslist - List active Weepers");
        sender.sendMessage(ChatColor.GRAY + "/weepersreload - Reload configuration");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return getMatches(args[0], Arrays.asList("spawn", "remove", "list", "reload"));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("spawn")) {
            return getMatches(args[1], Arrays.asList("1", "5", "10"));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            return getMatches(args[1], Arrays.asList("all"));
        }
        return new ArrayList<>();
    }

    private List<String> getMatches(String arg, List<String> candidates) {
        List<String> matches = new ArrayList<>();
        for (String candidate : candidates) {
            if (candidate.toLowerCase().startsWith(arg.toLowerCase())) {
                matches.add(candidate);
            }
        }
        return matches;
    }
}
