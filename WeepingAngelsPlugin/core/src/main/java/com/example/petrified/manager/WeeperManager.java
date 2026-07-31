package com.example.petrified.manager;

import com.example.petrified.api.WeeperAPI;
import com.example.petrified.config.WeeperConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Manages all active Weeper entities, their state, and the main AI loop.
 * Implements quantum locking mechanics based on player line of sight.
 */
public class WeeperManager {

    private static final Logger LOGGER = Logger.getLogger("Petrified");

    private final JavaPlugin plugin;
    private final WeeperConfig config;
    private final WeeperAPI api;
    private final NamespacedKey weeperKey;
    private final NamespacedKey nameKey;

    private final Map<UUID, WeeperState> activeWeepers = new ConcurrentHashMap<>();
    private int aiTaskId = -1;

    public WeeperManager(JavaPlugin plugin, WeeperConfig config, WeeperAPI api) {
        this.plugin = plugin;
        this.config = config;
        this.api = api;
        this.weeperKey = new NamespacedKey(plugin, "weeper");
        this.nameKey = new NamespacedKey(plugin, "weeper_name");
    }

    public NamespacedKey getWeeperKey() {
        return weeperKey;
    }

    public WeeperConfig getConfig() {
        return config;
    }

    public WeeperAPI getApi() {
        return api;
    }

    public int getActiveCount() {
        return activeWeepers.size();
    }

    public boolean isWeeper(Entity entity) {
        if (!(entity instanceof Monster monster)) {
            return false;
        }
        PersistentDataContainer pdc = monster.getPersistentDataContainer();
        return pdc.has(weeperKey, PersistentDataType.BYTE);
    }

    public WeeperState getWeeperState(Monster monster) {
        return activeWeepers.get(monster.getUniqueId());
    }

    public Monster spawnWeeper(Location location) {
        if (activeWeepers.size() >= config.getMaxWeepers()) {
            if (config.isDebug()) {
                LOGGER.info("[Petrified] Max weepers reached, cannot spawn.");
            }
            return null;
        }

        World world = location.getWorld();
        if (world == null) {
            return null;
        }

        Monster monster = api.spawnWeeper(location);
        if (monster == null) {
            return null;
        }

        // Set custom display name: colored bold "Weeper"
        String displayName = ChatColor.BOLD + "" + ChatColor.DARK_PURPLE + "Weeper";
        monster.setCustomName(displayName);
        monster.setCustomNameVisible(true);

        // Mark as weeper with PDC
        PersistentDataContainer pdc = monster.getPersistentDataContainer();
        pdc.set(weeperKey, PersistentDataType.BYTE, (byte) 1);

        WeeperState state = new WeeperState(monster.getUniqueId(), monster.getLocation().clone(), false);
        activeWeepers.put(monster.getUniqueId(), state);

        if (config.isDebug()) {
            LOGGER.info("[Petrified] Spawned Weeper at " + location.getBlockX() + ", "
                    + location.getBlockY() + ", " + location.getBlockZ()
                    + " in world " + world.getName());
        }

        return monster;
    }

    public void removeWeeper(UUID uuid) {
        activeWeepers.remove(uuid);
        Entity entity = Bukkit.getEntity(uuid);
        if (entity != null && !entity.isDead()) {
            entity.remove();
        }
    }

    public void removeWeeper(Monster monster) {
        removeWeeper(monster.getUniqueId());
    }

    public int removeAllWeepers(World world) {
        int count = 0;
        for (Entity entity : world.getEntities()) {
            if (isWeeper(entity)) {
                removeWeeper(entity.getUniqueId());
                count++;
            }
        }
        return count;
    }

    public void startAILoop() {
        if (aiTaskId != -1) {
            return;
        }
        int interval = config.getCheckIntervalTicks();
        aiTaskId = Bukkit.getScheduler().runTaskTimer(plugin, this::tickAI, interval, interval).getTaskId();
    }

    public void stopAILoop() {
        if (aiTaskId != -1) {
            Bukkit.getScheduler().cancelTask(aiTaskId);
            aiTaskId = -1;
        }
    }

