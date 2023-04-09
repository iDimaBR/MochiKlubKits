package com.github.idimabr.mochiklubkits.listener.kits;

import com.github.idimabr.mochiklubkits.MochiKlubKits;
import com.github.idimabr.mochiklubkits.enuns.Clicked;
import com.github.idimabr.mochiklubkits.event.ItemKitInteractEvent;
import com.github.idimabr.mochiklubkits.manager.PlayerManager;
import com.github.idimabr.mochiklubkits.models.Kit;
import com.github.idimabr.mochiklubkits.models.PlayerKit;
import com.github.idimabr.mochiklubkits.util.ConfigUtil;
import com.github.idimabr.mochiklubkits.util.TimeUtils;
import com.github.idimabr.mochiklubkits.util.Utils;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;
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
    public void onUse(ItemKitInteractEvent e) {
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
        player.playSound(player.getLocation(), kit.getSound(), 1, 1);

        if (e.getClickType() == Clicked.LEFT) {
            if(kit.isRunning()) return;
            final int duration = (int) kit.getOptions().get("duration-heal");
            Utils.createSpiral(player, kit.getParticle(), kit.getParticleOptions());
            kit.setRunning(true);
            new BukkitRunnable() {
                int counter = duration;
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

            new BukkitRunnable(){
                int entitiesCount = 100;
                public void run(){
                    final Location location = arrow.getLocation();
                    if(arrow.isDead() || arrow.isInBlock() || arrow.isInWater() || arrow.isOnGround() || entitiesCount-- <= 0){
                        this.cancel();
                        return;
                    }
                    if(kit.getParticleOptions() != null) {
                        player.getWorld().spawnParticle(
                                kit.getParticle(),
                                location.getX(),
                                location.getY(),
                                location.getZ(),
                                0,
                                0.001,
                                1,
                                0,
                                1,
                                kit.getParticleOptions()
                        );
                    }else {
                        player.getWorld().spawnParticle(kit.getParticle(), location, 1);
                    }
                }
            }.runTaskTimer(MochiKlubKits.getPlugin(), 0, 1);

            arrow.setVelocity(player.getLocation().getDirection().multiply(2.5D));
            arrow.setDamage(5);
            arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            arrow.setKnockbackStrength(2);
            arrow.setMetadata("haruko", new FixedMetadataValue(MochiKlubKits.getPlugin(), ""));
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§a§lHABILIDADE: §fProjétil rápido"));
            kit.setRunning(false);
        }
    }

    @EventHandler
    public void onHit(ProjectileHitEvent e){
        final Projectile entity = e.getEntity();
        if(!entity.hasMetadata("haruko")) return;
        entity.remove();
    }
}
