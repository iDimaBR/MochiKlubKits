package com.github.idimabr.mochiklubkits.task;

import com.github.idimabr.mochiklubkits.manager.BarManager;
import com.github.idimabr.mochiklubkits.manager.PlayerManager;
import com.github.idimabr.mochiklubkits.menus.ChooseGui;
import com.github.idimabr.mochiklubkits.models.Kit;
import com.github.idimabr.mochiklubkits.models.PlayerKit;
import com.github.idimabr.mochiklubkits.util.ConfigUtil;
import com.nickuc.login.api.nLoginAPI;
import lombok.AllArgsConstructor;
import me.saiintbrisson.minecraft.ViewFrame;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@AllArgsConstructor
public class MingBarTask extends BukkitRunnable {

    private PlayerManager playerManager;
    private BarManager barManager;

    private ConfigUtil config;
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            final PlayerKit playerKit = playerManager.findCache(player.getUniqueId());
            if(playerKit == null) continue;
            final Kit kit = playerKit.getKit();
            if(kit == null) continue;
            if(!kit.getName().equals("Ming")) continue;
            if(!nLoginAPI.getApi().isAuthenticated(player.getName())) continue;

            final BossBar bar = barManager.getBar(player);
            if(bar == null){
                continue;
            }
            final Entity attackedEntity = kit.getLastAttackEntity();
            if(attackedEntity == null){
                bar.removePlayer(player);
                kit.setLastAttackEntity(null);
                continue;
            }

            if(!(attackedEntity instanceof LivingEntity)){
                bar.removePlayer(player);
                kit.setLastAttackEntity(null);
                continue;
            }

            final LivingEntity living = (LivingEntity) attackedEntity;
            if(living.getHealth() <= 0){
                bar.removePlayer(player);
                kit.setLastAttackEntity(null);
                continue;
            }

            if(attackedEntity.getWorld() != player.getWorld()){
                bar.removePlayer(player);
                kit.setLastAttackEntity(null);
                continue;
            }

            if(attackedEntity.getLocation().distance(player.getLocation()) > 20){
                bar.removePlayer(player);
                kit.setLastAttackEntity(null);
                continue;
            }

            String attacked;
            if(attackedEntity.getType() != EntityType.PLAYER){
                attacked = attackedEntity.getType().name();
            }else{
                attacked = attackedEntity.getName();
            }

            bar.setTitle(
                    config.getString("Kits.Ming.config.bar-title.attack", "&f{attacked} &atem &f{health}/{maxhealth} &ade vida")
                            .replace("&","§")
                            .replace("{attacked}", attacked)
                            .replace("{health}", ((int) living.getHealth()) + "")
                            .replace("{maxhealth}", ((int) living.getMaxHealth()) + "")
            );
            bar.addPlayer(player);
        }
    }
}