    private void tickAI() {
        if (!config.isEnabled()) {
            return;
        }

        // Clean up dead or invalid weepers
        activeWeepers.entrySet().removeIf(entry -> {
            Entity entity = Bukkit.getEntity(entry.getKey());
            return entity == null || entity.isDead() || !entity.isValid();
        });

        for (Map.Entry<UUID, WeeperState> entry : activeWeepers.entrySet()) {
            UUID uuid = entry.getKey();
            WeeperState state = entry.getValue();
            Entity entity = Bukkit.getEntity(uuid);
            if (!(entity instanceof Monster monster) || monster.isDead()) {
                continue;
            }

            Location monsterLoc = monster.getLocation();
            World world = monsterLoc.getWorld();
            if (world == null) {
                continue;
            }

            // Find nearest player within detection radius
            Player nearestPlayer = findNearestPlayer(monsterLoc, config.getDetectionRadius());

            boolean observed = false;
            if (nearestPlayer != null) {
                if (config.isRequireLineOfSight()) {
                    // Quantum Locking Vector Check: Use Player location vectors and target block lines-of-sight
                    Vector playerEyeLoc = nearestPlayer.getEyeLocation().toVector();
                    Vector monsterVec = monsterLoc.clone().add(0, 1.0, 0).toVector();
                    Vector direction = monsterVec.subtract(playerEyeLoc).normalize();
                    Vector lookDirection = nearestPlayer.getLocation().getDirection();
                    
                    // Dot product check - if player is looking directly at weeper
                    double dot = direction.dot(lookDirection);
                    observed = dot > 0.95; // Threshold for "looking directly at"
                    
                    // Also verify no blocks in between
                    if (observed) {
                        observed = api.hasLineOfSight(nearestPlayer, monsterLoc.clone().add(0, 1.0, 0));
                    }
                } else {
                    observed = nearestPlayer.getLocation().distanceSquared(monsterLoc) <=
                            (double) config.getDetectionRadius() * config.getDetectionRadius();
                }
            }

            if (observed) {
                // Quantum locked - freeze in place
                if (!state.isLocked()) {
                    state.setLocked(true);
                    api.applyFrozenState(monster);
                    // Spawn BLOCK particles to indicate quantum lock
                    monsterLoc.getWorld().spawnParticle(Particle.BLOCK_CRACK, monsterLoc.clone().add(0, 1, 0),
                            15, 0.3, 0.5, 0.3, 0.1, monsterLoc.getBlock().getBlockData());
                    if (config.isDebug()) {
                        LOGGER.info("[Petrified] Weeper " + uuid + " is now quantum locked.");
                    }
                }
            } else {
                // Unlocked - pursue with increased speed
                if (state.isLocked()) {
                    state.setLocked(false);
                    api.applyPursuitState(monster);
                    if (config.isDebug()) {
                        LOGGER.info("[Petrified] Weeper " + uuid + " is now unlocked and pursuing.");
                    }
                }

                if (nearestPlayer != null) {
                    // Check attack/contact range
                    double distSq = monsterLoc.distanceSquared(nearestPlayer.getLocation());
                    double attackRange = config.getAttackRange();
                    
                    if (distSq <= attackRange * attackRange) {
                        // Contact! Trigger Quantum Teleportation Curse
                        if (nearestPlayer != null && !nearestPlayer.isDead()) {
                            // Cancel standard physical damage and trigger curse
                            WeeperListener listener = getWeeperListener();
                            if (listener != null) {
                                listener.triggerQuantumCurse(nearestPlayer, monster);
                            }
                        }
                        // Face the player
                        facePlayer(monster, nearestPlayer);
                    } else {
                        // Move toward player with increased speed
                        final Player target = nearestPlayer;
                        final Location currentLoc = monsterLoc.clone();
                        final UUID weeperUuid = uuid;
                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                            Location nextStep = calculateNextStep(currentLoc, target.getLocation(),
                                    config.getMovementSpeed() * 2.0, world); // Doubled speed when unobserved
                            if (nextStep != null) {
                                Bukkit.getScheduler().runTask(plugin, () -> {
                                    Entity e = Bukkit.getEntity(weeperUuid);
                                    if (e instanceof Monster m && !m.isDead() && m.isValid()) {
                                        WeeperState s = activeWeepers.get(weeperUuid);
                                        if (s != null && !s.isLocked()) {
                                            api.safeTeleport(m, nextStep);
                                            s.setLastSafeLocation(nextStep.clone());
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            }

            // Void protection
            if (monsterLoc.getY() < world.getMinHeight() - 10) {
                Location safe = state.getLastSafeLocation();
                if (safe != null && safe.getWorld() != null) {
                    api.safeTeleport(monster, safe);
                }
            }
        }
    }

    private WeeperListener getWeeperListener() {
        // Try to get the listener from registered listeners
        for (org.bukkit.event.Listener listener : plugin.getServer().getPluginManager().getRegisteredListeners()) {
            if (listener instanceof WeeperListener) {
                return (WeeperListener) listener;
            }
        }
        return null;
    }

    private Player findNearestPlayer(Location location, int radius) {
        Player nearest = null;
        double nearestDistSq = (double) radius * radius;
        World world = location.getWorld();
        if (world == null) {
            return null;
        }
        Collection<? extends Player> players = world.getPlayers();
        for (Player player : players) {
            if (player.getGameMode().name().equals("SPECTATOR")) {
                continue;
            }
            double distSq = player.getLocation().distanceSquared(location);
            if (distSq < nearestDistSq) {
                nearestDistSq = distSq;
                nearest = player;
            }
        }
        return nearest;
    }

    private Location calculateNextStep(Location from, Location to, double speed, World world) {
        Vector direction = to.clone().subtract(from).toVector();
        direction.setY(0);
        double length = direction.length();
        if (length < 0.1) {
            return null;
        }
        direction.normalize().multiply(speed);

        Location candidate = from.clone().add(direction);
        candidate.setY(from.getY());

        // Check if candidate is passable
        if (isLocationSafe(candidate, world)) {
            return candidate;
        }

        // Try step up
        Location stepUp = candidate.clone().add(0, 1, 0);
        if (isLocationSafe(stepUp, world)) {
            return stepUp;
        }

        // Try side-step left
        Vector perpendicular = new Vector(-direction.getZ(), 0, direction.getX()).normalize().multiply(speed);
        Location sideLeft = from.clone().add(perpendicular);
        sideLeft.setY(from.getY());
        if (isLocationSafe(sideLeft, world)) {
            return sideLeft;
        }

        // Try side-step right
        Location sideRight = from.clone().add(perpendicular.multiply(-1));
        sideRight.setY(from.getY());
        if (isLocationSafe(sideRight, world)) {
            return sideRight;
        }

        // No valid path found
        return null;
    }

    private boolean isLocationSafe(Location loc, World world) {
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        // The block at feet level should be solid (ground)
        if (!world.getBlockAt(x, y - 1, z).getType().isSolid()) {
            return false;
        }
        // The block at the entity position and one above should be passable
        if (world.getBlockAt(x, y, z).getType().isSolid()) {
            return false;
        }
        if (world.getBlockAt(x, y + 1, z).getType().isSolid()) {
            return false;
        }
        return true;
    }

    private void facePlayer(Monster monster, Player player) {
        Location monsterLoc = monster.getLocation();
        Location playerLoc = player.getLocation();
        double dx = playerLoc.getX() - monsterLoc.getX();
        double dz = playerLoc.getZ() - monsterLoc.getZ();
        float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        monster.setRotation(yaw, 0);
    }

    /**
     * Internal state tracking for each weeper.
     */
    public static class WeeperState {
        private final UUID uuid;
        private Location lastSafeLocation;
        private volatile boolean locked;

        public WeeperState(UUID uuid, Location initialLocation, boolean locked) {
            this.uuid = uuid;
            this.lastSafeLocation = initialLocation;
            this.locked = locked;
        }

        public UUID getUuid() {
            return uuid;
        }

        public Location getLastSafeLocation() {
            return lastSafeLocation;
        }

        public void setLastSafeLocation(Location loc) {
            this.lastSafeLocation = loc;
        }

        public boolean isLocked() {
            return locked;
        }

        public void setLocked(boolean locked) {
            this.locked = locked;
        }
    }
}
