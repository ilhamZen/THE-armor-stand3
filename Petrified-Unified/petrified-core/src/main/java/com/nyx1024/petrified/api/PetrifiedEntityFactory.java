package com.nyx1024.petrified.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

/**
 * Factory for loading the appropriate PetrifiedEntityAPI implementation based on server version.
 * Merged from WeeperFactory (WeepingAngelsPlugin) and AngelFactory (Petrified).
 */
public class PetrifiedEntityFactory {

    private static final Logger LOGGER = Logger.getLogger("Petrified");
    private static PetrifiedEntityAPI instance;

    /**
     * Loads and returns the appropriate PetrifiedEntityAPI implementation for the current server version.
     * Uses reflection to avoid hard dependencies on version-specific modules.
     * @return The API implementation, or null if none found
     * @throws RuntimeException if no compatible implementation is found
     */
    @NotNull
    public static synchronized PetrifiedEntityAPI getAPI() {
        if (instance != null) {
            return instance;
        }

        String bukkitVersion = Bukkit.getServer().getBukkitVersion();
        String minecraftVersion = Bukkit.getServer().getMinecraftVersion();
        
        LOGGER.info("[Petrified] Detecting server version: " + bukkitVersion + " (MC: " + minecraftVersion + ")");

        // Parse major.minor version from getBukkitVersion() - primary source of truth
        int[] version = parseVersion(bukkitVersion);
        int major = version[0];
        int minor = version[1];

        // Try to load implementations in order of preference (newest first)
        String[] implClasses = new String[]{
            // Contemporary 1.21.x implementations
            "com.nyx1024.petrified.impl.v1_21_R1.PetrifiedEntityImpl_1_21_R1",
            // Modern 1.20.x implementations  
            "com.nyx1024.petrified.impl.v1_20_R2.PetrifiedEntityImpl_1_20_R2",
            "com.nyx1024.petrified.impl.v1_20_R1.PetrifiedEntityImpl_1_20_R1",
            // Legacy implementations
            "com.nyx1024.petrified.impl.v1_16.PetrifiedEntityImpl_1_16",
            "com.nyx1024.petrified.impl.v1_12.PetrifiedEntityImpl_1_12",
            "com.nyx1024.petrified.impl.v1_8.PetrifiedEntityImpl_1_8"
        };

        for (String implClass : implClasses) {
            try {
                Class<?> clazz = Class.forName(implClass);
                Object obj = clazz.getDeclaredConstructor().newInstance();
                if (obj instanceof PetrifiedEntityAPI) {
                    PetrifiedEntityAPI api = (PetrifiedEntityAPI) obj;
                    if (api.isCompatible()) {
                        instance = api;
                        LOGGER.info("[Petrified] Loaded API implementation: " + implClass);
                        return instance;
                    } else {
                        LOGGER.fine("[Petrified] Implementation " + implClass + " not compatible with this version");
                    }
                }
            } catch (ClassNotFoundException e) {
                // Implementation not available for this build, try next
            } catch (Exception e) {
                LOGGER.warning("[Petrified] Failed to load implementation " + implClass + ": " + e.getMessage());
            }
        }

        // No compatible implementation found - create a fallback best-effort implementation
        LOGGER.warning("[Petrified] No compatible implementation found for version " + bukkitVersion);
        LOGGER.warning("[Petrified] Falling back to best-effort mode with limited functionality");
        
        try {
            instance = new FallbackEntityAPI();
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("[Petrified] No compatible API implementation found and fallback failed");
        }
    }

    /**
     * Parses version string like "1.21.5-R0.1-SNAPSHOT" into [major, minor] array.
     * Primary parsing uses getBukkitVersion() which works on runtime-remapped versions.
     * @param versionString The version string to parse
     * @return int array [major, minor]
     */
    @NotNull
    private static int[] parseVersion(@NotNull String versionString) {
        // Pattern: ^(\d+)\.(\d+)(?:\.(\d+))?
        String[] parts = versionString.split("[.-]");
        int major = 1;
        int minor = 0;
        
        for (String part : parts) {
            if (part.matches("\\d+")) {
                if (major == 1) {
                    major = Integer.parseInt(part);
                } else if (minor == 0) {
                    minor = Integer.parseInt(part);
                    break;
                }
            }
        }
        
        return new int[]{major, minor};
    }

    /**
     * Checks if a specific implementation class is available.
     * @param className Fully qualified class name
     * @return true if class can be loaded
     */
    public static boolean isImplementationAvailable(@NotNull String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Resets the cached instance (for testing/reload).
     */
    public static void reset() {
        instance = null;
    }

    /**
     * Fallback implementation used when no version-specific implementation is available.
     * Provides basic functionality using only public Bukkit API.
     */
    private static class FallbackEntityAPI implements PetrifiedEntityAPI {

        @Override
        public String getVersionId() {
            return "fallback";
        }

        @Override
        public void applyFrozenState(@NotNull org.bukkit.entity.Monster entity) {
            entity.setAI(false);
            entity.setSilent(true);
        }

        @Override
        public void applyPursuitState(@NotNull org.bukkit.entity.Monster entity) {
            entity.setAI(true);
            entity.setSilent(false);
        }

        @Override
        public boolean hasLineOfSight(@NotNull Player player, @NotNull Location target) {
            return player.hasLineOfSight(target);
        }

        @Override
        public boolean safeTeleport(@NotNull org.bukkit.entity.Monster entity, @NotNull Location destination) {
            if (destination.getWorld() == null || destination.getWorld() != entity.getWorld()) {
                return false;
            }
            return entity.teleport(destination);
        }

        @Override
        public boolean isCompatible() {
            return true;
        }

        @Override
        public org.bukkit.entity.Monster spawnEntity(@NotNull Location location) {
            if (location.getWorld() == null) {
                return null;
            }
            // Use Zombie as base entity type for compatibility
            return (org.bukkit.entity.Monster) location.getWorld().spawnEntity(
                location, org.bukkit.entity.EntityType.ZOMBIE);
        }
    }
}
