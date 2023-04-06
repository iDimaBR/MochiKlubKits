package com.github.idimabr.mochiklubkits.commands;

import com.github.idimabr.mochiklubkits.manager.KitManager;
import com.github.idimabr.mochiklubkits.models.Kit;
import com.github.idimabr.mochiklubkits.storage.dao.StorageRepository;
import com.github.idimabr.mochiklubkits.util.ConfigUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
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
public class ChangeClassCommand implements CommandExecutor, TabCompleter {

    private StorageRepository repository;
    private KitManager kitManager;
    private ConfigUtil messages;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!(sender instanceof Player)){
            sender.sendMessage("§cApenas jogadores.");
            return false;
        }

        if(!sender.hasPermission("mochiklubkits.changeclass")){
            sender.sendMessage(messages.getString("Defaults.no-permission").replace("&","§"));
            return false;
        }

        if(args.length == 0){
            sender.sendMessage(messages.getString("Usages-Commands.mkit").replace("&","§"));
            return false;
        }

        final Kit kitCache = kitManager.getCacheKit().get(StringUtils.capitalize(args[0]));
        if(kitCache == null){
            sender.sendMessage(messages.getString("Defaults.class-not-found").replace("&","§"));
            return false;
        }

        Player target = (Player) sender;

        if(args.length == 2){
            target = Bukkit.getPlayer(args[1]);
            if(target == null){
                sender.sendMessage(messages.getString("Defaults.player-not-found").replace("&","§"));
                return false;
            }
        }

        repository.changeClass(target.getUniqueId(), kitCache.getName());
        target.kickPlayer(
                messages.getString("Defaults.class-changed")
                        .replace("&","§")
                        .replace("{kit}", kitCache.getName())
        );
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1){
            return Arrays.asList("Mk","Emika","Lirou","Ming","Haruko");
        }

        if(args.length == 2){
            return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).filter($ -> $.startsWith(args[1])).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
