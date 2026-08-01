package com.nyx1024.petrified.listener;

import com.nyx1024.petrified.config.ConfigManager;
import com.nyx1024.petrified.manager.AngelManager;
import com.nyx1024.petrified.core.PetrifiedAngel;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
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
 * Handles all damage and interaction events for Petrified entities.
 * Implements quantum teleportation curse mechanics and pickaxe-only combat.
 */
public class PetrifiedListener implements Listener {

    private static final Logger LOGGER = Logger.getLogger("Petrified");
    private static final Random RANDOM = new Random();

    private AngelManager angelManager;
    private final ConfigManager config;

    public PetrifiedListener(AngelManager angelManager, ConfigManager config) {
        this.angelManager = angelManager;
        this.config = config;
    }

    public void setAngelManager(AngelManager angelManager) {
        this.angelManager = angelManager;
    }

    public AngelManager getAngelManager() {
        return angelManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        
        // Check if this is a petrified angel
        PetrifiedAngel angel = findAngelByEntity(entity);
        if (angel == null) {
            return;
        }

        // Cancel all non-player damage
        if (!(event instanceof EntityDamageByEntityEvent)) {
            event.setCancelled(true);

            // Void protection: teleport back to last safe location
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                Location safeLoc = angel.getTargetLocation();
                if (safeLoc != null) {
                    angel.teleportSafe(safeLoc);
                }
            }
            return;
        }

        EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
        Entity damager = damageEvent.getDamager();
        
        // Only pickaxe hits count
        if (!(damager instanceof Player)) {
            event.setCancelled(true);
            return;
        }

        Player player = (Player) damager;
        
        // Check if holding pickaxe
        if (!isHoldingPickaxe(player)) {
            event.setCancelled(true);
            return;
        }

        // Valid pickaxe hit - increment hit count
        int hits = angel.incrementHitCount();
        int requiredHits = config.getRequiredPickaxeHits();
        
        // Spawn crack particles based on damage stage
        int stages = config.getCrackStages();
        int stage = (hits * stages) / requiredHits;
        spawnCrackParticles(angel, Math.min(stage, stages - 1));

        if (hits >= requiredHits) {
            // Angel shatters
            handleAngelDeath(angel, player);
            event.setCancelled(true);
        } else {
            // Allow the hit but cancel vanilla damage
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Optional: Send welcome message or check for resource pack
        if (config.isDebug()) {
            player.sendMessage("[Petrified] Welcome! Beware of the statues...");
        }
    }

    /**
     * Called when an angel touches/attacks a player - triggers the Quantum Teleportation Curse.
     */
    public void triggerQuantumCurse(Player player, PetrifiedAngel angel) {
        if (!config.isQuantumTeleportEnabled()) {
            return;
        }

        Location playerLoc = player.getLocation();
        Location angelLoc = angel.getEntity().getLocation();
        
        // Play displacement sound
        playDisplacementSound(player, angelLoc);
        
        // Calculate random teleport destination
        int minDist = config.getTeleportMinDistance();
        int maxDist = config.getTeleportMaxDistance();
        int distance = minDist + RANDOM.nextInt(maxDist - minDist + 1);
        
        double angle = RANDOM.nextDouble() * 2 * Math.PI;
        double newX = playerLoc.getX() + Math.cos(angle) * distance;
        double newZ = playerLoc.getZ() + Math.sin(angle) * distance;
        
        Location destLocation = new Location(playerLoc.getWorld(), newX, playerLoc.getY(), newZ);
        Location safeLocation = findSafeLocation(destLocation);
        
        if (safeLocation != null) {
            player.teleport(safeLocation);
            
            // Apply potion effects
            int duration = config.getPotionEffectDuration();
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration, 0, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, duration, 0, false, false));
            
            // Spawn particles
            playerLoc.getWorld().spawnParticle(Particle.PORTAL, playerLoc, 50, 1, 2, 1, 0.5);
            playerLoc.getWorld().spawnParticle(Particle.END_ROD, safeLocation, 30, 0.5, 1, 0.5, 0.1);
            
            if (config.isDebug()) {
                LOGGER.info("[Petrified] Quantum Teleportation Curse triggered on " + player.getName());
            }
        }
    }

    private PetrifiedAngel findAngelByEntity(Entity entity) {
        if (angelManager == null) return null;
        for (PetrifiedAngel angel : angelManager.getAllAngels()) {
            if (angel.getEntity().getUniqueId().equals(entity.getUniqueId())) {
                return angel;
            }
        }
        return null;
    }

    private boolean isHoldingPickaxe(Player player) {
        String material = player.getInventory().getItemInMainHand().getType().name();
        return material.contains("PICKAXE");
    }

    private void spawnCrackParticles(PetrifiedAngel angel, int stage) {
        Location loc = angel.getEntity().getLocation();
        int count = 5 + (stage * 5);
        angel.getEntity().getWorld().spawnParticle(
            Particle.BLOCK_CRACK,
            loc.clone().add(0, 1, 0),
            count,
            0.3, 0.3, 0.3,
            0.05,
            org.bukkit.Material.COBBLESTONE.createBlockData()
        );
    }

    private void handleAngelDeath(PetrifiedAngel angel, Player killer) {
        Location loc = angel.getEntity().getLocation();
        
        // Drop loot
        for (String lootEntry : config.getDeathLoot()) {
            String[] parts = lootEntry.split(":");
            if (parts.length == 2) {
                String materialName = parts[0];
                String[] range = parts[1].split("-");
                int minAmount = Integer.parseInt(range[0]);
                int maxAmount = range.length > 1 ? Integer.parseInt(range[1]) : minAmount;
                int amount = minAmount + RANDOM.nextInt(maxAmount - minAmount + 1);
                
                try {
                    org.bukkit.Material mat = org.bukkit.Material.valueOf(materialName);
                    loc.getWorld().dropItemNaturally(loc, new org.bukkit.inventory.ItemStack(mat, amount));
                } catch (IllegalArgumentException e) {
                    LOGGER.warning("[Petrified] Invalid material in death loot: " + materialName);
                }
            }
        }
        
        // Remove the angel
        angel.remove();
        angelManager.removeAngel(angel);
        
        if (config.isDebug()) {
            LOGGER.info("[Petrified] Angel shattered by " + killer.getName());
        }
    }

    private Location findSafeLocation(Location loc) {
        if (loc.getWorld() == null) return null;
        
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
        
        return loc;
    }

    private void playDisplacementSound(Entity listener, Location location) {
        if (listener instanceof Player) {
            ((Player) listener).playSound(location, "entity.enderman.teleport", 1.0f, 0.8f);
        }
    }
}
