package com.github.idimabr.mochiklubkits.menus;

import com.github.idimabr.mochiklubkits.MochiKlubKits;
import com.github.idimabr.mochiklubkits.manager.BarManager;
import com.github.idimabr.mochiklubkits.manager.KitManager;
import com.github.idimabr.mochiklubkits.manager.PlayerManager;
import com.github.idimabr.mochiklubkits.models.Kit;
import com.github.idimabr.mochiklubkits.models.PlayerKit;
import com.github.idimabr.mochiklubkits.storage.dao.StorageRepository;
import com.github.idimabr.mochiklubkits.util.ConfigUtil;
import com.github.idimabr.mochiklubkits.util.ItemBuilder;
import com.github.idimabr.mochiklubkits.util.Utils;
import lombok.AllArgsConstructor;
import me.saiintbrisson.minecraft.OpenViewContext;
import me.saiintbrisson.minecraft.View;
import me.saiintbrisson.minecraft.ViewContext;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.stream.Collectors;

@AllArgsConstructor
public class ChooseGui extends View {

    private final MochiKlubKits plugin;

    public ChooseGui(MochiKlubKits plugin, int rows, String title) {
        super(rows, title);
        setCancelOnClick(true);
        this.plugin = plugin;
    }
    @Override
    public void onRender(@NotNull ViewContext context){
        final Player player = context.getPlayer();
        final FileConfiguration menus = plugin.getConfig();
        final ConfigUtil messages = plugin.getMessages();
        final StorageRepository repository = plugin.getRepository();
        final PlayerManager playerManager = plugin.getPlayerManager();
        final KitManager kitManager = plugin.getKitManager();
        final BarManager barManager = plugin.getBarManager();

        for (String key : menus.getConfigurationSection("Menus").getKeys(false)) {
            final ConfigurationSection section = menus.getConfigurationSection("Menus." + key);
            if(section == null) continue;
            if(!section.isSet("slot")) continue;
            if(!section.isSet("material")) continue;

            final int slot = section.getInt("slot");
            if(slot > this.getSize()) continue;

            final Kit kit = kitManager.getCacheKit().get(key);
            if(kit == null) continue;

            final ItemBuilder builder = new ItemBuilder(section.getString("material"));
            if(section.isSet("data")) builder.setDurability((short) section.getInt("data"));
            if(section.isSet("owner")) builder.setSkullOwner(player.getName());
            if(section.isSet("amount")) builder.setAmount(section.getInt("amount", 1));
            if(section.isSet("name")) builder.setName(section.getString("name").replace("{player}", player.getName()).replace("&","§"));
            if(section.isSet("lore")) builder.setLore(section.getStringList("lore").stream()
                    .map($ -> $.replace("&", "§").replace("{permission}", kit.getPermission())).collect(Collectors.toList()));

            context.slot(slot, builder.build()).onClick(e -> {
                playerManager.addCache(player.getUniqueId(), new PlayerKit(player.getUniqueId(), kit, 0));
                Utils.clearAndGive(kit, player);
                repository.updateUser(player.getUniqueId());

                switch(kit.getName()){
                    case "Haruko":
                        player.setMaxHealth(24);
                        break;
                    case "Ming":
                        barManager.initBar(player);
                        break;
                    default:
                        player.setMaxHealth(20);
                        break;
                }

                player.sendMessage(
                        messages.getString("Defaults.select-class")
                        .replace("&","§")
                        .replace("{kit}", key)
                );
                player.closeInventory();
            });
        }
    }


}

