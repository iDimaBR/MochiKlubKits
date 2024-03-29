package com.github.idimabr.mochiklubkits.manager;

import com.github.idimabr.mochiklubkits.MochiKlubKits;
import com.github.idimabr.mochiklubkits.models.Kit;
import com.github.idimabr.mochiklubkits.util.ConfigUtil;
import com.github.idimabr.mochiklubkits.util.ItemBuilder;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class KitManager {

    private final ConfigUtil config;

    private Map<String, Kit> cacheKit = Maps.newHashMap();

    public void loadKitsCache() {
        final ConfigurationSection mainSection = config.getConfigurationSection("Kits");
        for (String name : mainSection.getKeys(false)) {
            final ConfigurationSection kitSection = mainSection.getConfigurationSection(name);
            if (kitSection == null) continue;

            final Kit kit = new Kit(name);

            if (kitSection.isSet("permission"))
                kit.setPermission(kitSection.getString("permission"));


            if (kitSection.isSet("effects"))
                kit.setEffects(transformEffects(kitSection.getStringList("effects")));


            if (kitSection.isSet("config"))
                kit.setOptions(kitSection.getConfigurationSection("config").getValues(false));


            if (kitSection.isSet("itemstack")) {
                final ItemBuilder builder = new ItemBuilder(kitSection.getString("itemstack.material", "BEDROCK"));
                builder.addNBT("klubkits", name);
                if (name.equals("Lirou")) builder.addNBT("elementItem", "AIR");

                if (kitSection.isSet("itemstack.name"))
                    builder.setName(kitSection.getString("itemstack.name").replace("&", "§"));
                if (kitSection.isSet("itemstack.data"))
                    builder.setDurability((short) kitSection.getInt("itemstack.data"));
                if (kitSection.isSet("itemstack.lore")) {
                    final List<String> lore = kitSection.getStringList("itemstack.lore");
                    lore.replaceAll($ -> $.replace("&", "§"));
                    builder.setLore(lore);
                }

                kit.setItem(builder.build());
            }

            if (kitSection.isSet("particle")) {
                kit.setParticle(
                        Arrays.stream(Particle.values())
                                .filter($ -> $.name().equals(kitSection.getString("particle", "FIREWORKS_SPARK").toUpperCase()))
                                .findAny()
                                .orElse(Particle.FIREWORKS_SPARK));


                if (kitSection.isSet("particle-options")) {
                    final Particle.DustOptions dustOptions = new Particle.DustOptions(
                            Color.fromRGB(
                                    kitSection.getInt("particle-options.red", 255),
                                    kitSection.getInt("particle-options.green", 255),
                                    kitSection.getInt("particle-options.blue", 255)
                            ),
                            1
                    );
                    kit.setParticleOptions(dustOptions);
                }
            }

            if(kitSection.isSet("sound")){
                kit.setSound(Enum.valueOf(Sound.class, kitSection.getString("sound", "BLOCK_ANVIL_LAND")));
            }else{
                kit.setSound(Sound.BLOCK_LEVER_CLICK);
            }

            JavaPlugin.getPlugin(MochiKlubKits.class).getLogger().info("Kit '" + name + "' carregado em cache.");
            cacheKit.put(name, kit);
        }
    }

    public Kit cloneFromCache(String name){
        final Kit kit = cacheKit.get(name);
        return new Kit(kit.getName(), kit.getPermission(), kit.getEffects(), kit.getOptions(), kit.getItem(), kit.getParticle(), kit.getParticleOptions(), kit.getSound());
    }

    private List<PotionEffect> transformEffects(List<String> list) {
        final List<PotionEffect> effects = new ArrayList<>();
        for (String string : list) {
            if(!string.contains(";")) continue;

            final String[] split = string.split(";");
            if(split.length < 2) continue;

            effects.add(new PotionEffect(PotionEffectType.getByName(split[0]), 20 * Integer.parseInt(split[1]), Integer.parseInt(split[2])));
        }
        return effects;
    }
}
