package com.github.idimabr.mochiklubkits.listener;

import com.github.idimabr.mochiklubkits.manager.PlayerManager;
import com.github.idimabr.mochiklubkits.menus.ChooseGui;
import com.github.idimabr.mochiklubkits.models.Kit;
import com.github.idimabr.mochiklubkits.models.PlayerKit;
import com.github.idimabr.mochiklubkits.storage.dao.StorageRepository;
import com.github.idimabr.mochiklubkits.util.Utils;
import com.nickuc.login.api.event.bukkit.auth.AuthenticateEvent;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.AllArgsConstructor;
import me.saiintbrisson.minecraft.ViewFrame;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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

        Utils.clearAndGive(null, player);
        if(repository.isDatabased(uniqueId)){
            final PlayerKit playerKit = repository.loadUser(uniqueId);
            if(playerKit == null) return;
            final Kit kit = playerKit.getKit();
            if(kit == null) return;

            if(kit.getName().equals("Haruko")){
                player.setMaxHealth(24);
            }else{
                player.setMaxHealth(20);
            }
            Utils.clearAndGive(kit, player);
        }else{
            frame.open(ChooseGui.class, player);
        }
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
