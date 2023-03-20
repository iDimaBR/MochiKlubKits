package com.github.idimabr.mochiklubkits.listener.kits;

import com.github.idimabr.mochiklubkits.event.ItemKitInteractEvent;
import com.github.idimabr.mochiklubkits.manager.PlayerManager;
import com.github.idimabr.mochiklubkits.models.Kit;
import com.github.idimabr.mochiklubkits.models.PlayerKit;
import com.github.idimabr.mochiklubkits.storage.dao.StorageRepository;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.AllArgsConstructor;
import me.saiintbrisson.minecraft.ViewFrame;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@AllArgsConstructor
public class MKListener implements Listener {

    private PlayerManager playerManager;

    @EventHandler
    public void onDrop(ItemKitInteractEvent e){
        final Player player = e.getPlayer();
        final PlayerKit playerKit = playerManager.findCache(player.getUniqueId());
        final Kit kit = playerKit.getKit();
        if(!kit.getName().equals("MK")) return;

        if(playerKit.inCooldown()){
            final float left = playerKit.getCooldown() / 1000;
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cEspere " + left + " segundos para usar novamente."));
            return;
        }

        // VARIABLES OPTIONS
        final int lineCross = (int) kit.getOptions().get("line-cross");
        final int range = (int) kit.getOptions().get("range");
        final int nearRange = range / 2;
        final double damage = (double) kit.getOptions().get("damage");
        //

        final List<Block> blocks = player.getLineOfSight(null, range);
        final Block lastBlock = blocks.get(blocks.size() / 2);

        for (Entity en : lastBlock.getWorld().getNearbyEntities(lastBlock.getLocation(), nearRange, nearRange, nearRange)) {
            if(!(en instanceof LivingEntity)) continue;

            final LivingEntity entity = (LivingEntity) en;
            for (Block block : blocks) {
                if(entity.getLocation().distance(block.getLocation()) <= lineCross){
                    entity.setVelocity(entity.getLocation().getDirection().multiply(-3).normalize().setY(1));
                    entity.damage(damage);
                    break;
                }
            }
        }

        for (PotionEffect effect : kit.getEffects()) {
            player.removePotionEffect(effect.getType());
            player.addPotionEffect(effect);
        }

        player.setVelocity(player.getLocation().getDirection().multiply(range));
        player.setFallDistance(0);
        playerKit.setCooldown(System.currentTimeMillis() + 2000);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§a§lHABILIDADE: §fDash furioso"));
    }
}
