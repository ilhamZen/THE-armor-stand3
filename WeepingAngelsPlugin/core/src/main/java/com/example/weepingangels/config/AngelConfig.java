package com.example.weepingangels.config;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Typed accessor for all configuration values.
 */
public class AngelConfig {

    private final FileConfiguration config;

    public AngelConfig(FileConfiguration config) {
        this.config = config;
    }

    public boolean isEnabled() {
        return config.getBoolean("enabled", true);
    }

    public int getMaxAngels() {
        return config.getInt("max-angels", 40);
    }

    public int getCheckIntervalTicks() {
        return config.getInt("check-interval-ticks", 10);
    }

    public int getDetectionRadius() {
        return config.getInt("detection-radius", 24);
    }

    public double getMovementSpeed() {
        return config.getDouble("movement-speed", 0.35);
    }

    public double getAttackRange() {
        return config.getDouble("attack-range", 1.75);
    }

    public boolean isRequireLineOfSight() {
        return config.getBoolean("require-line-of-sight", true);
    }

    public boolean isNaturalSpawningEnabled() {
        return config.getBoolean("natural-spawning.enabled", true);
    }

    public int getSpawnAttemptIntervalTicks() {
        return config.getInt("natural-spawning.attempt-interval-ticks", 200);
    }

    public int getSpawnChancePercent() {
        return config.getInt("natural-spawning.chance-percent", 12);
    }

    public int getMinLightLevel() {
        return config.getInt("natural-spawning.min-light-level", 0);
    }

    public int getMaxLightLevel() {
        return config.getInt("natural-spawning.max-light-level", 7);
    }

    public boolean isOnlyNearPlayers() {
        return config.getBoolean("natural-spawning.only-near-players", true);
    }

    public int getPlayerSearchRadius() {
        return config.getInt("natural-spawning.player-search-radius", 48);
    }

    public int getCrampedFreeBlockThreshold() {
        return config.getInt("spawning.cramped-free-block-threshold", 14);
    }

    public int getOpenFreeBlockThreshold() {
        return config.getInt("spawning.open-free-block-threshold", 22);
    }

    public int getClusterMin() {
        return config.getInt("spawning.cluster-min", 2);
    }

    public int getClusterMax() {
        return config.getInt("spawning.cluster-max", 4);
    }

    public int getClusterRadius() {
        return config.getInt("spawning.cluster-radius", 4);
    }

    public int getRequiredPickaxeHits() {
        return config.getInt("combat.required-pickaxe-hits", 3);
    }

    public boolean isOnlyPickaxeDamage() {
        return config.getBoolean("combat.only-pickaxe-damage", true);
    }

    public int getResetHitsAfterSeconds() {
        return config.getInt("combat.reset-hits-after-seconds", -1);
    }

    public boolean isDebug() {
        return config.getBoolean("debug", false);
    }
}
