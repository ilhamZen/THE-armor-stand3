package com.nyx1024.petrified.util;

import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Resolves Sound names across Minecraft versions.
 * Handles sound name changes between versions.
 */
public final class SoundResolver {

    private SoundResolver() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Get a sound with legacy fallback.
     * @param modernName Modern sound name (1.13+).
     * @param legacyName Legacy sound name (pre-1.13).
     * @return The Sound or null if neither exists.
     */
    @Nullable
    public static Sound getSound(@NotNull String modernName, @NotNull String legacyName) {
        try {
            return Sound.valueOf(modernName);
        } catch (IllegalArgumentException e) {
            try {
                return Sound.valueOf(legacyName);
            } catch (IllegalArgumentException ex) {
                return null;
            }
        }
    }

    /**
     * Get ambient dread sound.
     * @return The dread ambient sound.
     */
    @Nullable
    public static Sound getAmbientDread() {
        return getSound("AMBIENT_CAVE", "AMBIENCE_CAVE");
    }

    /**
     * Get stone break sound.
     * @return Stone break sound.
     */
    @NotNull
    public static Sound getStoneBreak() {
        Sound sound = getSound("BLOCK_STONE_BREAK", "DIG_STONE");
        return sound != null ? sound : Sound.valueOf("BLOCK_ANVIL_BREAK");
    }

    /**
     * Get entity hurt sound.
     * @return Entity hurt sound.
     */
    @Nullable
    public static Sound getEntityHurt() {
        return getSound("ENTITY_GENERIC_HURT", "GAME_NEUTRAL_HURT");
    }

    /**
     * Get teleport sound.
     * @return Teleport sound.
     */
    @Nullable
    public static Sound getTeleport() {
        return getSound("ENTITY_ENDERMAN_TELEPORT", "MOB_ENDERMEN_PORTAL");
    }

    /**
     * Get armor stand place sound.
     * @return Armor stand place sound.
     */
    @Nullable
    public static Sound getArmorStandPlace() {
        return getSound("ENTITY_ARMOR_STAND_PLACE", null);
    }

    /**
     * Get warden ambient sound for dread effect.
     * @return Warden ambient or fallback.
     */
    @Nullable
    public static Sound getWardenAmbient() {
        return getSound("ENTITY_WARDEN_AMBIENT", "AMBIENT_CAVE");
    }

    /**
     * Get block hit sound.
     * @return Block hit sound.
     */
    @NotNull
    public static Sound getBlockHit() {
        Sound sound = getSound("BLOCK_STONE_HIT", "DIG_STONE");
        return sound != null ? sound : Sound.CLICK;
    }

    /**
     * Get lightning sound.
     * @return Lightning sound.
     */
    @Nullable
    public static Sound getLightning() {
        return getSound("ENTITY_LIGHTNING_BOLT_IMPACT", "AMBIENCE_THUNDER");
    }

    /**
     * Get experience orb pickup sound.
     * @return XP orb sound.
     */
    @Nullable
    public static Sound getExpOrb() {
        return getSound("ENTITY_EXPERIENCE_ORB_PICKUP", "ORB_PICKUP");
    }
}
