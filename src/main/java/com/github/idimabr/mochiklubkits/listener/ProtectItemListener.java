package com.github.idimabr.mochiklubkits.listener;

import com.github.idimabr.mochiklubkits.enuns.Clicked;
import com.github.idimabr.mochiklubkits.event.ItemKitInteractEvent;
import com.github.idimabr.mochiklubkits.manager.PlayerManager;
import com.github.idimabr.mochiklubkits.models.PlayerKit;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@AllArgsConstructor
public class ProtectItemListener implements Listener {

    private PlayerManager playerManager;

    @EventHandler
    public void onDrop(PlayerDropItemEvent e){
        final ItemStack itemStack = e.getItemDrop().getItemStack();
        final NBTItem NBT = new NBTItem(itemStack);

        if(NBT.hasKey("klubkits")) e.setCancelled(true);
    }

    @EventHandler
    public void onDrop(ItemSpawnEvent e){
        final ItemStack itemStack = e.getEntity().getItemStack();
        final NBTItem NBT = new NBTItem(itemStack);

        if(NBT.hasKey("klubkits")) e.setCancelled(true);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        final Player player = e.getEntity();
        final PlayerInventory inventory = player.getInventory();
        for (ItemStack content : inventory.getContents()) {
            if(content == null) continue;
            if(content.getType() == Material.AIR) continue;

            final NBTItem NBT = new NBTItem(content);
            if(NBT.hasKey("klubkits")) inventory.removeItem(content);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e){
        final Player player = e.getPlayer();
        final PlayerKit playerKit = playerManager.findCache(player.getUniqueId());
        player.getInventory().addItem(playerKit.getKit().getItem());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        if(!e.getAction().name().contains("CLICK")) return;
        final Player player = e.getPlayer();
        final ItemStack item = e.getItem();
        if(item == null) return;
        if(item.getType() == Material.AIR) return;

        final NBTItem NBT = new NBTItem(item);
        if(!NBT.hasKey("klubkits")) return;

        final PlayerKit playerKit = playerManager.findCache(player.getUniqueId());

        Bukkit.getPluginManager().callEvent(new ItemKitInteractEvent(player, playerKit, item, e.getAction().name().contains("RIGHT") ? Clicked.RIGHT : Clicked.LEFT));
        e.setCancelled(true);
    }
}
