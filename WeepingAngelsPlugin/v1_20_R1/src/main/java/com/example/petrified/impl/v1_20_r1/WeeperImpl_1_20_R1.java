package com.example.petrified.impl.v1_20_r1;

import com.example.petrified.api.WeeperAPI;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * WeeperAPI implementation for Minecraft 1.20.1 (Paper/Spigot).
 */
public class WeeperImpl_1_20_R1 implements WeeperAPI {

    @Override
    public String getVersionId() {
        return "v1_20_R1";
    }

    @Override
    public void applyFrozenState(Monster monster) {
        // Freeze movement by setting AI disabled or using potion effects
        monster.setAI(false);
        monster.setSilent(true);
    }

    @Override
    public void applyPursuitState(Monster monster) {
        // Enable AI for pursuit
        monster.setAI(true);
        monster.setSilent(false);
    }

    @Override
    public boolean hasLineOfSight(Player player, Location target) {
        // Use Bukkit's built-in line of sight check
        return player.hasLineOfSight(target);
    }

    @Override
    public boolean safeTeleport(Monster monster, Location destination) {
        if (destination.getWorld() == null || destination.getWorld() != monster.getWorld()) {
            return false;
        }
        return monster.teleport(destination);
    }

    @Override
    public boolean isCompatible() {
        String version = org.bukkit.Bukkit.getServer().getMinecraftVersion();
        return version.startsWith("1.20") && !version.contains("1.20.5") && !version.contains("1.20.6");
    }

    @Override
    public Monster spawnWeeper(Location location) {
        if (location.getWorld() == null) {
            return null;
        }
        // Spawn a Zombie as the base monster type (can be customized)
        Monster monster = (Monster) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
        monster.setCustomNameVisible(true);
        return monster;
    }
}
