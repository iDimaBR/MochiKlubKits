package com.github.idimabr.mochiklubkits.listener.kits;

import com.github.idimabr.mochiklubkits.event.ItemKitInteractEvent;
import com.github.idimabr.mochiklubkits.manager.PlayerManager;
import com.github.idimabr.mochiklubkits.models.Kit;
import com.github.idimabr.mochiklubkits.models.PlayerKit;
import com.google.common.collect.Lists;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;

@RequiredArgsConstructor
public class EmikaListener implements Listener {

    private final PlayerManager playerManager;

    private List<Player> falling = Lists.newArrayList();

    @EventHandler
    public void onDrop(ItemKitInteractEvent e){
        final ItemStack item = e.getItem();
        final Player player = e.getPlayer();
        final PlayerKit playerKit = playerManager.findCache(player.getUniqueId());
        final Kit kit = playerKit.getKit();
        if(!kit.getName().equals("Emika")) return;

        if(playerKit.inCooldown()){
            final float left = playerKit.getCooldown() / 1000;
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cEspere " + left + " segundos para usar novamente."));
            return;
        }

        final NBTItem NBT = new NBTItem(item);
        if(NBT.hasKey("useAmount")){
            final int useAmount = NBT.getInteger("useAmount");
            if(useAmount == 1){
                playerKit.setCooldown(System.currentTimeMillis() + 2000);
                NBT.setInteger("useAmount", 3);
            }else{
                NBT.setInteger("useAmount", useAmount - 1);
            }
        }else{
            NBT.setInteger("useAmount", 3);
        }
        NBT.applyNBT(item);

        player.sendMessage("§fUsos: §c" + NBT.getInteger("useAmount"));

        final int distance = (int) kit.getOptions().get("distance");
        final int distanceFly = (int) kit.getOptions().get("distance-fly");

        for (PotionEffect effect : kit.getEffects()) {
            player.removePotionEffect(effect.getType());
            player.addPotionEffect(effect);
        }

        player.setVelocity(player.getLocation().getDirection().multiply(distance).normalize().setY(distanceFly));
        falling.remove(player);
        falling.add(player);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§a§lHABILIDADE: §fPular"));
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e){
        if(e.getEntity().getType() != EntityType.PLAYER) return;
        if(!e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) return;

        final Player player = (Player) e.getEntity();
        final PlayerKit playerKit = playerManager.findCache(player.getUniqueId());
        final Kit kit = playerKit.getKit();
        if(!kit.getName().equals("Emika")) return;

        if(falling.contains(player)){
            e.setCancelled(true);
            falling.remove(player);
            return;
        }

        final double reduce = (double) kit.getOptions().get("reduce-jump");
        e.setDamage(e.getDamage() - reduce);
    }
}
