package com.example.weepingangels.manager;

import com.example.weepingangels.api.WeepingAngelAPI;
import com.example.weepingangels.config.AngelConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Manages all active Weeping Angel entities, their state, and the main AI loop.
 */
public class AngelManager {

    private static final Logger LOGGER = Logger.getLogger("WeepingAngels");

    private final JavaPlugin plugin;
    private final AngelConfig config;
    private final WeepingAngelAPI api;
    private final NamespacedKey angelKey;
    private final NamespacedKey hitsKey;

    private final Map<UUID, AngelState> activeAngels = new ConcurrentHashMap<>();
    private int aiTaskId = -1;

    public AngelManager(JavaPlugin plugin, AngelConfig config, WeepingAngelAPI api) {
        this.plugin = plugin;
        this.config = config;
        this.api = api;
        this.angelKey = new NamespacedKey(plugin, "angel");
        this.hitsKey = new NamespacedKey(plugin, "hits");
    }

    public NamespacedKey getAngelKey() {
        return angelKey;
    }

    public NamespacedKey getHitsKey() {
        return hitsKey;
    }

    public AngelConfig getConfig() {
        return config;
    }

    public WeepingAngelAPI getApi() {
        return api;
    }

    public int getActiveCount() {
        return activeAngels.size();
    }

    public boolean isAngel(Entity entity) {
        if (!(entity instanceof ArmorStand stand)) {
            return false;
        }
        PersistentDataContainer pdc = stand.getPersistentDataContainer();
        return pdc.has(angelKey, PersistentDataType.BYTE);
    }

    public int getHitCount(ArmorStand stand) {
        PersistentDataContainer pdc = stand.getPersistentDataContainer();
        Integer hits = pdc.get(hitsKey, PersistentDataType.INTEGER);
        return hits != null ? hits : 0;
    }

    public void setHitCount(ArmorStand stand, int hits) {
        PersistentDataContainer pdc = stand.getPersistentDataContainer();
        pdc.set(hitsKey, PersistentDataType.INTEGER, hits);
    }

    public ArmorStand spawnAngel(Location location) {
        if (activeAngels.size() >= config.getMaxAngels()) {
            if (config.isDebug()) {
                LOGGER.info("[WeepingAngels] Max angels reached, cannot spawn.");
            }
            return null;
        }

        World world = location.getWorld();
        if (world == null) {
            return null;
        }

        ArmorStand stand = world.spawn(location, ArmorStand.class, as -> {
            as.setArms(true);
            as.setBasePlate(false);
            as.setGravity(true);
            as.setInvulnerable(true);
            as.setSilent(true);
            as.setCanPickupItems(false);
            as.setCustomNameVisible(false);
            as.setMarker(false);
            as.setSmall(false);
            PersistentDataContainer pdc = as.getPersistentDataContainer();
            pdc.set(angelKey, PersistentDataType.BYTE, (byte) 1);
            pdc.set(hitsKey, PersistentDataType.INTEGER, 0);
        });

        api.applyFrozenPose(stand);
        AngelState state = new AngelState(stand.getUniqueId(), stand.getLocation().clone(), false);
        activeAngels.put(stand.getUniqueId(), state);

        if (config.isDebug()) {
            LOGGER.info("[WeepingAngels] Spawned angel at " + location.getBlockX() + ", "
                    + location.getBlockY() + ", " + location.getBlockZ()
                    + " in world " + world.getName());
        }

        return stand;
    }

    public void removeAngel(UUID uuid) {
        activeAngels.remove(uuid);
        Entity entity = Bukkit.getEntity(uuid);
        if (entity != null && !entity.isDead()) {
            entity.remove();
        }
    }

    public void removeAngel(ArmorStand stand) {
        removeAngel(stand.getUniqueId());
    }

    public int removeAllAngels(World world) {
        int count = 0;
        for (Entity entity : world.getEntities()) {
            if (isAngel(entity)) {
                removeAngel(entity.getUniqueId());
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

        // Clean up dead or invalid angels
        activeAngels.entrySet().removeIf(entry -> {
            Entity entity = Bukkit.getEntity(entry.getKey());
            return entity == null || entity.isDead() || !entity.isValid();
        });

        for (Map.Entry<UUID, AngelState> entry : activeAngels.entrySet()) {
            UUID uuid = entry.getKey();
            AngelState state = entry.getValue();
            Entity entity = Bukkit.getEntity(uuid);
            if (!(entity instanceof ArmorStand stand) || stand.isDead()) {
                continue;
            }

            Location standLoc = stand.getLocation();
            World world = standLoc.getWorld();
            if (world == null) {
                continue;
            }

            // Find nearest player within detection radius
            Player nearestPlayer = findNearestPlayer(standLoc, config.getDetectionRadius());

            boolean observed = false;
            if (nearestPlayer != null) {
                if (config.isRequireLineOfSight()) {
                    observed = api.hasLineOfSight(nearestPlayer, standLoc.clone().add(0, 1.0, 0));
                } else {
                    observed = nearestPlayer.getLocation().distanceSquared(standLoc) <=
                            (double) config.getDetectionRadius() * config.getDetectionRadius();
                }
            }

            if (observed) {
                // Quantum locked
                if (!state.isLocked()) {
                    state.setLocked(true);
                    api.applyFrozenPose(stand);
                    if (config.isDebug()) {
                        LOGGER.info("[WeepingAngels] Angel " + uuid + " is now quantum locked.");
                    }
                }
            } else {
                // Unlocked - pursue
                if (state.isLocked()) {
                    state.setLocked(false);
                    api.applyPursuitPose(stand);
                    if (config.isDebug()) {
                        LOGGER.info("[WeepingAngels] Angel " + uuid + " is now unlocked and pursuing.");
                    }
                }

                if (nearestPlayer != null) {
                    // Check attack range
                    double distSq = standLoc.distanceSquared(nearestPlayer.getLocation());
                    double attackRange = config.getAttackRange();
                    if (distSq <= attackRange * attackRange) {
                        // In attack range - do not move, just face player
                        facePlayer(stand, nearestPlayer);
                    } else {
                        // Async pathfinding then sync movement
                        final Player target = nearestPlayer;
                        final Location currentLoc = standLoc.clone();
                        final UUID angelUuid = uuid;
                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                            Location nextStep = calculateNextStep(currentLoc, target.getLocation(),
                                    config.getMovementSpeed(), world);
                            if (nextStep != null) {
                                Bukkit.getScheduler().runTask(plugin, () -> {
                                    Entity e = Bukkit.getEntity(angelUuid);
                                    if (e instanceof ArmorStand as && !as.isDead() && as.isValid()) {
                                        AngelState s = activeAngels.get(angelUuid);
                                        if (s != null && !s.isLocked()) {
                                            api.safeTeleport(as, nextStep);
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
            if (standLoc.getY() < world.getMinHeight() - 10) {
                Location safe = state.getLastSafeLocation();
                if (safe != null && safe.getWorld() != null) {
                    api.safeTeleport(stand, safe);
                }
            }
        }
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

    private void facePlayer(ArmorStand stand, Player player) {
        Location standLoc = stand.getLocation();
        Location playerLoc = player.getLocation();
        double dx = playerLoc.getX() - standLoc.getX();
        double dz = playerLoc.getZ() - standLoc.getZ();
        float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        stand.setRotation(yaw, 0);
    }

    /**
     * Internal state tracking for each angel.
     */
    public static class AngelState {
        private final UUID uuid;
        private Location lastSafeLocation;
        private volatile boolean locked;

        public AngelState(UUID uuid, Location initialLocation, boolean locked) {
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
