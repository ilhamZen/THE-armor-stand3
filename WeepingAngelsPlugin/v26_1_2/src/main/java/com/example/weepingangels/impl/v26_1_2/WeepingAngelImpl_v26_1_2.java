package com.example.weepingangels.impl.v26_1_2;

import com.example.weepingangels.api.WeepingAngelAPI;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

/**
 * WeepingAngelAPI implementation for PurpurMC 26.1.2 (Minecraft 1.21.1).
 * Uses public Paper/Bukkit API only, optimized for PurpurMC's enhanced performance.
 * 
 * This module targets Minecraft 1.21.1 running on PurpurMC build 26.1.2,
 * which is based on Paper/Spigot mappings and provides improved entity handling
 * and packet processing compared to vanilla implementations.
 */
public class WeepingAngelImpl_v26_1_2 implements WeepingAngelAPI {

    // Frozen pose: Angel appears as a normal statue when observed
    private static final EulerAngle FROZEN_HEAD = new EulerAngle(Math.toRadians(10), 0, 0);
    private static final EulerAngle FROZEN_BODY = new EulerAngle(0, 0, 0);
    private static final EulerAngle FROZEN_LEFT_ARM = new EulerAngle(Math.toRadians(-45), 0, Math.toRadians(-10));
    private static final EulerAngle FROZEN_RIGHT_ARM = new EulerAngle(Math.toRadians(-45), 0, Math.toRadians(10));
    private static final EulerAngle FROZEN_LEFT_LEG = new EulerAngle(Math.toRadians(-5), 0, Math.toRadians(-3));
    private static final EulerAngle FROZEN_RIGHT_LEG = new EulerAngle(Math.toRadians(5), 0, Math.toRadians(3));

    // Pursuit pose: Angel moves aggressively when unobserved
    private static final EulerAngle PURSUIT_HEAD = new EulerAngle(Math.toRadians(-20), 0, 0);
    private static final EulerAngle PURSUIT_BODY = new EulerAngle(Math.toRadians(10), 0, 0);
    private static final EulerAngle PURSUIT_LEFT_ARM = new EulerAngle(Math.toRadians(-90), 0, Math.toRadians(-20));
    private static final EulerAngle PURSUIT_RIGHT_ARM = new EulerAngle(Math.toRadians(-90), 0, Math.toRadians(20));
    private static final EulerAngle PURSUIT_LEFT_LEG = new EulerAngle(Math.toRadians(-30), 0, 0);
    private static final EulerAngle PURSUIT_RIGHT_LEG = new EulerAngle(Math.toRadians(30), 0, 0);

    @Override
    public String getVersionId() {
        return "v26_1_2 (PurpurMC 1.21.1)";
    }

    @Override
    public void applyFrozenPose(ArmorStand stand) {
        stand.setHeadPose(FROZEN_HEAD);
        stand.setBodyPose(FROZEN_BODY);
        stand.setLeftArmPose(FROZEN_LEFT_ARM);
        stand.setRightArmPose(FROZEN_RIGHT_ARM);
        stand.setLeftLegPose(FROZEN_LEFT_LEG);
        stand.setRightLegPose(FROZEN_RIGHT_LEG);
    }

    @Override
    public void applyPursuitPose(ArmorStand stand) {
        stand.setHeadPose(PURSUIT_HEAD);
        stand.setBodyPose(PURSUIT_BODY);
        stand.setLeftArmPose(PURSUIT_LEFT_ARM);
        stand.setRightArmPose(PURSUIT_RIGHT_ARM);
        stand.setLeftLegPose(PURSUIT_LEFT_LEG);
        stand.setRightLegPose(PURSUIT_RIGHT_LEG);
    }

    @Override
    public boolean hasLineOfSight(Player player, Location target) {
        Location eyeLoc = player.getEyeLocation();
        Vector direction = target.clone().subtract(eyeLoc).toVector().normalize();
        double distance = eyeLoc.distance(target);

        World world = eyeLoc.getWorld();
        if (world == null) {
            return false;
        }

        // Use Paper's optimized ray tracing (available in 1.21.1+)
        RayTraceResult result = world.rayTraceBlocks(eyeLoc, direction, distance);
        
        // If no block was hit, the player has clear line of sight
        return result == null || result.getHitBlock() == null;
    }

    @Override
    public boolean safeTeleport(ArmorStand stand, Location destination) {
        if (stand.isDead() || !stand.isValid()) {
            return false;
        }
        if (destination.getWorld() == null) {
            return false;
        }
        
        // In 1.21.1+, teleport is async-safe with proper world loading
        stand.teleport(destination);
        return true;
    }

    @Override
    public boolean isCompatible() {
        // This implementation is specifically built for PurpurMC 26.1.2
        return true;
    }
}
