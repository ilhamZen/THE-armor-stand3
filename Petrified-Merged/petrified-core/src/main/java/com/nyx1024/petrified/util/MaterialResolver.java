package com.nyx1024.petrified.util;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Resolves Material names across Minecraft versions.
 * Handles the 1.13 Flattening by providing legacy fallbacks.
 */
public final class MaterialResolver {

    private static final boolean IS_PRE_13 = !Material.class.isEnum() || hasLegacyMaterials();

    private MaterialResolver() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static boolean hasLegacyMaterials() {
        try {
            Material.valueOf("STONE_BUTTON");
            return false;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    /**
     * Get a Material by name with automatic legacy handling.
     * @param modernName The modern (1.13+) material name.
     * @param legacyName The legacy (pre-1.13) material name.
     * @return The resolved Material, or AIR if neither exists.
     */
    @NotNull
    public static Material getMaterial(@NotNull String modernName, @NotNull String legacyName) {
        try {
            return Material.valueOf(modernName);
        } catch (IllegalArgumentException e) {
            try {
                return Material.valueOf(legacyName);
            } catch (IllegalArgumentException ex) {
                return Material.AIR;
            }
        }
    }

    /**
     * Get a Material that is guaranteed to exist on all versions.
     * @param name The material name.
     * @return The Material or AIR.
     */
    @NotNull
    public static Material safeValueOf(@NotNull String name) {
        try {
            return Material.valueOf(name);
        } catch (IllegalArgumentException e) {
            return Material.AIR;
        }
    }

    /**
     * Check if a material is a pickaxe.
     * @param material The material to check.
     * @return true if it's a pickaxe.
     */
    public static boolean isPickaxe(@Nullable Material material) {
        if (material == null) return false;
        String name = material.name();
        return name.endsWith("_PICKAXE") || 
               name.equals("WOOD_PICKAXE") ||
               name.equals("STONE_PICKAXE") ||
               name.equals("IRON_PICKAXE") ||
               name.equals("GOLD_PICKAXE") ||
               name.equals("DIAMOND_PICKAXE");
    }

    /**
     * Get stone material for the current version.
     * @return Stone material.
     */
    @NotNull
    public static Material getStone() {
        return safeValueOf("STONE");
    }

    /**
     * Get cobblestone material for the current version.
     * @return Cobblestone material.
     */
    @NotNull
    public static Material getCobblestone() {
        return safeValueOf("COBBLESTONE");
    }

    /**
     * Get mirror/reflective block materials.
     * @return Array of reflective materials.
     */
    @NotNull
    public static Material[] getReflectiveBlocks() {
        return new Material[] {
            safeValueOf("GLASS"),
            safeValueOf("WHITE_STAINED_GLASS"),
            safeValueOf("ORANGE_STAINED_GLASS"),
            safeValueOf("MAGENTA_STAINED_GLASS"),
            safeValueOf("LIGHT_BLUE_STAINED_GLASS"),
            safeValueOf("YELLOW_STAINED_GLASS"),
            safeValueOf("LIME_STAINED_GLASS"),
            safeValueOf("PINK_STAINED_GLASS"),
            safeValueOf("GRAY_STAINED_GLASS"),
            safeValueOf("LIGHT_GRAY_STAINED_GLASS"),
            safeValueOf("CYAN_STAINED_GLASS"),
            safeValueOf("PURPLE_STAINED_GLASS"),
            safeValueOf("BLUE_STAINED_GLASS"),
            safeValueOf("BROWN_STAINED_GLASS"),
            safeValueOf("GREEN_STAINED_GLASS"),
            safeValueOf("RED_STAINED_GLASS"),
            safeValueOf("BLACK_STAINED_GLASS"),
            safeValueOf("POLISHED_ANDESITE"),
            safeValueOf("POLISHED_DIORITE"),
            safeValueOf("POLISHED_GRANITE"),
            safeValueOf("QUARTZ_BLOCK"),
            safeValueOf("SMOOTH_QUARTZ"),
            safeValueOf("SEA_LANTERN"),
            safeValueOf("GLOWSTONE"),
            safeValueOf("SHULKER_BOX"),
            safeValueOf("WHITE_SHULKER_BOX")
        };
    }

    /**
     * Check if a material is reflective (mirror trap).
     * @param material The material to check.
     * @return true if reflective.
     */
    public static boolean isReflective(@Nullable Material material) {
        if (material == null) return false;
        for (Material reflective : getReflectiveBlocks()) {
            if (material == reflective) return true;
        }
        return false;
    }
}
