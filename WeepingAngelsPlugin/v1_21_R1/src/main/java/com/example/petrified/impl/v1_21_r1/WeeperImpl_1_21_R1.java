package com.example.petrified.impl.v1_21_r1;

import com.example.petrified.api.WeeperAPI;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

public class WeeperImpl_1_21_R1 implements WeeperAPI {
    @Override
    public String getVersionId() { return "v1_21_R1"; }
    @Override
    public void applyFrozenState(Monster m) { m.setAI(false); m.setSilent(true); }
    @Override
    public void applyPursuitState(Monster m) { m.setAI(true); m.setSilent(false); }
    @Override
    public boolean hasLineOfSight(Player p, Location t) { return p.hasLineOfSight(t); }
    @Override
    public boolean safeTeleport(Monster m, Location d) { return d.getWorld() != null && m.teleport(d); }
    @Override
    public boolean isCompatible() { return org.bukkit.Bukkit.getServer().getMinecraftVersion().startsWith("1.21"); }
    @Override
    public Monster spawnWeeper(Location l) { return l.getWorld() != null ? (Monster)l.getWorld().spawnEntity(l, EntityType.ZOMBIE) : null; }
}
