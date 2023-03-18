package com.github.idimabr.mochiklubkits.storage.dao;

import com.github.flexstore.FlexEconomy;
import com.github.flexstore.manager.UserManager;
import com.github.flexstore.model.UserAccount;
import com.github.flexstore.storage.adapter.DataAdapter;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class StorageRepository {

    private FlexEconomy plugin;

    public void createTable() {
        this.executor().updateQuery("CREATE TABLE IF NOT EXISTS accounts(" +
                "`uuid` varchar(36) PRIMARY KEY NOT NULL, " +
                "`coins` double default 0" +
                ")");
    }

    public void updateUser(UUID uuid, boolean bypassDirt) {
        final UserManager userManager = plugin.getUserManager();
        final UserAccount account = userManager.getAccount(uuid);
        System.out.println(userManager.getCache());
        if(account == null) return;
        System.out.println("1");
        if(!bypassDirt && !account.isDirty()) return;
        System.out.println("2");

        account.setDirty(false);
        this.executor().updateQuery(
                "REPLACE INTO accounts(" +
                        "uuid," +
                        "coins" +
                        ") VALUES(?,?)",
                    statement -> {
                        statement.set(1, uuid.toString());
                        statement.set(2, account.getCoins());
                    });
    }

    public UserAccount loadUser(UUID uuid) {
        return this.executor().resultOneQuery(
                "SELECT * FROM accounts WHERE uuid = ?;",
                statement -> statement.set(1, uuid.toString()),
                DataAdapter.class
        );
    }

    private SQLExecutor executor() {
        return new SQLExecutor(plugin.getConnection());
    }

}