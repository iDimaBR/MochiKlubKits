package com.github.idimabr.mochiklubkits.util;

import com.github.idimabr.mochiklubkits.MochiKlubKits;
import com.github.idimabr.mochiklubkits.models.Kit;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    private static final List<String> haveBars = new ArrayList<>();

    public static void clearAndGive(Kit kit, Player player){
        final PlayerInventory inventory = player.getInventory();
        for (ItemStack content : inventory.getContents()) {
            if(content == null) continue;
            if(content.getType() == Material.AIR) continue;

            final NBTItem NBT = new NBTItem(content);
            if(NBT.hasKey("klubkits")) inventory.removeItem(content);
        }

        if(kit != null) player.getInventory().addItem(kit.getItem());
    }

    public static void playParticle(Player player, Particle particle, Particle.DustOptions options){
        Location origin = player.getEyeLocation();
        Vector direction = origin.getDirection().clone();
        direction.multiply(10);

        direction.normalize();
        for (int i = 0; i < 10; i++) {
            Location loc = origin.add(direction);
            if(options != null) {
                player.getWorld().spawnParticle(
                        particle,
                        loc.getX(),
                        loc.getY(),
                        loc.getZ(),
                        0,
                        0.001,
                        1,
                        0,
                        1,
                        options
                );
            }else {
                player.getWorld().spawnParticle(particle, loc, 1);
            }
        }
    }

    public static void particleBeam(Player player, Particle particle, Particle.DustOptions options){
        final Location startLoc = player.getEyeLocation();
        final Location particleLoc = startLoc.clone();
        final Vector dir = startLoc.getDirection();
        final Vector vecOffset = dir.clone().multiply(0.5);

        new BukkitRunnable(){
            // The run() function runs every X number of ticks - see below
            int entitiesCount = 100;
            public void run(){
                particleLoc.add(vecOffset);

                if(entitiesCount-- <= 0 || particleLoc.getBlock().getType() != Material.AIR){
                    this.cancel();
                    return;
                }

                if(options != null) {
                    player.getWorld().spawnParticle(
                            particle,
                            particleLoc.getX(),
                            particleLoc.getY(),
                            particleLoc.getZ(),
                            0,
                            0.001,
                            1,
                            0,
                            1,
                            options
                    );
                }else {
                    player.getWorld().spawnParticle(particle, particleLoc, 1);
                }
            }
        }.runTaskTimer(MochiKlubKits.getPlugin(), 0, 1);
    }

    public static void createSpiral(Player player, Particle particle, Particle.DustOptions options) {
        new BukkitRunnable(){
            double phi = 0;
            double counter = 10;
            public void run(){
                phi += Math.PI/8;
                double x;
                double y;
                double z;
                Location loc = player.getLocation();
                for(double t = 0; t <= 2*Math.PI; t += Math.PI/16){
                    for(double i = 0; i <= 1; i++){
                        x = 0.3*(5*Math.PI-t)*0.5*Math.cos(t+phi+i*Math.PI);
                        y = 0.5*t;
                        z = 0.3*(5*Math.PI-t)*0.5*Math.sin(t+phi+i*Math.PI);
                        loc.add(x,y,z);

                        if(options != null) {
                            player.getWorld().spawnParticle(
                                    particle,
                                    loc.getX(),
                                    loc.getY(),
                                    loc.getZ(),
                                    0,
                                    0.001,
                                    1,
                                    0,
                                    1,
                                    options
                            );
                        }else {
                            player.getWorld().spawnParticle(particle, loc, 1);
                        }

                        loc.subtract(x,y,z);
                    }
                }
                if(counter-- <= 0){
                    this.cancel();
                }
            }
        }.runTaskTimer(MochiKlubKits.getPlugin(), 0, 1);
    }
}
