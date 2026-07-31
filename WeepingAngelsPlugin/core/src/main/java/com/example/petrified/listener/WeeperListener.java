package com.example.petrified.listener;

import com.example.petrified.config.WeeperConfig;
import com.example.petrified.manager.WeeperManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;
import java.util.logging.Logger;

/**
 * Handles all damage and interaction events for Weeper entities.
 * Implements quantum teleportation curse mechanics.
 */
public class WeeperListener implements Listener {

    private static final Logger LOGGER = Logger.getLogger("Petrified");
    private static final Random RANDOM = new Random();

    private WeeperManager weeperManager;
    private final WeeperConfig config;

    public WeeperListener(WeeperManager weeperManager, WeeperConfig config) {
        this.weeperManager = weeperManager;
        this.config = config;
    }

    /**
     * Sets the WeeperManager reference. Used to break circular dependency during initialization.
     */
    public void setManager(WeeperManager weeperManager) {
        this.weeperManager = weeperManager;
    }

    public WeeperManager getManager() {
        return weeperManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Monster monster)) {
            return;
        }
        if (!weeperManager.isWeeper(monster)) {
            return;
        }

        // Cancel all non-player damage
        if (!(event instanceof EntityDamageByEntityEvent)) {
            event.setCancelled(true);

            // Void protection: teleport back to last safe location
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                WeeperManager.WeeperState state = weeperManager.getWeeperState(monster);
                if (state != null && state.getLastSafeLocation() != null) {
                    weeperManager.getApi().safeTeleport(monster, state.getLastSafeLocation());
                }
            }
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Monster monster)) {
            return;
        }
        if (!weeperManager.isWeeper(monster)) {
            return;
        }

        Entity damager = event.getDamager();
        if (!(damager instanceof Player player)) {
            return;
        }

        // Cancel vanilla damage - Weepers are invulnerable to normal attacks
        event.setCancelled(true);
        
        if (config.isDebug()) {
            LOGGER.info("[Petrified] Player " + player.getName() + " attacked a Weeper - damage cancelled.");
        }
    }

    /**
     * Called when a Weeper touches/attacks a player - triggers the Quantum Teleportation Curse.
     */
    public void triggerQuantumCurse(Player player, Monster weeper) {
        if (!config.isQuantumTeleportEnabled()) {
            return;
        }

        Location playerLoc = player.getLocation();
        Location weeperLoc = weeper.getLocation();
        
        // Play jumpscare screech sound on contact
        player.playSound(player.getLocation(), "entity.weeper.jumpscare", 1.0f, 1.0f);
        
        // Calculate random teleport destination (300-500 blocks away)
        int minDist = config.getTeleportMinDistance();
        int maxDist = config.getTeleportMaxDistance();
        int distance = minDist + RANDOM.nextInt(maxDist - minDist + 1);
        
        // Random angle
        double angle = RANDOM.nextDouble() * 2 * Math.PI;
        
        double newX = playerLoc.getX() + Math.cos(angle) * distance;
        double newZ = playerLoc.getZ() + Math.sin(angle) * distance;
        
        // Find safe Y level at the new location
        Location destLocation = new Location(playerLoc.getWorld(), newX, playerLoc.getY(), newZ);
        Location safeLocation = findSafeLocation(destLocation);
        
        if (safeLocation != null) {
            // Teleport the player
            player.teleport(safeLocation);
            
            // Apply potion distortions: Blindness and Nausea for 100 ticks (5 seconds)
            int duration = config.getPotionEffectDuration();
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration, 0, false, false));
            player.addPotionEffect(new PotionEffect(org.bukkit.potion.PotionEffectType.CONFUSION, duration, 0, false, false));
            
            // Spawn particles at original location
            playerLoc.getWorld().spawnParticle(Particle.PORTAL, playerLoc, 50, 1, 2, 1, 0.5);
            playerLoc.getWorld().spawnParticle(Particle.END_ROD, safeLocation, 30, 0.5, 1, 0.5, 0.1);
            
            if (config.isDebug()) {
                LOGGER.info("[Petrified] Quantum Teleportation Curse triggered on " + player.getName() 
                        + " by Weeper. Teleported " + distance + " blocks.");
            }
        }
    }

    /**
     * Finds a safe location near the target location.
     */
    private Location findSafeLocation(Location loc) {
        if (loc.getWorld() == null) {
            return null;
        }
        
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        
        // Search upward for a safe spot
        for (int dy = 0; dy < 10; dy++) {
            int checkY = y + dy;
            if (loc.getWorld().getBlockAt(x, checkY, z).getType().isAir() &&
                loc.getWorld().getBlockAt(x, checkY + 1, z).getType().isAir() &&
                loc.getWorld().getBlockAt(x, checkY - 1, z).getType().isSolid()) {
                return new Location(loc.getWorld(), x + 0.5, checkY, z + 0.5);
            }
        }
        
        // If no safe spot found above, try to find ground level
        for (int dy = 0; dy > -20; dy--) {
            int checkY = y + dy;
            if (loc.getWorld().getBlockAt(x, checkY, z).getType().isAir() &&
                loc.getWorld().getBlockAt(x, checkY + 1, z).getType().isAir() &&
                loc.getWorld().getBlockAt(x, checkY - 1, z).getType().isSolid()) {
                return new Location(loc.getWorld(), x + 0.5, checkY, z + 0.5);
            }
        }
        
        return loc; // Return original if no safe spot found
    }

    /**
     * Handles player join events to inject the custom resource pack with Weeper sounds.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Force-inject the runtime resource pack so players can hear custom .ogg files
        String packUrl = "https://github.com/WeepingAngelsPlugin/raw/main/resourcepack.zip";
        player.setResourcePack(packUrl);
    }
}
