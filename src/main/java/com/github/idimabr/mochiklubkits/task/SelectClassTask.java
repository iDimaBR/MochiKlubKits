package com.github.idimabr.mochiklubkits.task;

import com.github.idimabr.mochiklubkits.manager.PlayerManager;
import com.github.idimabr.mochiklubkits.menus.ChooseGui;
import com.github.idimabr.mochiklubkits.models.PlayerKit;
import com.nickuc.login.api.nLoginAPI;
import lombok.AllArgsConstructor;
import me.saiintbrisson.minecraft.ViewFrame;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@AllArgsConstructor
public class SelectClassTask extends BukkitRunnable {

    private PlayerManager playerManager;
    private ViewFrame frame;

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            final PlayerKit playerKit = playerManager.findCache(player.getUniqueId());
            if(playerKit != null) continue;
            if(!nLoginAPI.getApi().isAuthenticated(player.getName())) continue;

            player.closeInventory();
            frame.open(ChooseGui.class, player);
        }
    }
}
