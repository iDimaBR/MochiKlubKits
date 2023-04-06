package com.github.idimabr.mochiklubkits.listener;

import com.github.idimabr.mochiklubkits.manager.PlayerManager;
import com.github.idimabr.mochiklubkits.models.Kit;
import com.github.idimabr.mochiklubkits.models.PlayerKit;
import lombok.AllArgsConstructor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import java.util.List;

@AllArgsConstructor
public class BlockFallDamageListener implements Listener {

    private PlayerManager playerManager;
    private List<Player> fallingPlayers;

    @EventHandler
    public void onDamage(EntityDamageEvent e){
        if(e.getEntity().getType() != EntityType.PLAYER) return;
        if(!e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) return;

        final Player player = (Player) e.getEntity();
        final PlayerKit playerKit = playerManager.findCache(player.getUniqueId());
        if(playerKit == null) return;

        final Kit kit = playerKit.getKit();
        if(kit == null) return;

        switch(kit.getName()){
            case "Emika":
                final double reduce = (double) kit.getOptions().get("reduce-jump");
                e.setDamage(e.getDamage() - reduce);
                break;
            case "Mk":
                break;
            default: return;
        }

        if(fallingPlayers.contains(player)){
            e.setCancelled(true);
            fallingPlayers.remove(player);
        }
    }
}
