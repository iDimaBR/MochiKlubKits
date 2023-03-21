package com.github.idimabr.mochiklubkits;

import com.github.idimabr.mochiklubkits.commands.ChangeClassCommand;
import com.github.idimabr.mochiklubkits.commands.ItemKitCommand;
import com.github.idimabr.mochiklubkits.commands.OpenMenuCommand;
import com.github.idimabr.mochiklubkits.commands.ResetClassCommand;
import com.github.idimabr.mochiklubkits.listener.*;
import com.github.idimabr.mochiklubkits.listener.kits.*;
import com.github.idimabr.mochiklubkits.manager.KitManager;
import com.github.idimabr.mochiklubkits.manager.PlayerManager;
import com.github.idimabr.mochiklubkits.menus.ChooseGui;
import com.github.idimabr.mochiklubkits.storage.SQLDatabaseFactory;
import com.github.idimabr.mochiklubkits.storage.dao.StorageRepository;
import com.github.idimabr.mochiklubkits.task.SelectClassTask;
import com.github.idimabr.mochiklubkits.util.ConfigUtil;
import com.henryfabio.sqlprovider.connector.SQLConnector;
import lombok.Getter;
import me.saiintbrisson.minecraft.ViewFrame;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class MochiKlubKits extends JavaPlugin {

    private static MochiKlubKits plugin;
    private ConfigUtil config;
    private KitManager kitManager;
    private PlayerManager playerManager;
    private SQLConnector connection;
    private StorageRepository repository;
    private ViewFrame view;

    @Override
    public void onLoad() {
        this.config = new ConfigUtil(this, "config.yml");
    }

    @Override
    public void onEnable() {
        this.plugin = this;

        view = ViewFrame.of(this, new ChooseGui(this,
                config.getInt("Menus.Config.row", 3),
                config.getString("Menus.Config.title", "Escolha sua classe")
        ));
        view.register();


        loadStorage();
        loadManagers();
        loadListeners();
        loadCommands();
        loadTasks();
    }

    @Override
    public void onDisable() {
        // nada
    }

    private void loadCommands(){
        getCommand("mkit").setExecutor(new ChangeClassCommand(repository, kitManager));
        getCommand("itemkit").setExecutor(new ItemKitCommand(playerManager));
        getCommand("mreset").setExecutor(new ResetClassCommand(repository, playerManager));
        getCommand("mkmenu").setExecutor(new OpenMenuCommand(view));
    }

    private void loadListeners(){
        final PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new AuthListener(playerManager, repository, view), this);
        pm.registerEvents(new ProtectItemListener(playerManager), this);

        pm.registerEvents(new MKListener(playerManager), this);
        pm.registerEvents(new HarukoListener(playerManager), this);
        pm.registerEvents(new EmikaListener(playerManager), this);
        pm.registerEvents(new LirouListener(playerManager), this);
        pm.registerEvents(new MingListener(playerManager), this);

    }

    private void loadTasks(){
        new SelectClassTask(playerManager, view).runTaskTimer(this, 20L * 10 , 20L * 10);
    }

    private void loadManagers(){
        this.kitManager = new KitManager(config);
        this.kitManager.loadKitsCache();

        this.playerManager = new PlayerManager(repository);
    }

    private void loadStorage() {
        connection = SQLDatabaseFactory.createConnector(config.getConfigurationSection("Database"));
        repository = new StorageRepository(this);
        repository.createTable();
    }

    public static MochiKlubKits getPlugin() {
        return plugin;
    }
}
