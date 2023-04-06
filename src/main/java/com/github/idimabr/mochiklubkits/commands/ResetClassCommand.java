package com.github.idimabr.mochiklubkits.commands;

import com.github.idimabr.mochiklubkits.manager.PlayerManager;
import com.github.idimabr.mochiklubkits.storage.dao.StorageRepository;
import com.github.idimabr.mochiklubkits.util.ConfigUtil;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class ResetClassCommand implements CommandExecutor {

    private StorageRepository repository;
    private PlayerManager playerManager;
    private ConfigUtil messages;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!sender.hasPermission("mochiklubkits.reset")){
            sender.sendMessage(messages.getString("Defaults.no-permission").replace("&","§"));
            return false;
        }

        if(args.length == 1){
            final Player target = Bukkit.getPlayer(args[0]);
            if(target == null){
                sender.sendMessage(messages.getString("Defaults.player-not-found").replace("&","§"));
                return false;
            }

            repository.deleteUser(target.getUniqueId());
            playerManager.removeCache(target.getUniqueId());
            target.kickPlayer(messages.getString("Defaults.class-reset").replace("&","§"));
            return true;
        }

        if(!(sender instanceof Player)){
            sender.sendMessage("§cApenas jogadores podem executar esse comando.");
            return false;
        }

        final Player player = (Player) sender;

        repository.deleteUser(player.getUniqueId());
        playerManager.removeCache(player.getUniqueId());
        player.kickPlayer(messages.getString("Defaults.class-reset").replace("&","§"));
        return false;
    }
}
