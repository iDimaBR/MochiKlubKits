package com.github.idimabr.mochiklubkits.manager;

import com.github.idimabr.mochiklubkits.MochiKlubKits;
import com.github.idimabr.mochiklubkits.models.Kit;
import com.github.idimabr.mochiklubkits.util.ConfigUtil;
import com.github.idimabr.mochiklubkits.util.ItemBuilder;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class KitManager {

    private final ConfigUtil config;

    private Map<String, Kit> cacheKit = Maps.newHashMap();

    public void loadKitsCache(){
        final ConfigurationSection mainSection = config.getConfigurationSection("Kits");
        for (String name : mainSection.getKeys(false)) {
            final ConfigurationSection kitSection = mainSection.getConfigurationSection(name);
            if(kitSection == null) continue;

            final Kit kit = new Kit(name);

            if(kitSection.isSet("permission"))
                kit.setPermission(kitSection.getString("permission"));


            if(kitSection.isSet("effects"))
                kit.setEffects(transformEffects(kitSection.getStringList("effects")));


            if(kitSection.isSet("config"))
                kit.setOptions(kitSection.getConfigurationSection("config").getValues(false));


            if(kitSection.isSet("itemstack")){
                final ItemBuilder builder = new ItemBuilder(kitSection.getString("itemstack.material", "BEDROCK"));
                builder.addNBT("klubkits","");

                if(kitSection.isSet("itemstack.name")) builder.setName(kitSection.getString("itemstack.name").replace("&","§"));
                if(kitSection.isSet("itemstack.data")) builder.setDurability((short) kitSection.getInt("itemstack.data"));
                if(kitSection.isSet("itemstack.lore")){
                    final List<String> lore = kitSection.getStringList("itemstack.lore");
                    lore.replaceAll($ -> $.replace("&","§"));
                    builder.setLore(lore);
                }

                kit.setItem(builder.build());
            }

            if(kitSection.isSet("particle"))
                kit.setParticle(Particle.valueOf(kitSection.getString("particle", "PARTICLE_DUST")));


            JavaPlugin.getPlugin(MochiKlubKits.class).getLogger().info("Kit '" + name + "' carregado em cache.");
            cacheKit.put(name, kit);
        }
    }

    private List<PotionEffect> transformEffects(List<String> list){
        final List<PotionEffect> effects = new ArrayList<>();
        for (String string : list) {
            if(!string.contains(";")) continue;

            final String[] split = string.split(";");
            if(split.length < 2) continue;

            effects.add(new PotionEffect(PotionEffectType.getByName(split[0]), 20 * Integer.parseInt(split[1]), Integer.parseInt(split[2])));
        }
        return effects;
    }

    public Kit cloneFromCache(String name){
        final Kit kit = cacheKit.get(name);
        return new Kit(kit.getName(), kit.getPermission(), kit.getEffects(), kit.getOptions(), kit.getItem(), kit.getParticle());
    }
}
