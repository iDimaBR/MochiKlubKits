package com.github.idimabr.mochiklubkits.manager;

import com.github.idimabr.mochiklubkits.models.PlayerKit;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private Map<UUID, PlayerKit> cache = Maps.newHashMap();

    public PlayerKit getPlayerKit(UUID uuid){
        PlayerKit playerKit = cache.get(uuid);
        if(playerKit == null){
            // ** PUXA DO SQL **
            // if(account == null) {
            // ** SE FOR NULL DO SQL, CRIAR UM NOVO **
            //    cache.put(uuid, new PlayerKit());
            //}
        }
        return playerKit;
    }

    public PlayerKit findCache(UUID uuid){
        return cache.get(uuid);
    }

    public void addCache(UUID uuid, PlayerKit playerKit) {
        this.cache.put(uuid, playerKit);
    }

    public void removeCache(UUID uuid) {
        this.cache.remove(uuid);
    }
}
