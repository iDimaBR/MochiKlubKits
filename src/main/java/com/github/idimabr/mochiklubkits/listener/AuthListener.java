package com.github.idimabr.mochiklubkits.listener;

import com.github.idimabr.mochiklubkits.manager.PlayerManager;
import com.github.idimabr.mochiklubkits.menus.ChooseGui;
import com.github.idimabr.mochiklubkits.models.PlayerKit;
import com.github.idimabr.mochiklubkits.storage.dao.StorageRepository;
import com.nickuc.login.api.event.bukkit.auth.AuthenticateEvent;
import lombok.AllArgsConstructor;
import me.saiintbrisson.minecraft.ViewFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

@AllArgsConstructor
public class AuthListener implements Listener {

    private PlayerManager playerManager;
    private StorageRepository repository;
    private ViewFrame frame;

    @EventHandler
    public void onJoin(AuthenticateEvent e){
        final Player player = e.getPlayer();
        final UUID uniqueId = player.getUniqueId();
        if(!repository.isDatabased(uniqueId)){
            frame.open(ChooseGui.class, player);
            return;
        }

        final PlayerKit playerKit = repository.loadUser(uniqueId);
        if(playerKit.getKit().getName().equals("Haruko")) player.setMaxHealth(24);
        player.getInventory().addItem(playerKit.getKit().getItem());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        final Player player = e.getPlayer();
        final UUID uniqueId = player.getUniqueId();
        final PlayerKit playerKit = playerManager.findCache(uniqueId);
        if(playerKit == null) return;

        player.getInventory().removeItem(playerKit.getKit().getItem());
        repository.updateUser(uniqueId);
        playerManager.removeCache(uniqueId);
    }
}
