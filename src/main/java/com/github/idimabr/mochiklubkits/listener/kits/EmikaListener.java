package com.github.idimabr.mochiklubkits.listener.kits;

import com.github.idimabr.mochiklubkits.MochiKlubKits;
import com.github.idimabr.mochiklubkits.event.ItemKitInteractEvent;
import com.github.idimabr.mochiklubkits.manager.PlayerManager;
import com.github.idimabr.mochiklubkits.models.Kit;
import com.github.idimabr.mochiklubkits.models.PlayerKit;
import com.github.idimabr.mochiklubkits.util.ConfigUtil;
import com.github.idimabr.mochiklubkits.util.TimeUtils;
import com.google.common.collect.Lists;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

@RequiredArgsConstructor
public class EmikaListener implements Listener {

    private final PlayerManager playerManager;
    private final ConfigUtil messages;

    private final List<Player> fallingPlayers;

    @EventHandler
    public void onDrop(ItemKitInteractEvent e){
        final ItemStack item = e.getItem();
        final Player player = e.getPlayer();
        final PlayerKit playerKit = playerManager.findCache(player.getUniqueId());
        final Kit kit = playerKit.getKit();
        if(!kit.getName().equals("Emika")) return;

        if(playerKit.inCooldown()){
            player.spigot().sendMessage(
                    ChatMessageType.ACTION_BAR,
                    new TextComponent(messages.getString("KitDefaults.await-cooldown")
                            .replace("&","§")
                            .replace("{time}", TimeUtils.format(playerKit.getCooldown() - System.currentTimeMillis()))
                    )
            );
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

        final int distance = (int) kit.getOptions().get("distance");
        final int distanceFly = (int) kit.getOptions().get("distance-fly");

        for (PotionEffect effect : kit.getEffects()) {
            player.removePotionEffect(effect.getType());
            player.addPotionEffect(effect);
        }

        player.setVelocity(player.getLocation().getDirection().multiply(distance).normalize().setY(distanceFly));
        fallingPlayers.remove(player);
        fallingPlayers.add(player);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§a§lHABILIDADE: §fPular"));
        player.playSound(player.getLocation(), kit.getSound(), 1, 1);

        new BukkitRunnable(){
            int entitiesCount = 100;
            public void run(){
                if(!fallingPlayers.contains(player) || entitiesCount-- <= 0){
                    this.cancel();
                    return;
                }

                if(kit.getParticleOptions() != null) {
                    player.getWorld().spawnParticle(
                            kit.getParticle(),
                            player.getLocation().getX(),
                            player.getLocation().getY(),
                            player.getLocation().getZ(),
                            0,
                            0.001,
                            1,
                            0,
                            1,
                            kit.getParticleOptions()
                    );
                }else {
                    player.getWorld().spawnParticle(kit.getParticle(), player.getLocation(), 1);
                }
            }
        }.runTaskTimer(MochiKlubKits.getPlugin(), 0, 1);
    }
}
