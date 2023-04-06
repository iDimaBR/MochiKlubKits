package com.github.idimabr.mochiklubkits.util;

import com.github.idimabr.mochiklubkits.models.Kit;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

public class Utils {

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

    public static void playParticle(Player player, Particle particle){
        Location origin = player.getEyeLocation();
        Vector direction = origin.getDirection().clone();
        direction.multiply(10);

        direction.normalize();
        for (int i = 0; i < 10; i++) {
            Location loc = origin.add(direction);
            loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, new Particle.DustOptions(Color.fromBGR(255, 255, 255), 1));
        }
    }
}
