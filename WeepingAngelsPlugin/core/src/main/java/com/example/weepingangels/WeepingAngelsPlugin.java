package com.example.weepingangels;

import com.example.weepingangels.api.WeepingAngelAPI;
import com.example.weepingangels.api.WeepingAngelFactory;
import com.example.weepingangels.command.AngelCommand;
import com.example.weepingangels.config.AngelConfig;
import com.example.weepingangels.listener.AngelListener;
import com.example.weepingangels.manager.AngelManager;
import com.example.weepingangels.manager.SpawnManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
 * Main plugin class for WeepingAngels.
 * Initializes all managers, listeners, and commands.
 */
public class WeepingAngelsPlugin extends JavaPlugin {

    private static final Logger LOGGER = Logger.getLogger("WeepingAngels");

    private AngelConfig angelConfig;
    private WeepingAngelAPI api;
    private AngelManager angelManager;
    private SpawnManager spawnManager;

    @Override
    public void onEnable() {
        // Save default config
        saveDefaultConfig();
        angelConfig = new AngelConfig(getConfig());

        if (!angelConfig.isEnabled()) {
            LOGGER.info("[WeepingAngels] Plugin is disabled in config. Not starting.");
            return;
        }

        // Detect version and load implementation
        api = WeepingAngelFactory.create();
        if (api == null) {
            LOGGER.severe("[WeepingAngels] No compatible version implementation found. Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize managers
        angelManager = new AngelManager(this, angelConfig, api);
        spawnManager = new SpawnManager(this, angelConfig, angelManager);

        // Register listeners
        getServer().getPluginManager().registerEvents(new AngelListener(angelManager, angelConfig), this);

        // Register commands
        PluginCommand cmd = getCommand("weepingangel");
        if (cmd != null) {
            AngelCommand commandHandler = new AngelCommand(this, angelManager, spawnManager);
            cmd.setExecutor(commandHandler);
            cmd.setTabCompleter(commandHandler);
        }

        // Start AI loop
        angelManager.startAILoop();

        // Start natural spawning
        spawnManager.startNaturalSpawning();

        LOGGER.info("[WeepingAngels] WeepingAngels v" + getDescription().getVersion()
                + " enabled. Implementation: " + api.getVersionId());
    }

    @Override
    public void onDisable() {
        if (angelManager != null) {
            angelManager.stopAILoop();
        }
        if (spawnManager != null) {
            spawnManager.stopNaturalSpawning();
        }
        LOGGER.info("[WeepingAngels] WeepingAngels disabled.");
    }

    public void reloadAngelConfig() {
        reloadConfig();
        angelConfig = new AngelConfig(getConfig());
        LOGGER.info("[WeepingAngels] Configuration reloaded.");
    }

    public AngelConfig getAngelConfig() {
        return angelConfig;
    }

    public AngelManager getAngelManager() {
        return angelManager;
    }

    public SpawnManager getSpawnManager() {
        return spawnManager;
    }
}
