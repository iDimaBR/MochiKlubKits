package com.github.idimabr.mochiklubkits.commands;

import com.github.idimabr.mochiklubkits.manager.PlayerManager;
import com.github.idimabr.mochiklubkits.storage.dao.StorageRepository;
import lombok.AllArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class ResetClassCommand implements CommandExecutor {

    private StorageRepository repository;
    private PlayerManager playerManager;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!(sender instanceof Player)){
            sender.sendMessage("§cApenas jogadores.");
            return false;
        }

        if(!sender.hasPermission("mochiklubkits.reset")){
            sender.sendMessage("§cSem permissão!");
            return false;
        }

        final Player player = (Player) sender;

        repository.deleteUser(player.getUniqueId());
        playerManager.removeCache(player.getUniqueId());
        player.kickPlayer("§cSua classe foi resetada. Entre novamente para escolher sua nova classe!");
        return false;
    }
}
