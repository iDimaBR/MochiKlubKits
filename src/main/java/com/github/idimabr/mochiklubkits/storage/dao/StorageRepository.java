package com.github.idimabr.mochiklubkits.storage.dao;

import com.github.idimabr.mochiklubkits.MochiKlubKits;
import com.github.idimabr.mochiklubkits.manager.KitManager;
import com.github.idimabr.mochiklubkits.manager.PlayerManager;
import com.github.idimabr.mochiklubkits.models.Kit;
import com.github.idimabr.mochiklubkits.models.PlayerKit;
import com.github.idimabr.mochiklubkits.storage.adapter.DataAdapter;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import lombok.AllArgsConstructor;
import java.util.UUID;

@AllArgsConstructor
public class StorageRepository {

    private MochiKlubKits plugin;

    public void createTable() {
        this.executor().updateQuery("CREATE TABLE IF NOT EXISTS players(" +
                "`uuid` VARCHAR(36) PRIMARY KEY NOT NULL, " +
                "`kit` VARCHAR(36) NOT NULL," +
                "`cooldown` BIGINT(19) default 0" +
                ")");

    }

    public void updateUser(UUID uuid) {
        final PlayerManager manager = plugin.getPlayerManager();
        final PlayerKit playerKit = manager.getPlayerKit(uuid);
        if(playerKit == null) return;
        if(playerKit.getKit() == null) return;

        this.executor().updateQuery(
                "REPLACE INTO players(" +
                        "uuid," +
                        "kit," +
                        "cooldown" +
                        ") VALUES(?,?,?)",
                statement -> {
                    statement.set(1, uuid.toString());
                    statement.set(2, playerKit.getKit().getName());
                    statement.set(3, playerKit.getCooldown());
                });
    }

    public boolean isDatabased(UUID uuid){
        return this.executor().resultQuery(
                "SELECT * FROM players WHERE uuid = ?;",
                $ -> $.set(1, uuid.toString()),
                SimpleResultSet::next
        );
    }

    public PlayerKit loadUser(UUID uuid) {
        return this.executor().resultOneQuery(
                "SELECT * FROM players WHERE uuid = ?;",
                statement -> statement.set(1, uuid.toString()),
                DataAdapter.class
        );
    }

    private SQLExecutor executor() {
        return new SQLExecutor(plugin.getConnection());
    }

    public void deleteUser(UUID uuid) {
        this.executor().updateQuery("DELETE FROM players WHERE uuid = ?;", statement -> statement.set(1, uuid.toString()));
    }

    public void changeClass(UUID uuid, String kit){
        final PlayerManager manager = plugin.getPlayerManager();
        final KitManager kitManager = plugin.getKitManager();
        final PlayerKit playerKit = manager.getPlayerKit(uuid);
        System.out.println("teste 1");
        if(playerKit == null) return;

        final Kit kitCache = kitManager.getCacheKit().get(kit);
        if(kitCache == null) return;

        playerKit.setKit(kitCache);
        this.executor().updateQuery(
                "REPLACE INTO players(" +
                        "uuid," +
                        "kit," +
                        "cooldown" +
                        ") VALUES(?,?,?)",
                statement -> {
                    statement.set(1, uuid.toString());
                    statement.set(2, kitCache.getName());
                    statement.set(3, playerKit.getCooldown());
                });
    }
}