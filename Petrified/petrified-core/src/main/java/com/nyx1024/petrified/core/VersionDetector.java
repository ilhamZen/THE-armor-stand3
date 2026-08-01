package com.nyx1024.petrified.core;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Detects the server version at runtime using Bukkit API.
 * Primary source: Bukkit.getBukkitVersion() string parsing.
 * Secondary optional: NMS package suffix (never used as primary gate).
 */
public class VersionDetector {

    private static final Pattern VERSION_PATTERN = Pattern.compile("^(\\d+)\\.(\\d+)(?:\\.(\\d+))?");
    
    private final int majorVersion;
    private final int minorVersion;
    private final int patchVersion;
    private final String nmsSuffix;
    private final boolean runtimeRemapped;
    private final String serverSoftware;

    public VersionDetector() {
        String bukkitVersion = Bukkit.getBukkitVersion();
        Matcher matcher = VERSION_PATTERN.matcher(bukkitVersion);
        
        if (matcher.find()) {
            this.majorVersion = Integer.parseInt(matcher.group(1));
            this.minorVersion = Integer.parseInt(matcher.group(2));
            this.patchVersion = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0;
        } else {
            this.majorVersion = 1;
            this.minorVersion = 21;
            this.patchVersion = 0;
        }

        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        String[] parts = packageName.split("\\.");
        this.nmsSuffix = parts.length > 0 && !parts[parts.length - 1].equals("craftbukkit") 
            ? parts[parts.length - 1] : null;
        
        this.runtimeRemapped = isRuntimeRemappedInternal();
        this.serverSoftware = detectServerSoftware();
    }

    private boolean isRuntimeRemappedInternal() {
        try {
            Class.forName("org.bukkit.craftbukkit.CraftServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private String detectServerSoftware() {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            return "Paper";
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("net.pl3x.purpur.PurpurConfig");
                return "Purpur";
            } catch (ClassNotFoundException ex) {
                try {
                    Class.forName("org.spigotmc.SpigotConfig");
                    return "Spigot";
                } catch (ClassNotFoundException exc) {
                    return "Bukkit";
                }
            }
        }
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public int getPatchVersion() {
        return patchVersion;
    }

    @Nullable
    public String getNmsSuffix() {
        return nmsSuffix;
    }

    public boolean isRuntimeRemapped() {
        return runtimeRemapped;
    }

    @NotNull
    public String getServerSoftware() {
        return serverSoftware;
    }

    public boolean isAtLeast(int major, int minor) {
        if (this.majorVersion > major) return true;
        if (this.majorVersion == major) return this.minorVersion >= minor;
        return false;
    }

    public boolean isAtLeast(int major, int minor, int patch) {
        if (this.majorVersion > major) return true;
        if (this.majorVersion == major && this.minorVersion > minor) return true;
        if (this.majorVersion == major && this.minorVersion == minor) return this.patchVersion >= patch;
        return false;
    }

    public boolean isAtMost(int major, int minor) {
        if (this.majorVersion < major) return true;
        if (this.majorVersion == major) return this.minorVersion <= minor;
        return false;
    }

    public boolean isLegacy() {
        return isAtMost(1, 16);
    }

    public boolean isModern() {
        return isAtLeast(1, 17);
    }

    @Override
    public String toString() {
        return String.format("VersionDetector(%d.%d.%d, NMS=%s, Remapped=%s, Software=%s)",
            majorVersion, minorVersion, patchVersion, nmsSuffix, runtimeRemapped, serverSoftware);
    }
}
