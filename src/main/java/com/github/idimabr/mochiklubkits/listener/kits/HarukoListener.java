package com.github.idimabr.mochiklubkits.listener.kits;

import com.github.idimabr.mochiklubkits.MochiKlubKits;
import com.github.idimabr.mochiklubkits.enuns.Clicked;
import com.github.idimabr.mochiklubkits.event.ItemKitInteractEvent;
import com.github.idimabr.mochiklubkits.manager.PlayerManager;
import com.github.idimabr.mochiklubkits.models.Kit;
import com.github.idimabr.mochiklubkits.models.PlayerKit;
import com.github.idimabr.mochiklubkits.util.ConfigUtil;
import com.github.idimabr.mochiklubkits.util.TimeUtils;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

@AllArgsConstructor
public class HarukoListener implements Listener {

    private PlayerManager playerManager;
    private ConfigUtil messages;

    @EventHandler
    public void onDrop(ItemKitInteractEvent e) {
        final Player player = e.getPlayer();
        final PlayerKit playerKit = playerManager.findCache(player.getUniqueId());
        final Kit kit = playerKit.getKit();
        if(!playerKit.getKit().getName().equals("Haruko")) return;

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

        playerKit.setCooldown(System.currentTimeMillis() + 2000);

        if (e.getClickType() == Clicked.LEFT) {
            if(kit.isRunning()) return;

            kit.setRunning(true);
            new BukkitRunnable() {
                int counter = (int) kit.getOptions().get("duration-heal");
                @Override
                public void run() {
                    if(counter == 0){
                        this.cancel();
                        kit.setRunning(false);
                        return;
                    }

                    player.removePotionEffect(PotionEffectType.REGENERATION);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 10, 0));

                    final List<Entity> entityList = player.getNearbyEntities(10, 10, 10);
                    for (Entity en : entityList) {
                        if (!(en instanceof LivingEntity)) continue;
                        final LivingEntity entity = (LivingEntity) en;

                        entity.removePotionEffect(PotionEffectType.REGENERATION);
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 10, 0));
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§a§lHABILIDADE ATIVA: §fRegeneração em Área"));
                    }
                    counter--;
                }
            }.runTaskTimer(MochiKlubKits.getPlugin(), 20L, 20L);
        }else{
            Arrow arrow = player.launchProjectile(Arrow.class, player.getLocation().getDirection());
            if((boolean) kit.getOptions().get("arrow-effect-enable")){
                arrow.setBasePotionData(
                        new PotionData(PotionType.valueOf((String) kit.getOptions().get("arrow-effect")))
                );
            }

            arrow.setVelocity(player.getLocation().getDirection().multiply(2.5D));
            arrow.setDamage(5);
            arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            arrow.setKnockbackStrength(2);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§a§lHABILIDADE: §fProjétil rápido"));
            kit.setRunning(false);
        }
    }
}
