package com.github.idimabr.mochiklubkits.commands;

import com.github.idimabr.mochiklubkits.manager.PlayerManager;
import com.github.idimabr.mochiklubkits.models.PlayerKit;
import com.github.idimabr.mochiklubkits.util.ConfigUtil;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class ItemKitCommand implements CommandExecutor {

    private PlayerManager playerManager;
    private ConfigUtil messages;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!(sender instanceof Player)){
            sender.sendMessage("§cApenas jogadores.");
            return false;
        }

        if(!sender.hasPermission("mochiklubkits.itemkit")){
            sender.sendMessage(messages.getString("Defaults.no-permission").replace("&","§"));
            return false;
        }

        final Player player = (Player) sender;
        final PlayerInventory inventory = player.getInventory();
        for (ItemStack content : inventory.getContents()) {
            if(content == null) continue;
            if(content.getType() == Material.AIR) continue;

            final NBTItem NBT = new NBTItem(content);
            if(NBT.hasKey("klubkits")) inventory.removeItem(content);
        }

        final PlayerKit playerKit = playerManager.getPlayerKit(player.getUniqueId());
        if(playerKit == null){
            player.sendMessage(messages.getString("Defaults.no-have-class").replace("&","§"));
            return false;
        }
        if(playerKit.getKit() == null){
            player.sendMessage(messages.getString("Defaults.no-have-class").replace("&","§"));
            return false;
        }

        inventory.addItem(playerKit.getKit().getItem());
        return true;
    }
}
