package com.github.idimabr.mochiklubkits.listener;

import com.github.idimabr.mochiklubkits.enuns.Clicked;
import com.github.idimabr.mochiklubkits.event.ItemKitInteractEvent;
import com.github.idimabr.mochiklubkits.manager.PlayerManager;
import com.github.idimabr.mochiklubkits.models.PlayerKit;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@AllArgsConstructor
public class ProtectItemListener implements Listener {

    private PlayerManager playerManager;

    @EventHandler
    public void onCraft(CraftItemEvent e){
        final ItemStack[] contents = e.getInventory().getContents();
        for (ItemStack content : contents) {
            if(content == null) continue;
            if(content.getType() == Material.AIR) continue;

            final NBTItem NBT = new NBTItem(content);
            if(NBT.hasKey("klubkits")) {
                e.setCancelled(true);
                e.setResult(Event.Result.DENY);
                return;
            }
        }
    }

    @EventHandler
    public void onDragInventory(InventoryDragEvent e){
        final Inventory inventory = e.getInventory();
        if(inventory == null) return;
        if(inventory.getType() == InventoryType.PLAYER) return;

        final ItemStack cursor = e.getOldCursor();
        if(cursor == null) return;
        if(cursor.getType() == Material.AIR) return;

        final NBTItem NBT = new NBTItem(cursor);
        if(!NBT.hasKey("klubkits")) return;

        e.setCancelled(true);
        e.setResult(Event.Result.DENY);
    }



    @EventHandler
    public void onMoveInventory(InventoryClickEvent e){
        final Player player = (Player) e.getWhoClicked();
        final Inventory clickedInventory = e.getClickedInventory();
        final InventoryView openInventory = player.getOpenInventory();
        final ItemStack cursor = e.getCursor();
        final ItemStack item = e.getCurrentItem();
        final ClickType click = e.getClick();

        if(openInventory.getType() != InventoryType.PLAYER){
            if(item != null){
                if(e.getHotbarButton() == 0){
                    e.setResult(Event.Result.DENY);
                    e.setCancelled(true);
                    return;
                }
                if(item.getType() != Material.AIR){
                    if(new NBTItem(item).hasKey("klubkits")){
                        if(click.isShiftClick() || click.isKeyboardClick()){
                            e.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }

        if(cursor == null) return;
        if(cursor.getType() == Material.AIR) return;

        final NBTItem NBT = new NBTItem(cursor);
        if(!NBT.hasKey("klubkits")) return;

        if(clickedInventory == null) return;
        if(clickedInventory.getType() == InventoryType.PLAYER) return;
        if(openInventory.getType() == InventoryType.PLAYER) return;

        e.setCancelled(true);
        e.setResult(Event.Result.DENY);
    }

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
        if(playerKit == null) return;

        final String kitName = NBT.getString("klubkits");
        if(playerKit.getKit() == null) return;
        if(!playerKit.getKit().getName().equalsIgnoreCase(kitName)) return;

        Bukkit.getPluginManager().callEvent(new ItemKitInteractEvent(player, playerKit, item, e.getAction().name().contains("RIGHT") ? Clicked.RIGHT : Clicked.LEFT));
        e.setCancelled(true);
    }
}
