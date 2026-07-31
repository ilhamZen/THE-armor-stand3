package com.example.petrified.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

/**
 * Factory for loading the appropriate WeeperAPI implementation based on server version.
 */
public class WeeperFactory {

    private static final Logger LOGGER = Logger.getLogger("Petrified");
    private static WeeperAPI instance;

    /**
     * Loads and returns the appropriate WeeperAPI implementation for the current server version.
     */
    public static synchronized WeeperAPI getAPI() {
        if (instance != null) {
            return instance;
        }

        String bukkitVersion = Bukkit.getServer().getBukkitVersion();
        String minecraftVersion = Bukkit.getServer().getMinecraftVersion();
        
        LOGGER.info("[Petrified] Detecting server version: " + bukkitVersion + " (MC: " + minecraftVersion + ")");

        // Try to load implementations in order of preference (newest first)
        String[] implClasses = new String[]{
            // Purpur 26.x implementations
            "com.example.petrified.impl.v26_2_0.WeeperImpl_v26_2_0",
            "com.example.petrified.impl.v26_1_2.WeeperImpl_v26_1_2",
            // 1.21.x implementations
            "com.example.petrified.impl.v1_21_r1.WeeperImpl_1_21_R1",
            // 1.20.x implementations
            "com.example.petrified.impl.v1_20_r3.WeeperImpl_1_20_R3",
            "com.example.petrified.impl.v1_20_r2.WeeperImpl_1_20_R2",
            "com.example.petrified.impl.v1_20_r1.WeeperImpl_1_20_R1",
            // Legacy implementations (if needed)
            "com.example.petrified.impl.v1_16_r1.WeeperImpl_1_16_R1",
            "com.example.petrified.impl.v1_12_r1.WeeperImpl_1_12_R1",
            "com.example.petrified.impl.v1_7_r4.WeeperImpl_1_7_R4"
        };

        for (String implClass : implClasses) {
            try {
                Class<?> clazz = Class.forName(implClass);
                Object obj = clazz.getDeclaredConstructor().newInstance();
                if (obj instanceof WeeperAPI) {
                    WeeperAPI api = (WeeperAPI) obj;
                    if (api.isCompatible()) {
                        instance = api;
                        LOGGER.info("[Petrified] Loaded WeeperAPI implementation: " + implClass);
                        return instance;
                    }
                }
            } catch (ClassNotFoundException e) {
                // Implementation not available for this build, try next
            } catch (Exception e) {
                LOGGER.warning("[Petrified] Failed to load implementation " + implClass + ": " + e.getMessage());
            }
        }

        throw new RuntimeException("[Petrified] No compatible WeeperAPI implementation found for server version: " + bukkitVersion);
    }

    /**
     * Checks if a specific implementation class is available.
     */
    public static boolean isImplementationAvailable(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
