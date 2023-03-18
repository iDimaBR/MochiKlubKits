package com.github.idimabr.mochiklubkits.storage.adapter;

import com.github.flexstore.FlexEconomy;
import com.github.flexstore.manager.UserManager;
import com.github.flexstore.model.UserAccount;
import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;

import java.util.ArrayList;
import java.util.UUID;

public class DataAdapter implements SQLResultAdapter<UserAccount> {
    @Override
    public UserAccount adaptResult(SimpleResultSet rs) {
        final FlexEconomy plugin = FlexEconomy.getInstance();
        final UserManager userManager = plugin.getUserManager();

        final UUID uuid = UUID.fromString(rs.get("uuid"));
        final double coins = rs.get("coins");

        final UserAccount account = new UserAccount(coins, new ArrayList<>(), false);
        userManager.add(uuid, account);
        return account;
    }
}
