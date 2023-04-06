package com.github.idimabr.mochiklubkits.listener.kits;

import com.github.idimabr.mochiklubkits.MochiKlubKits;
import com.github.idimabr.mochiklubkits.event.ItemKitInteractEvent;
import com.github.idimabr.mochiklubkits.manager.PlayerManager;
import com.github.idimabr.mochiklubkits.models.Kit;
import com.github.idimabr.mochiklubkits.models.PlayerKit;
import com.github.idimabr.mochiklubkits.util.ConfigUtil;
import com.github.idimabr.mochiklubkits.util.TimeUtils;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

@AllArgsConstructor
public class MingListener implements Listener {

    private PlayerManager playerManager;
    private ConfigUtil messages;

    @EventHandler
    public void onDrop(ItemKitInteractEvent e){
        final Player player = e.getPlayer();
        final PlayerKit playerKit = playerManager.findCache(player.getUniqueId());
        final Kit kit = playerKit.getKit();
        if(!kit.getName().equals("Ming")) return;

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

        // VARIABLES OPTIONS
        final int distance = (int) kit.getOptions().get("distance");
        final double damage = (double) kit.getOptions().get("damage");
        //

        kit.setRunning(true);
        new BukkitRunnable() {
            int counter = (int) kit.getOptions().get("duration");
            @Override
            public void run() {
                if(counter == 0){
                    this.cancel();
                    kit.setRunning(false);
                    return;
                }

                final List<Entity> entityList = player.getNearbyEntities(distance, distance, distance);
                for (Entity en : entityList) {
                    if (!(en instanceof LivingEntity)) continue;
                    if(en.getUniqueId() == player.getUniqueId()) continue;
                    final LivingEntity entity = (LivingEntity) en;

                    entity.setVelocity(entity.getLocation().getDirection().multiply(-3).normalize().setY(1));
                    entity.damage(damage);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§a§lHABILIDADE ATIVA: §fFuração raivoso"));
                }
                counter--;
            }
        }.runTaskTimer(MochiKlubKits.getPlugin(), 20L, 20L);

        playerKit.setCooldown(System.currentTimeMillis() + 2000);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§a§lHABILIDADE: §fFuração raivoso"));
    }
}
