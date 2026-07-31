package com.example.weepingangels.command;

import com.example.weepingangels.WeepingAngelsPlugin;
import com.example.weepingangels.manager.AngelManager;
import com.example.weepingangels.manager.SpawnManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Handles the /weepingangel command and its subcommands.
 */
public class AngelCommand implements CommandExecutor, TabCompleter {

    private final WeepingAngelsPlugin plugin;
    private final AngelManager angelManager;
    private final SpawnManager spawnManager;

    public AngelCommand(WeepingAngelsPlugin plugin, AngelManager angelManager, SpawnManager spawnManager) {
        this.plugin = plugin;
        this.angelManager = angelManager;
        this.spawnManager = spawnManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("weepingangels.admin")) {
            sender.sendMessage("\u00a7cYou do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "spawn" -> handleSpawn(sender, args);
            case "remove" -> handleRemove(sender);
            case "reload" -> handleReload(sender);
            default -> sendHelp(sender);
        }
        return true;
    }

    private void handleSpawn(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("\u00a7cOnly players can use the spawn command.");
            return;
        }

        Location spawnLoc;
        if (args.length >= 2 && args[1].equalsIgnoreCase("here")) {
            spawnLoc = player.getLocation();
        } else {
            // Use target block location if available, otherwise player location
            var targetBlock = player.getTargetBlockExact(10);
            if (targetBlock != null) {
                spawnLoc = targetBlock.getLocation().add(0.5, 1, 0.5);
            } else {
                spawnLoc = player.getLocation();
            }
        }

        World world = spawnLoc.getWorld();
        if (world == null) {
            sender.sendMessage("\u00a7cCould not determine world.");
            return;
        }

        spawnManager.spawnAtLocation(spawnLoc, world);
        sender.sendMessage("\u00a7aWeeping Angel spawn initiated at your location.");
    }

    private void handleRemove(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("\u00a7cOnly players can use the remove command in a specific world.");
            return;
        }

        World world = player.getWorld();
        int removed = angelManager.removeAllAngels(world);
        sender.sendMessage("\u00a7aRemoved " + removed + " Weeping Angel(s) from world " + world.getName() + ".");
    }

    private void handleReload(CommandSender sender) {
        plugin.reloadAngelConfig();
        sender.sendMessage("\u00a7aWeepingAngels configuration reloaded.");
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("\u00a76=== WeepingAngels Commands ===");
        sender.sendMessage("\u00a7e/weepingangel spawn [here] \u00a77- Spawn angels at location");
        sender.sendMessage("\u00a7e/weepingangel remove \u00a77- Remove all angels in current world");
        sender.sendMessage("\u00a7e/weepingangel reload \u00a77- Reload configuration");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            String partial = args[0].toLowerCase();
            for (String s : Arrays.asList("spawn", "remove", "reload")) {
                if (s.startsWith(partial)) {
                    completions.add(s);
                }
            }
            return completions;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("spawn")) {
            List<String> completions = new ArrayList<>();
            if ("here".startsWith(args[1].toLowerCase())) {
                completions.add("here");
            }
            return completions;
        }
        return new ArrayList<>();
    }
}
