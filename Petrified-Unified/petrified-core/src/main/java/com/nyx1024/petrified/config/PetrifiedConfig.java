package com.nyx1024.petrified.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Logger;

/**
 * Configuration handler for the Petrified plugin.
 * Merged from WeeperConfig (WeepingAngelsPlugin) and ConfigManager (Petrified).
 */
public class PetrifiedConfig {

    private static final Logger LOGGER = Logger.getLogger("Petrified");

    private final JavaPlugin plugin;
    private FileConfiguration config;

    public PetrifiedConfig(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();
    }

    /**
     * Reloads the configuration from disk.
     */
    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
        LOGGER.info("[Petrified] Configuration reloaded.");
    }

    // ==================== Core Settings ====================

    public boolean isEnabled() {
        return config.getBoolean("enabled", true);
    }

    public int getMaxEntities() {
        return config.getInt("max-entities", config.getInt("max-weepers", 50));
    }

    public int getDetectionRadius() {
        return config.getInt("detection-radius", 32);
    }

    public int getDreadRadius() {
        return config.getInt("dread-radius", 16);
    }

    public boolean isRequireLineOfSight() {
        return config.getBoolean("require-line-of-sight", true);
    }

    public boolean isUseFOV() {
        return config.getBoolean("use-fov", true);
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

    // ==================== Mirror Trap Settings ====================

    @NotNull
    public List<String> getMirrorReflectiveBlocks() {
        return config.getStringList("mirror-reflective-blocks");
    }

    // ==================== Combat Settings ====================

    public int getRequiredPickaxeHits() {
        return config.getInt("combat.required-pickaxe-hits", 
               config.getInt("required-pickaxe-hits", 3));
    }

    public int getHitCooldownTicks() {
        return config.getInt("combat.hit-cooldown-ticks", 10);
    }

    public int getCrackStages() {
        return config.getInt("combat.crack-stages", 3);
    }

    @NotNull
    public List<String> getDeathLoot() {
        return config.getStringList("combat.death-loot");
    }

    // ==================== Displacement Settings ====================

    public boolean isQuantumTeleportEnabled() {
        return config.getBoolean("quantum-teleport.enabled",
               config.getBoolean("displacement.enabled", true));
    }

    @NotNull
    public String getDisplacementMode() {
        return config.getString("displacement.mode", "DISPLACE");
    }

    @NotNull
    public String getDisplacementAnchor() {
        return config.getString("displacement.anchor", "spawn");
    }

    public int getTeleportMinDistance() {
        return config.getInt("quantum-teleport.min-distance",
               config.getInt("displacement.min-distance", 300));
    }

    public int getTeleportMaxDistance() {
        return config.getInt("quantum-teleport.max-distance",
               config.getInt("displacement.max-distance", 500));
    }

    public int getPotionEffectDuration() {
        return config.getInt("quantum-teleport.potion-duration-ticks",
               config.getInt("displacement.duration-seconds", 5) * 20);
    }

    @NotNull
    public List<String> getDisplacementEffects() {
        return config.getStringList("displacement.effects");
    }

    // ==================== Sensory Horror Settings ====================

    @NotNull
    public String getSensoryTitle() {
        return config.getString("sensory.title", "&c&DON'T BLINK");
    }

    @NotNull
    public String getSensoryActionbar() {
        return config.getString("sensory.actionbar", "&8You feel watched...");
    }

    @NotNull
    public String getSensorySound() {
        return config.getString("sensory.sound", "AMBIENT_CAVE");
    }

    public boolean isSensoryParticles() {
        return config.getBoolean("sensory.particles", true);
    }

    public double getSensoryIntensity() {
        return config.getDouble("sensory.intensity", 0.5);
    }

    // ==================== Spawning Settings ====================

    public boolean isNaturalSpawningEnabled() {
        return config.getBoolean("natural-spawning.enabled", false);
    }

    public int getNaturalSpawningInterval() {
        return config.getInt("natural-spawning.interval-ticks", 200);
    }

    public double getNaturalSpawningChance() {
        return config.getDouble("natural-spawning.chance", 0.01);
    }

    public int getNaturalSpawningLightMin() {
        return config.getInt("natural-spawning.light-min", 0);
    }

    public int getNaturalSpawningLightMax() {
        return config.getInt("natural-spawning.light-max", 7);
    }

    // ==================== Performance Settings ====================

    public int getPerPlayerLOSBudget() {
        return config.getInt("performance.per-player-los-budget", 5);
    }

    public boolean isViewDistanceAware() {
        return config.getBoolean("performance.view-distance-aware", true);
    }

    public boolean isAutoQualityReduction() {
        return config.getBoolean("performance.auto-quality-reduction", true);
    }

    // ==================== Integration Settings ====================

    public boolean isPlaceholderAPIEnabled() {
        return config.getBoolean("integrations.placeholderapi", false);
    }

    public boolean isBStatsEnabled() {
        return config.getBoolean("integrations.bstats", true);
    }

    public boolean isUpdateCheckerEnabled() {
        return config.getBoolean("integrations.update-checker", true);
    }

    // ==================== Persistence Settings ====================

    public boolean isPersistenceEnabled() {
        return config.getBoolean("persistence.enabled", true);
    }

    @NotNull
    public String getPersistenceFile() {
        return config.getString("persistence.file", "angels.yml");
    }

    // ==================== Language Settings ====================

    @NotNull
    public String getLocale() {
        return config.getString("lang.locale", "en_US");
    }
}
