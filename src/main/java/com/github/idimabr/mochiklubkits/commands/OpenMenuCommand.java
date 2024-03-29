package com.github.idimabr.mochiklubkits.commands;

import com.github.idimabr.mochiklubkits.menus.ChooseGui;
import com.github.idimabr.mochiklubkits.util.ConfigUtil;
import lombok.AllArgsConstructor;
import me.saiintbrisson.minecraft.ViewFrame;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class OpenMenuCommand implements CommandExecutor {

    private ViewFrame frame;
    private ConfigUtil messages;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!(sender instanceof Player)){
            sender.sendMessage("§cApenas jogadores.");
            return false;
        }

        if(!sender.hasPermission("mochiklubkits.openmenu")){
            sender.sendMessage(messages.getString("Defaults.no-permission").replace("&","§"));
            return false;
        }

        final Player player = (Player) sender;
        frame.open(ChooseGui.class, player);
        return false;
    }
}
