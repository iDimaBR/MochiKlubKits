package com.github.idimabr.mochiklubkits.manager;

import com.github.idimabr.mochiklubkits.util.ConfigUtil;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import java.util.Map;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class BarManager {

    private final ConfigUtil config;

    private Map<UUID, BossBar> bars = Maps.newHashMap();

    public void initBar(Player player){
        final BossBar bar = Bukkit.createBossBar("§c", BarColor.PURPLE, BarStyle.SOLID);
        bar.addPlayer(player);
        bars.put(player.getUniqueId(), bar);
    }

    public BossBar getBar(Player player){
        return bars.get(player.getUniqueId());
    }

    public void removeBar(Player player){
        bars.remove(player.getUniqueId());
    }
}
