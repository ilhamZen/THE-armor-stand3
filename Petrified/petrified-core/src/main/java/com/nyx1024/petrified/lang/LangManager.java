package com.nyx1024.petrified.lang;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Manages language files and message localization.
 */
public class LangManager {

    private final Plugin plugin;
    private final Map<String, String> messages = new HashMap<>();
    private String locale = "en_US";

    public LangManager(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Load the language file.
     * @param locale The locale to load.
     */
    public void load(@NotNull String locale) {
        this.locale = locale;
        messages.clear();
        
        File langFile = new File(plugin.getDataFolder(), "lang/" + locale + ".yml");
        FileConfiguration config;
        
        if (langFile.exists()) {
            config = YamlConfiguration.loadConfiguration(langFile);
        } else {
            InputStream stream = plugin.getResource("lang/" + locale + ".yml");
            if (stream == null) {
                stream = plugin.getResource("lang/en_US.yml");
            }
            if (stream != null) {
                config = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(stream, StandardCharsets.UTF_8));
            } else {
                plugin.getLogger().warning("[Petrified] No language file found!");
                return;
            }
        }
        
        for (String key : config.getKeys(true)) {
            if (!config.isConfigurationSection(key)) {
                Object value = config.get(key);
                if (value instanceof String) {
                    messages.put(key, ChatColor.translateAlternateColorCodes('&', (String) value));
                }
            }
        }
    }

    /**
     * Reload the language file.
     */
    public void reload() {
        load(locale);
    }

    /**
     * Get a message by key.
     * @param key The message key.
     * @return The translated message or the key if not found.
     */
    @NotNull
    public String get(@NotNull String key) {
        return messages.getOrDefault(key, "&7[&fPetrified&7] &r" + key);
    }

    /**
     * Get a message with placeholders replaced.
     * @param key The message key.
     * @param args Placeholder replacements in order.
     * @return The formatted message.
     */
    @NotNull
    public String get(@NotNull String key, @NotNull String... args) {
        String msg = get(key);
        for (int i = 0; i < args.length; i++) {
            msg = msg.replace("{" + i + "}", args[i]);
        }
        return msg;
    }

    /**
     * Get the current locale.
     * @return The locale string.
     */
    @NotNull
    public String getLocale() {
        return locale;
    }

    /**
     * Save a language file from resources.
     * @param locale The locale to save.
     */
    public void saveDefaultLang(@NotNull String locale) {
        File langFile = new File(plugin.getDataFolder(), "lang/" + locale + ".yml");
        if (!langFile.exists()) {
            InputStream stream = plugin.getResource("lang/" + locale + ".yml");
            if (stream != null) {
                try {
                    File parent = langFile.getParentFile();
                    if (parent != null && !parent.exists()) {
                        parent.mkdirs();
                    }
                    java.nio.file.Files.copy(
                        java.nio.channels.Channels.newChannel(stream).getInputStream(),
                        langFile.toPath()
                    );
                } catch (IOException e) {
                    plugin.getLogger().log(Level.WARNING, "Failed to save lang file", e);
                }
            }
        }
    }
}
