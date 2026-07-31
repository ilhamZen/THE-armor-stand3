package com.example.petrified.impl.v1_21_r1;

import com.example.petrified.api.WeeperAPI;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

public class WeeperImpl_1_21_R1 implements WeeperAPI {
    
    @Override
    public String getVersionId() { 
        return "v1_21_R1"; 
    }
    
    @Override
    public void applyFrozenState(Monster m) { 
        m.setAI(false); 
        m.setSilent(true);
        // Apply slowness for frozen effect
        m.addPotionEffect(new org.bukkit.potion.PotionEffect(
            org.bukkit.potion.PotionEffectType.SLOWNESS, 
            Integer.MAX_VALUE, 
            255, 
            true, 
            false
        ));
    }
    
    @Override
    public void applyPursuitState(Monster m) { 
        m.removePotionEffect(org.bukkit.potion.PotionEffectType.SLOWNESS);
        m.setAI(true); 
        m.setSilent(false);
        // Boost speed when pursuing
        AttributeInstance speed = m.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (speed != null) {
            speed.setBaseValue(0.35);
        }
    }
    
    @Override
    public boolean hasLineOfSight(Player p, Location t) { 
        return p.hasLineOfSight(t); 
    }
    
    @Override
    public boolean safeTeleport(Monster m, Location d) { 
        return d.getWorld() != null && m.teleport(d); 
    }
    
    @Override
    public boolean isCompatible() { 
        String version = org.bukkit.Bukkit.getServer().getMinecraftVersion();
        return version.startsWith("1.21") || version.contains("26.1"); 
    }
    
    @Override
    public Monster spawnWeeper(Location l) { 
        if (l.getWorld() == null) return null;
        
        Zombie zombie = (Zombie) l.getWorld().spawnEntity(l, EntityType.ZOMBIE);
        zombie.setCustomName("§c§lWeeper");
        zombie.setCustomNameVisible(true);
        zombie.setAI(true);
        zombie.setSilent(false);
        
        // Set base attributes
        AttributeInstance speed = zombie.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (speed != null) {
            speed.setBaseValue(0.15);
        }
        
        AttributeInstance followRange = zombie.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);
        if (followRange != null) {
            followRange.setBaseValue(48.0);
        }
        
        return zombie;
    }
}
