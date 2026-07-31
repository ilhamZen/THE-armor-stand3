package com.example.petrified.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
 * Configuration handler for the Petrified plugin.
 */
public class WeeperConfig {

    private static final Logger LOGGER = Logger.getLogger("Petrified");

    private final JavaPlugin plugin;
    private FileConfiguration config;

    public WeeperConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();
    }

    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public boolean isEnabled() {
        return config.getBoolean("enabled", true);
    }

    public int getMaxWeepers() {
        return config.getInt("max-weepers", 50);
    }

    public int getDetectionRadius() {
        return config.getInt("detection-radius", 32);
    }

    public boolean isRequireLineOfSight() {
        return config.getBoolean("require-line-of-sight", true);
    }

    public double getAttackRange() {
        return config.getDouble("attack-range", 2.5);
    }

    public double getMovementSpeed() {
        return config.getDouble("movement-speed", 0.15);
    }

    public int getCheckIntervalTicks() {
        return config.getInt("check-interval-ticks", 2);
    }

    public boolean isDebug() {
        return config.getBoolean("debug", false);
    }

    public boolean isQuantumTeleportEnabled() {
        return config.getBoolean("quantum-teleport.enabled", true);
    }

    public int getTeleportMinDistance() {
        return config.getInt("quantum-teleport.min-distance", 300);
    }

    public int getTeleportMaxDistance() {
        return config.getInt("quantum-teleport.max-distance", 500);
    }

    public int getPotionEffectDuration() {
        return config.getInt("quantum-teleport.potion-duration-ticks", 100);
    }
}
