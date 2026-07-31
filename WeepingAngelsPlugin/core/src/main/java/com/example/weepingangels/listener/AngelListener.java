package com.example.weepingangels.listener;

import com.example.weepingangels.config.AngelConfig;
import com.example.weepingangels.manager.AngelManager;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Logger;

/**
 * Handles all damage events for Weeping Angel armor stands.
 * Implements pickaxe-only three-hit kill mechanic.
 */
public class AngelListener implements Listener {

    private static final Logger LOGGER = Logger.getLogger("WeepingAngels");

    private final AngelManager angelManager;
    private final AngelConfig config;

    public AngelListener(AngelManager angelManager, AngelConfig config) {
        this.angelManager = angelManager;
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof ArmorStand stand)) {
            return;
        }
        if (!angelManager.isAngel(stand)) {
            return;
        }

        // Cancel all non-pickaxe damage (fire, lava, explosion, fall, suffocation, void, etc.)
        if (!(event instanceof EntityDamageByEntityEvent)) {
            event.setCancelled(true);

            // Void protection: teleport back to last safe location
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                AngelManager.AngelState state = getAngelState(stand);
                if (state != null && state.getLastSafeLocation() != null) {
                    angelManager.getApi().safeTeleport(stand, state.getLastSafeLocation());
                }
            }
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof ArmorStand stand)) {
            return;
        }
        if (!angelManager.isAngel(stand)) {
            return;
        }

        // Always cancel vanilla damage
        event.setCancelled(true);

        Entity damager = event.getDamager();
        if (!(damager instanceof Player player)) {
            return;
        }

        if (!config.isOnlyPickaxeDamage()) {
            // If config allows any damage, treat as pickaxe hit
            processPickaxeHit(stand, player);
            return;
        }

        // Check main hand first
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (isPickaxe(mainHand)) {
            processPickaxeHit(stand, player);
            return;
        }

        // Optionally check off-hand only if main hand is empty
        if (mainHand.getType() == Material.AIR) {
            ItemStack offHand = player.getInventory().getItemInOffHand();
            if (isPickaxe(offHand)) {
                processPickaxeHit(stand, player);
                return;
            }
        }

        // Not a pickaxe - do nothing, damage already cancelled
        if (config.isDebug()) {
            LOGGER.info("[WeepingAngels] Non-pickaxe hit on angel by " + player.getName() + " - ignored.");
        }
    }

    private boolean isPickaxe(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }
        return item.getType().name().endsWith("_PICKAXE");
    }

    private void processPickaxeHit(ArmorStand stand, Player player) {
        int currentHits = angelManager.getHitCount(stand);
        int newHits = currentHits + 1;
        angelManager.setHitCount(stand, newHits);

        // Visual and audio feedback
        stand.getWorld().spawnParticle(Particle.BLOCK_CRACK, stand.getLocation().add(0, 1, 0),
                15, 0.3, 0.5, 0.3, 0.1, Material.STONE.createBlockData());
        stand.getWorld().playSound(stand.getLocation(), Sound.BLOCK_STONE_HIT, 1.0f, 0.8f);

        if (config.isDebug()) {
            LOGGER.info("[WeepingAngels] Pickaxe hit " + newHits + "/" + config.getRequiredPickaxeHits()
                    + " on angel by " + player.getName());
        } // Perbaikan: Menutup block debug if

        if (newHits >= config.getRequiredPickaxeHits()) {
            // Angel dies
            stand.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, stand.getLocation().add(0, 1, 0),
                    1, 0, 0, 0, 0);
            stand.getWorld().playSound(stand.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1.5f);
            angelManager.removeAngel(stand);
            if (config.isDebug()) {
                LOGGER.info("[WeepingAngels] Angel destroyed by " + player.getName());
            }
        }
    } // Perbaikan: Menutup method processPickaxeHit

    private AngelManager.AngelState getAngelState(ArmorStand stand) {
        // Access through the manager's internal map is not directly exposed,
        // so we return null here. Void protection is handled in the AI loop.
        return null;
    }
}
