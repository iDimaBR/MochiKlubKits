package com.github.idimabr.mochiklubkits;

import com.github.idimabr.mochiklubkits.commands.ChangeClassCommand;
import com.github.idimabr.mochiklubkits.commands.ItemKitCommand;
import com.github.idimabr.mochiklubkits.commands.OpenMenuCommand;
import com.github.idimabr.mochiklubkits.commands.ResetClassCommand;
import com.github.idimabr.mochiklubkits.listener.*;
import com.github.idimabr.mochiklubkits.listener.kits.*;
import com.github.idimabr.mochiklubkits.manager.BarManager;
import com.github.idimabr.mochiklubkits.manager.KitManager;
import com.github.idimabr.mochiklubkits.manager.PlayerManager;
import com.github.idimabr.mochiklubkits.menus.ChooseGui;
import com.github.idimabr.mochiklubkits.models.PlayerKit;
import com.github.idimabr.mochiklubkits.storage.SQLDatabaseFactory;
import com.github.idimabr.mochiklubkits.storage.dao.StorageRepository;
import com.github.idimabr.mochiklubkits.task.MingBarTask;
import com.github.idimabr.mochiklubkits.task.SelectClassTask;
import com.github.idimabr.mochiklubkits.util.ConfigUtil;
import com.google.common.collect.Lists;
import com.henryfabio.sqlprovider.connector.SQLConnector;
import lombok.Getter;
import me.saiintbrisson.minecraft.ViewFrame;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public final class MochiKlubKits extends JavaPlugin {

    private static MochiKlubKits plugin;
    private ConfigUtil config;
    private ConfigUtil messages;
    private KitManager kitManager;
    private PlayerManager playerManager;
    private BarManager barManager;
    private SQLConnector connection;
    private StorageRepository repository;
    private ViewFrame view;
    private List<Player> fallingPlayers = Lists.newArrayList();

    @Override
    public void onLoad() {
        this.config = new ConfigUtil(this, "config.yml");
        this.messages = new ConfigUtil(this, "messages.yml");
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
        for (Map.Entry<UUID, PlayerKit> entry : playerManager.getCache().entrySet()) {
            final UUID uuid = entry.getKey();
            repository.updateUser(uuid);
        }
    }

    private void loadCommands(){
        getCommand("mkit").setExecutor(new ChangeClassCommand(repository, kitManager, messages));
        getCommand("itemkit").setExecutor(new ItemKitCommand(playerManager, messages));
        getCommand("mreset").setExecutor(new ResetClassCommand(repository, playerManager, messages));
        getCommand("mkmenu").setExecutor(new OpenMenuCommand(view, messages));
    }

    private void loadListeners(){
        final PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new AuthListener(playerManager, repository, barManager, view), this);
        pm.registerEvents(new ProtectItemListener(playerManager, config), this);
        pm.registerEvents(new BlockFallDamageListener(playerManager, fallingPlayers), this);

        pm.registerEvents(new MKListener(playerManager, messages, fallingPlayers), this);
        pm.registerEvents(new HarukoListener(playerManager, messages), this);
        pm.registerEvents(new EmikaListener(playerManager, messages, fallingPlayers), this);
        pm.registerEvents(new LirouListener(playerManager, messages), this);
        pm.registerEvents(new MingListener(playerManager, messages), this);

        if(!pm.isPluginEnabled("WorldGuard")){
            getLogger().warning("A dependencia 'WorldGuard' nao foi carregada.");
            pm.disablePlugin(this);
        }

        if(!pm.isPluginEnabled("nLogin")){
            getLogger().warning("A dependencia 'nLogin' nao foi carregada.");
            pm.disablePlugin(this);
        }
    }

    private void loadTasks(){
        new SelectClassTask(playerManager, view).runTaskTimer(this, 20L * 10 , 20L * 10);
        new MingBarTask(playerManager, barManager, config).runTaskTimer(this, 10L, 10L);
    }

    private void loadManagers(){
        this.kitManager = new KitManager(config);
        this.kitManager.loadKitsCache();

        this.playerManager = new PlayerManager(repository);

        this.barManager = new BarManager(config);
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
