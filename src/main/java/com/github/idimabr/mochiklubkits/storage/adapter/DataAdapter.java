package com.github.idimabr.mochiklubkits.storage.adapter;

import com.github.idimabr.mochiklubkits.MochiKlubKits;
import com.github.idimabr.mochiklubkits.manager.KitManager;
import com.github.idimabr.mochiklubkits.manager.PlayerManager;
import com.github.idimabr.mochiklubkits.models.Kit;
import com.github.idimabr.mochiklubkits.models.PlayerKit;
import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import java.util.UUID;

public class DataAdapter implements SQLResultAdapter<PlayerKit> {
    @Override
    public PlayerKit adaptResult(SimpleResultSet rs) {
        final MochiKlubKits plugin = MochiKlubKits.getPlugin();
        final PlayerManager userManager = plugin.getPlayerManager();
        final KitManager kitManager = plugin.getKitManager();

        final UUID uuid = UUID.fromString(rs.get("uuid"));
        final long cooldown = rs.get("cooldown");

        final Kit actualKit = kitManager.cloneFromCache(rs.get("kit"));

        final PlayerKit playerKit = new PlayerKit(uuid, actualKit, cooldown);
        userManager.addCache(uuid, playerKit);

        return playerKit;
    }
}
