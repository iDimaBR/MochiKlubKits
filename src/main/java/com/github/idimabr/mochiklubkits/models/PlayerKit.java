package com.github.idimabr.mochiklubkits.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor @Data
public class PlayerKit {

    private UUID player;
    private Kit kit;
    private long cooldown;

    public boolean inCooldown(){
        return cooldown > System.currentTimeMillis();
    }
}
