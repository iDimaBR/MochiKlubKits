package com.github.idimabr.mochiklubkits.listener.kits;

import com.github.idimabr.mochiklubkits.event.ItemKitInteractEvent;
import com.github.idimabr.mochiklubkits.manager.PlayerManager;
import com.github.idimabr.mochiklubkits.models.Kit;
import com.github.idimabr.mochiklubkits.models.PlayerKit;
import com.github.idimabr.mochiklubkits.util.ItemBuilder;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class LirouListener implements Listener {

    private PlayerManager playerManager;

    @EventHandler
    public void onDrop(ItemKitInteractEvent e){
        final Player player = e.getPlayer();
        final PlayerKit playerKit = playerManager.findCache(player.getUniqueId());
        final Kit kit = playerKit.getKit();
        if(!kit.getName().equals("Lirou")) return;

        if(playerKit.inCooldown()){
            final float left = playerKit.getCooldown() / 1000;
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cEspere " + left + " segundos para usar novamente."));
            return;
        }

        final ItemStack item = e.getItem();
        final NBTItem NBT = new NBTItem(item);
        if(!NBT.hasKey("elementItem")) return;

        final String element = NBT.getString("elementItem");

        // VARIABLES OPTIONS
        final double damage = (double) kit.getOptions().get("damage-" + element.toLowerCase());
        final int distance = (int) kit.getOptions().get("distance");
        final int nearDistance = distance / 2;
        final int lineCross = (int) kit.getOptions().get("line-cross");

        final ConfigurationSection effects = (ConfigurationSection) kit.getOptions().get("effects-element");

        System.out.println(effects);

        final List<Block> blocks = player.getLineOfSight(null, distance);
        final Block lastBlock = blocks.get(blocks.size() / 2);

        for (Entity en : lastBlock.getWorld().getNearbyEntities(lastBlock.getLocation(), nearDistance, nearDistance, nearDistance)) {
            if(!(en instanceof LivingEntity)) continue;
            if(en.getUniqueId() == player.getUniqueId()) continue;

            final LivingEntity entity = (LivingEntity) en;
            for (Block block : blocks) {
                if(entity.getLocation().distance(block.getLocation()) <= lineCross){
                    final PotionEffect effect = getEffect(effects.getString(element));
                    if(effect == null){
                        entity.setFireTicks(20 * 10);
                    }else{
                        entity.addPotionEffect(effect);
                    }

                    entity.damage(damage);
                    break;
                }
            }
        }

        final ItemStack elementItem = changeElement(item, element);
        player.getInventory().setItemInMainHand(elementItem);

        playerKit.setCooldown(System.currentTimeMillis() + 2000);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§a§lHABILIDADE: §fElemento " + translateElement(element)));
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e){
        final Entity entity = e.getEntity();
        if(entity.getType() != EntityType.PLAYER) return;

        final Player player = (Player) entity;
        final PlayerKit playerKit = playerManager.findCache(player.getUniqueId());
        final Kit kit = playerKit.getKit();

        switch(e.getCause()){
            case FIRE:
                final double reduceFire = (double) kit.getOptions().get("reduce-fire");
                e.setDamage(e.getDamage() - reduceFire);
                return;
            case LAVA:
                final double reduceLava = (double) kit.getOptions().get("reduce-lava");
                e.setDamage(e.getDamage() - reduceLava);
                return;
        }
    }

    private ItemStack changeElement(ItemStack item, String element){
        switch(element.toUpperCase()){
            case "FIRE":
                return new ItemBuilder(item).addNBT("elementItem", "WATER").build();
            case "WATER":
                return new ItemBuilder(item).addNBT("elementItem", "EARTH").build();
            case "EARTH":
                return new ItemBuilder(item).addNBT("elementItem", "AIR").build();
            default:
                return new ItemBuilder(item).addNBT("elementItem", "FIRE").build();
        }
    }

    private PotionEffect getEffect(String string){
        if(string == null) return null;
        if(!string.contains(";")) return null;

        final String[] split = string.split(";");
        if(split.length < 2) return null;

        return new PotionEffect(PotionEffectType.getByName(split[0]), 20 * Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    }

    private String translateElement(String string){
        switch(string){
            case "FIRE":
                return "Fogo";
            case "WATER":
                return "Água";
            case "EARTH":
                return "Terra";
            default:
                return "Ar";
        }
    }
}
