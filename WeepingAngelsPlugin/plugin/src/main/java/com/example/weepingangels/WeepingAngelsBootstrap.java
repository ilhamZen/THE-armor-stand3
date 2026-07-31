package com.example.weepingangels;

/**
 * This module aggregates all WeepingAngels modules into a single shaded JAR.
 * The main plugin class resides in the core module.
 * This class exists to ensure the plugin module has at least one source file.
 */
public final class WeepingAngelsBootstrap {

    private WeepingAngelsBootstrap() {
    }

    public static String getInfo() {
        return "WeepingAngels Plugin v1.0.0 - Multi-module shaded build";
    }
}
