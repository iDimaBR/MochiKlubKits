package com.github.idimabr.mochiklubkits;

import com.github.idimabr.mochiklubkits.manager.KitManager;
import com.github.idimabr.mochiklubkits.util.ConfigUtil;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class MochiKlubKits extends JavaPlugin {

    private ConfigUtil config;
    private KitManager kitManager;

    @Override
    public void onLoad() {
        this.config = new ConfigUtil(this, "config.yml");
    }

    @Override
    public void onEnable() {
        loadManagers();
    }

    @Override
    public void onDisable() {

    }

    private void loadManagers(){
        this.kitManager = new KitManager(config);
        this.kitManager.loadKitsCache();
    }
}
