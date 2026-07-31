package com.example.petrified.plugin;

import com.example.petrified.api.WeeperAPI;
import com.example.petrified.api.WeeperFactory;
import com.example.petrified.command.WeeperCommand;
import com.example.petrified.config.WeeperConfig;
import com.example.petrified.listener.WeeperListener;
import com.example.petrified.manager.WeeperManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
 * Main plugin class for Petrified.
 * A highly aggressive monster plugin featuring Weepers with quantum locking mechanics.
 */
public class PetrifiedPlugin extends JavaPlugin {

    private static final Logger LOGGER = Logger.getLogger("Petrified");

    private WeeperAPI api;
    private WeeperConfig config;
    private WeeperManager manager;
    private WeeperListener listener;

    @Override
    public void onEnable() {
        LOGGER.info("[Petrified] Enabling Petrified v" + getDescription().getVersion());

        // Load the appropriate API implementation for this server version
        try {
            api = WeeperFactory.getAPI();
        } catch (RuntimeException e) {
            LOGGER.severe("[Petrified] Failed to initialize: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize configuration
        config = new WeeperConfig(this);

        // Pass null initially to break the circular dependency loop
        listener = new WeeperListener(null, config);

        // Construct the manager with the ready listener instance
        manager = new WeeperManager(this, config, api, listener);

        // Inject the newly constructed manager back into the listener
        listener.setManager(manager);

        // Register listener after manager is fully initialized
        getServer().getPluginManager().registerEvents(listener, this);

        // Register commands
        WeeperCommand command = new WeeperCommand(manager, config);
        getCommand("weeperspawn").setExecutor(command);
        getCommand("weeperspawn").setTabCompleter(command);
        getCommand("weepersremove").setExecutor(command);
        getCommand("weepersremove").setTabCompleter(command);
        getCommand("weeperslist").setExecutor(command);
        getCommand("weepersreload").setExecutor(command);

        // Start AI loop
        manager.startAILoop();

        LOGGER.info("[Petrified] Successfully enabled! Weepers are now active.");
        LOGGER.info("[Petrified] Using API implementation: " + api.getVersionId());
    }

    @Override
    public void onDisable() {
        LOGGER.info("[Petrified] Disabling Petrified...");

        if (manager != null) {
            manager.stopAILoop();
        }

        LOGGER.info("[Petrified] Successfully disabled.");
    }

    public WeeperAPI getApi() {
        return api;
    }

    public WeeperConfig getConfigHandler() {
        return config;
    }

    public WeeperManager getManager() {
        return manager;
    }
}
