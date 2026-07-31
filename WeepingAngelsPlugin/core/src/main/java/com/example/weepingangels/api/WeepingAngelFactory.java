package com.example.weepingangels.api;

import org.bukkit.Bukkit;

import java.util.logging.Logger;

/**
 * Factory that detects the server version and instantiates the correct
 * WeepingAngelAPI implementation using reflection to avoid class loading issues.
 */
public class WeepingAngelFactory {

    private static final Logger LOGGER = Logger.getLogger("WeepingAngels");

    private WeepingAngelFactory() {
    }

    /**
     * Detects the server version and returns the appropriate implementation.
     * Returns null if no compatible implementation is found.
     */
    public static WeepingAngelAPI create() {
        String version = detectVersion();
        LOGGER.info("[WeepingAngels] Detected server version: " + version);

        if (version.startsWith("1.20")) {
            return instantiate("com.example.weepingangels.impl.v1_20_r1.WeepingAngelImpl_1_20_R1");
        } else if (version.startsWith("1.21") || version.startsWith("1.22")
                || version.startsWith("1.23") || version.startsWith("1.24")
                || version.startsWith("1.25") || version.startsWith("1.26")) {
            return instantiate("com.example.weepingangels.impl.v1_21_r1.WeepingAngelImpl_1_21_R1");
        }

        LOGGER.severe("[WeepingAngels] Unsupported server version: " + version
                + ". Weeping Angels will not function.");
        return null;
    }

    private static String detectVersion() {
        try {
            String bukkitVersion = Bukkit.getServer().getBukkitVersion();
            // bukkitVersion looks like "1.20.1-R0.1-SNAPSHOT" or "1.21.1-R0.1-SNAPSHOT"
            int dashIndex = bukkitVersion.indexOf('-');
            if (dashIndex > 0) {
                return bukkitVersion.substring(0, dashIndex);
            }
            return bukkitVersion;
        } catch (Exception e) {
            LOGGER.warning("[WeepingAngels] Could not detect version via getBukkitVersion, trying package name.");
            try {
                String packageName = Bukkit.getServer().getClass().getPackage().getName();
                String[] parts = packageName.split("\\.");
                if (parts.length > 3) {
                    return parts[3];
                }
            } catch (Exception ex) {
                LOGGER.warning("[WeepingAngels] Could not detect version via package name.");
            }
        }
        return "unknown";
    }

    private static WeepingAngelAPI instantiate(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            Object instance = clazz.getDeclaredConstructor().newInstance();
            if (instance instanceof WeepingAngelAPI api) {
                if (api.isCompatible()) {
                    LOGGER.info("[WeepingAngels] Loaded implementation: " + api.getVersionId());
                    return api;
                } else {
                    LOGGER.warning("[WeepingAngels] Implementation " + className + " reports incompatibility.");
                }
            }
        } catch (ClassNotFoundException e) {
            LOGGER.warning("[WeepingAngels] Implementation class not found: " + className);
        } catch (Exception e) {
            LOGGER.warning("[WeepingAngels] Failed to instantiate " + className + ": " + e.getMessage());
        }
        return null;
    }
}
