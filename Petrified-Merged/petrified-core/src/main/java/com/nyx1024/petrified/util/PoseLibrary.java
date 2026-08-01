package com.nyx1024.petrified.util;

import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;

/**
 * Library of predefined poses for angels.
 * All poses use EulerAngle (x, y, z) in radians.
 */
public final class PoseLibrary {

    private static final double DEG_TO_RAD = Math.PI / 180.0;

    // Frozen poses - when observed
    public static final EulerAngle FROZEN_PRAYER_HEAD = new EulerAngle(30 * DEG_TO_RAD, 0, 0);
    public static final EulerAngle FROZEN_PRAYER_BODY = new EulerAngle(0, 0, 0);
    public static final EulerAngle FROZEN_PRAYER_LEFT_ARM = new EulerAngle(-45 * DEG_TO_RAD, 0, -10 * DEG_TO_RAD);
    public static final EulerAngle FROZEN_PRAYER_RIGHT_ARM = new EulerAngle(-45 * DEG_TO_RAD, 0, 10 * DEG_TO_RAD);

    public static final EulerAngle FROZEN_REACH_HEAD = new EulerAngle(0, 45 * DEG_TO_RAD, 0);
    public static final EulerAngle FROZEN_REACH_BODY = new EulerAngle(0, 0, 0);
    public static final EulerAngle FROZEN_REACH_LEFT_ARM = new EulerAngle(-90 * DEG_TO_RAD, 0, 170 * DEG_TO_RAD);
    public static final EulerAngle FROZEN_REACH_RIGHT_ARM = new EulerAngle(-120 * DEG_TO_RAD, 0, -10 * DEG_TO_RAD);

    public static final EulerAngle FROZEN_FALL_HEAD = new EulerAngle(-20 * DEG_TO_RAD, 0, 15 * DEG_TO_RAD);
    public static final EulerAngle FROZEN_FALL_BODY = new EulerAngle(10 * DEG_TO_RAD, 0, -5 * DEG_TO_RAD);
    public static final EulerAngle FROZEN_FALL_LEFT_ARM = new EulerAngle(20 * DEG_TO_RAD, 0, 0);
    public static final EulerAngle FROZEN_FALL_RIGHT_ARM = new EulerAngle(-150 * DEG_TO_RAD, 0, 0);

    // Pursuit poses - when moving
    public static final EulerAngle PURSUIT_LUNGE_HEAD = new EulerAngle(0, 0, 0);
    public static final EulerAngle PURSUIT_LUNGE_BODY = new EulerAngle(20 * DEG_TO_RAD, 0, 0);
    public static final EulerAngle PURSUIT_LUNGE_LEFT_ARM = new EulerAngle(-180 * DEG_TO_RAD, 0, 0);
    public static final EulerAngle PURSUIT_LUNGE_RIGHT_ARM = new EulerAngle(-180 * DEG_TO_RAD, 0, 0);

    public static final EulerAngle PURSUIT_CRAWL_HEAD = new EulerAngle(90 * DEG_TO_RAD, 0, 0);
    public static final EulerAngle PURSUIT_CRAWL_BODY = new EulerAngle(90 * DEG_TO_RAD, 0, 0);
    public static final EulerAngle PURSUIT_CRAWL_LEFT_ARM = new EulerAngle(-90 * DEG_TO_RAD, 0, 0);
    public static final EulerAngle PURSUIT_CRAWL_RIGHT_ARM = new EulerAngle(-90 * DEG_TO_RAD, 0, 0);

    // Attack pose
    public static final EulerAngle ATTACK_STRIKE_HEAD = new EulerAngle(0, 180 * DEG_TO_RAD, 0);
    public static final EulerAngle ATTACK_STRIKE_BODY = new EulerAngle(0, 0, 0);
    public static final EulerAngle ATTACK_STRIKE_LEFT_ARM = new EulerAngle(-45 * DEG_TO_RAD, 0, 90 * DEG_TO_RAD);
    public static final EulerAngle ATTACK_STRIKE_RIGHT_ARM = new EulerAngle(-45 * DEG_TO_RAD, 0, -90 * DEG_TO_RAD);

    // Death pose
    public static final EulerAngle DEATH_CRUMBLE_HEAD = new EulerAngle(90 * DEG_TO_RAD, 0, 0);
    public static final EulerAngle DEATH_CRUMBLE_BODY = new EulerAngle(90 * DEG_TO_RAD, 0, 0);
    public static final EulerAngle DEATH_CRUMBLE_LEFT_ARM = new EulerAngle(0, 0, 0);
    public static final EulerAngle DEATH_CRUMBLE_RIGHT_ARM = new EulerAngle(0, 0, 0);

    private PoseLibrary() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Get a named pose preset.
     * @param name The pose name.
     * @return An array of 4 EulerAngles: [head, body, leftArm, rightArm].
     */
    @NotNull
    public static EulerAngle[] getPose(@NotNull String name) {
        switch (name.toUpperCase()) {
            case "FROZEN_PRAYER":
                return new EulerAngle[]{FROZEN_PRAYER_HEAD, FROZEN_PRAYER_BODY, FROZEN_PRAYER_LEFT_ARM, FROZEN_PRAYER_RIGHT_ARM};
            case "FROZEN_REACH":
                return new EulerAngle[]{FROZEN_REACH_HEAD, FROZEN_REACH_BODY, FROZEN_REACH_LEFT_ARM, FROZEN_REACH_RIGHT_ARM};
            case "FROZEN_FALL":
                return new EulerAngle[]{FROZEN_FALL_HEAD, FROZEN_FALL_BODY, FROZEN_FALL_LEFT_ARM, FROZEN_FALL_RIGHT_ARM};
            case "PURSUIT_LUNGE":
                return new EulerAngle[]{PURSUIT_LUNGE_HEAD, PURSUIT_LUNGE_BODY, PURSUIT_LUNGE_LEFT_ARM, PURSUIT_LUNGE_RIGHT_ARM};
            case "PURSUIT_CRAWL":
                return new EulerAngle[]{PURSUIT_CRAWL_HEAD, PURSUIT_CRAWL_BODY, PURSUIT_CRAWL_LEFT_ARM, PURSUIT_CRAWL_RIGHT_ARM};
            case "ATTACK_STRIKE":
                return new EulerAngle[]{ATTACK_STRIKE_HEAD, ATTACK_STRIKE_BODY, ATTACK_STRIKE_LEFT_ARM, ATTACK_STRIKE_RIGHT_ARM};
            case "DEATH_CRUMBLE":
                return new EulerAngle[]{DEATH_CRUMBLE_HEAD, DEATH_CRUMBLE_BODY, DEATH_CRUMBLE_LEFT_ARM, DEATH_CRUMBLE_RIGHT_ARM};
            default:
                return new EulerAngle[]{new EulerAngle(0, 0, 0), new EulerAngle(0, 0, 0), 
                    new EulerAngle(0, 0, 0), new EulerAngle(0, 0, 0)};
        }
    }

    /**
     * Interpolate between two EulerAngles.
     * @param from Starting angle.
     * @param to Target angle.
     * @param factor Interpolation factor (0.0 to 1.0).
     * @return Interpolated EulerAngle.
     */
    @NotNull
    public static EulerAngle lerp(@NotNull EulerAngle from, @NotNull EulerAngle to, double factor) {
        factor = Math.max(0.0, Math.min(1.0, factor));
        return new EulerAngle(
            from.getX() + (to.getX() - from.getX()) * factor,
            from.getY() + (to.getY() - from.getY()) * factor,
            from.getZ() + (to.getZ() - from.getZ()) * factor
        );
    }
}
