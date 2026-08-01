package com.nyx1024.petrified.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

/**
 * Manages plugin configuration loading and access.
 * All config values are accessed through this class.
 */
public class ConfigManager {

    private final Plugin plugin;
    private FileConfiguration config;
    private File configFile;

    public ConfigManager(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Load the configuration file.
     */
    public void load() {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        
        // Set defaults
        setDefaults();
    }

    private void setDefaults() {
        config.addDefault("enabled", true);
        config.addDefault("max-angels", 50);
        config.addDefault("check-interval-ticks", 20);
        config.addDefault("detection-radius", 32);
        config.addDefault("dread-radius", 16);
        config.addDefault("movement-speed", 0.15);
        config.addDefault("attack-range", 2.5);
        config.addDefault("require-line-of-sight", true);
        config.addDefault("use-fov", true);
        config.addDefault("mirror-reflective-blocks", List.of(
            "GLASS", "WHITE_STAINED_GLASS", "SEA_LANTERN", "GLOWSTONE"
        ));
        config.addDefault("combat.required-pickaxe-hits", 3);
        config.addDefault("combat.hit-cooldown-ticks", 10);
        config.addDefault("combat.crack-stages", 3);
        config.addDefault("combat.death-loot", List.of("COBBLESTONE:2-5"));
        config.addDefault("displacement.mode", "DISPLACE");
        config.addDefault("displacement.anchor", "spawn");
        config.addDefault("displacement.effects", List.of("BLINDNESS", "SLOWNESS", "NAUSEA"));
        config.addDefault("displacement.duration-seconds", 5);
        config.addDefault("sensory.title", "&c&DON'T BLINK");
        config.addDefault("sensory.actionbar", "&8You feel watched...");
        config.addDefault("sensory.sound", "AMBIENT_CAVE");
        config.addDefault("sensory.particles", true);
        config.addDefault("sensory.intensity", 0.5);
        config.addDefault("performance.per-player-los-budget", 5);
        config.addDefault("performance.view-distance-aware", true);
        config.addDefault("performance.auto-quality", true);
        config.addDefault("integrations.placeholderapi", true);
        config.addDefault("integrations.bstats", true);
        config.addDefault("integrations.update-checker", true);
        config.addDefault("lang.locale", "en_US");
        config.addDefault("debug", false);
        config.addDefault("natural-spawning.enabled", true);
        config.addDefault("natural-spawning.interval-ticks", 200);
        config.addDefault("natural-spawning.chance", 0.02);
        config.addDefault("natural-spawning.light-min", 0);
        config.addDefault("natural-spawning.light-max", 7);
        config.addDefault("natural-spawning.near-player-radius", 24);
        config.addDefault("spawning.cramped-threshold", 15);
        config.addDefault("spawning.open-threshold", 40);
        config.addDefault("spawning.cluster-min", 2);
        config.addDefault("spawning.cluster-max", 4);
        config.addDefault("spawning.cluster-radius", 5);
        config.addDefault("persistence.enabled", true);
        config.addDefault("persistence.file", "angels.yml");
        
        try {
            config.options().copyDefaults(true);
            plugin.saveConfig();
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to save config defaults", e);
        }
    }

    /**
     * Reload the configuration.
     */
    public void reload() {
        config = null;
        configFile = null;
        load();
    }

    /**
     * Get a boolean value.
     */
    public boolean getBoolean(@NotNull String path, boolean def) {
        return config != null ? config.getBoolean(path, def) : def;
    }

    public boolean getBoolean(@NotNull String path) {
        return config != null && config.getBoolean(path);
    }

    /**
     * Get an int value.
     */
    public int getInt(@NotNull String path, int def) {
        return config != null ? config.getInt(path, def) : def;
    }

    public int getInt(@NotNull String path) {
        return config != null ? config.getInt(path) : 0;
    }

    /**
     * Get a double value.
     */
    public double getDouble(@NotNull String path, double def) {
        return config != null ? config.getDouble(path, def) : def;
    }

    /**
     * Get a string value.
     */
    @NotNull
    public String getString(@NotNull String path, @NotNull String def) {
        return config != null ? config.getString(path, def) : def;
    }

    @NotNull
    public String getString(@NotNull String path) {
        return config != null ? config.getString(path, "") : "";
    }

    /**
     * Get a list of strings.
     */
    @NotNull
    public List<String> getStringList(@NotNull String path) {
        return config != null ? config.getStringList(path) : List.of();
    }

    /**
     * Check if enabled.
     */
    public boolean isEnabled() {
        return getBoolean("enabled", true);
    }

    /**
     * Get max angels.
     */
    public int getMaxAngels() {
        return getInt("max-angels", 50);
    }

    /**
     * Get check interval in ticks.
     */
    public int getCheckIntervalTicks() {
        return getInt("check-interval-ticks", 20);
    }

    /**
     * Get detection radius.
     */
    public int getDetectionRadius() {
        return getInt("detection-radius", 32);
    }

    /**
     * Get dread radius.
     */
    public int getDreadRadius() {
        return getInt("dread-radius", 16);
    }

    /**
     * Get movement speed.
     */
    public double getMovementSpeed() {
        return getDouble("movement-speed", 0.15);
    }

    /**
     * Get attack range.
     */
    public double getAttackRange() {
        return getDouble("attack-range", 2.5);
    }

    /**
     * Get required pickaxe hits.
     */
    public int getRequiredPickaxeHits() {
        return getInt("combat.required-pickaxe-hits", 3);
    }

    /**
     * Get displacement mode.
     */
    @NotNull
    public String getDisplacementMode() {
        return getString("displacement.mode", "DISPLACE").toUpperCase();
    }

    /**
     * Check if debug mode.
     */
    public boolean isDebug() {
        return getBoolean("debug", false);
    }
}
