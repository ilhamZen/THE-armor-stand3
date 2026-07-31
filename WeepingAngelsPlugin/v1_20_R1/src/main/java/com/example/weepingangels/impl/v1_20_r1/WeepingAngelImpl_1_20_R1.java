package com.example.weepingangels.impl.v1_20_r1;

import com.example.weepingangels.api.WeepingAngelAPI;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

/**
 * WeepingAngelAPI implementation for Minecraft 1.20.1 (Paper 1.20.1-R0.1-SNAPSHOT).
 * Uses public Paper/Bukkit API only.
 */
public class WeepingAngelImpl_1_20_R1 implements WeepingAngelAPI {

    private static final EulerAngle FROZEN_HEAD = new EulerAngle(Math.toRadians(10), 0, 0);
    private static final EulerAngle FROZEN_BODY = new EulerAngle(0, 0, 0);
    private static final EulerAngle FROZEN_LEFT_ARM = new EulerAngle(Math.toRadians(-45), 0, Math.toRadians(-10));
    private static final EulerAngle FROZEN_RIGHT_ARM = new EulerAngle(Math.toRadians(-45), 0, Math.toRadians(10));
    private static final EulerAngle FROZEN_LEFT_LEG = new EulerAngle(Math.toRadians(-5), 0, Math.toRadians(-3));
    private static final EulerAngle FROZEN_RIGHT_LEG = new EulerAngle(Math.toRadians(5), 0, Math.toRadians(3));

    private static final EulerAngle PURSUIT_HEAD = new EulerAngle(Math.toRadians(-20), 0, 0);
    private static final EulerAngle PURSUIT_BODY = new EulerAngle(Math.toRadians(10), 0, 0);
    private static final EulerAngle PURSUIT_LEFT_ARM = new EulerAngle(Math.toRadians(-90), 0, Math.toRadians(-20));
    private static final EulerAngle PURSUIT_RIGHT_ARM = new EulerAngle(Math.toRadians(-90), 0, Math.toRadians(20));
    private static final EulerAngle PURSUIT_LEFT_LEG = new EulerAngle(Math.toRadians(-30), 0, 0);
    private static final EulerAngle PURSUIT_RIGHT_LEG = new EulerAngle(Math.toRadians(30), 0, 0);

    @Override
    public String getVersionId() {
        return "v1_20_R1 (1.20.1)";
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
        stand.teleport(destination);
        return true;
    }

    @Override
    public boolean isCompatible() {
        return true;
    }
}
