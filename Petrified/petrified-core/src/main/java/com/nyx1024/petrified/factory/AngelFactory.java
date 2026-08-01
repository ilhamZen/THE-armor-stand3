package com.nyx1024.petrified.factory;

import com.nyx1024.petrified.core.PetrifiedAngel;
import com.nyx1024.petrified.core.VersionDetector;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.logging.Level;

/**
 * Factory for creating PetrifiedAngel instances.
 * Uses reflection to load version-specific implementations without hard dependencies.
 */
public class AngelFactory {

    private static final String[] IMPL_CLASSES = {
        "com.nyx1024.petrified.impl.v1_21_R1.PetrifiedAngelImpl",
        "com.nyx1024.petrified.impl.v1_20_R1.PetrifiedAngelImpl",
        "com.nyx1024.petrified.impl.legacy.PetrifiedAngelImpl"
    };

    private Constructor<?> implConstructor;
    private final VersionDetector versionDetector;

    public AngelFactory(@NotNull VersionDetector versionDetector) {
        this.versionDetector = versionDetector;
    }

    /**
     * Initialize the factory by finding and loading the appropriate implementation.
     * @return true if an implementation was found and loaded.
     */
    public boolean initialize() {
        for (String className : IMPL_CLASSES) {
            try {
                Class<?> clazz = Class.forName(className);
                Constructor<?> constructor = clazz.getDeclaredConstructor(ArmorStand.class, Plugin.class);
                constructor.setAccessible(true);
                this.implConstructor = constructor;
                Bukkit.getLogger().info("[Petrified] Loaded angel implementation: " + className);
                return true;
            } catch (ClassNotFoundException e) {
                Bukkit.getLogger().log(Level.FINE, "[Petrified] Implementation not found: " + className, e);
            } catch (NoSuchMethodException e) {
                Bukkit.getLogger().log(Level.SEVERE, "[Petrified] Invalid constructor in: " + className, e);
            } catch (Exception e) {
                Bukkit.getLogger().log(Level.WARNING, "[Petrified] Failed to load: " + className, e);
            }
        }
        Bukkit.getLogger().warning("[Petrified] No compatible angel implementation found!");
        return false;
    }

    /**
     * Create a new PetrifiedAngel instance wrapping an ArmorStand.
     * @param armorStand The ArmorStand entity.
     * @param plugin The plugin instance.
     * @return A new PetrifiedAngel, or null if factory not initialized.
     */
    @Nullable
    public PetrifiedAngel createAngel(@NotNull ArmorStand armorStand, @NotNull Plugin plugin) {
        if (implConstructor == null) {
            return null;
        }
        try {
            return (PetrifiedAngel) implConstructor.newInstance(armorStand, plugin);
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "[Petrified] Failed to create angel instance", e);
            return null;
        }
    }

    /**
     * Get the version detector used by this factory.
     * @return The version detector.
     */
    @NotNull
    public VersionDetector getVersionDetector() {
        return versionDetector;
    }
}
