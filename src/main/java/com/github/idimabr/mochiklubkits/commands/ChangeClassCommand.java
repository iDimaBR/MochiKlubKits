package com.github.idimabr.mochiklubkits.commands;

import com.github.idimabr.mochiklubkits.manager.KitManager;
import com.github.idimabr.mochiklubkits.manager.PlayerManager;
import com.github.idimabr.mochiklubkits.models.Kit;
import com.github.idimabr.mochiklubkits.storage.dao.StorageRepository;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class ChangeClassCommand implements CommandExecutor {

    private StorageRepository repository;
    private KitManager kitManager;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!(sender instanceof Player)){
            sender.sendMessage("§cApenas jogadores.");
            return false;
        }

        if(!sender.hasPermission("mochiklubkits.changeclass")){
            sender.sendMessage("§cSem permissão!");
            return false;
        }

        if(args.length == 0){
            sender.sendMessage("§cUtilize /mkit <kit> <player>");
            return false;
        }

        final Kit kitCache = kitManager.getCacheKit().get(args[0]);
        if(kitCache == null){
            sender.sendMessage("§cClasse não encontrada!");
            return false;
        }

        Player target = (Player) sender;

        if(args.length == 2){
            target = Bukkit.getPlayer(args[1]);
            if(target == null){
                sender.sendMessage("§cJogador não encontrado!");
                return false;
            }
        }

        repository.changeClass(target.getUniqueId(), kitCache.getName());
        target.kickPlayer("§cSua classe foi alterada. Entre novamente!");
        return false;
    }
}
