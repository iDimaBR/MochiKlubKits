package com.github.idimabr.mochiklubkits.manager;

import com.github.idimabr.mochiklubkits.models.PlayerKit;
import com.github.idimabr.mochiklubkits.storage.dao.StorageRepository;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class PlayerManager {

    private final StorageRepository repository;
    private Map<UUID, PlayerKit> cache = Maps.newHashMap();

    public PlayerKit getPlayerKit(UUID uuid){
        PlayerKit playerKit = cache.get(uuid);
        if(playerKit == null){
            playerKit = repository.loadUser(uuid);
            if(playerKit == null) {
                playerKit = new PlayerKit(uuid, null, 0);
                cache.put(uuid, playerKit);
            }
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

    public Map<UUID, PlayerKit> getCache() {
        return cache;
    }
}
