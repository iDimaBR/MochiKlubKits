package com.github.idimabr.mochiklubkits.models;


import lombok.Data;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Map;

@Data
public class Kit {

    private String name;
    private String permission;
    private List<PotionEffect> effects;
    private Map<String, Object> options;
    private ItemStack icon;
    private Particle particle;

    public Kit(String name) {
        this.name = name;
    }

    public Kit(String name, String permission) {
        this.name = name;
        this.permission = permission;
    }

    public Kit(String name, String permission, List<PotionEffect> effects) {
        this.name = name;
        this.permission = permission;
        this.effects = effects;
    }

    public Kit(String name, String permission, List<PotionEffect> effects, Map<String, Object> options) {
        this.name = name;
        this.permission = permission;
        this.effects = effects;
        this.options = options;
    }

    public Kit(String name, String permission, List<PotionEffect> effects, Map<String, Object> options, ItemStack icon) {
        this.name = name;
        this.permission = permission;
        this.effects = effects;
        this.options = options;
        this.icon = icon;
    }

    public Kit(String name, String permission, List<PotionEffect> effects, Map<String, Object> options, ItemStack icon, Particle particle) {
        this.name = name;
        this.permission = permission;
        this.effects = effects;
        this.options = options;
        this.icon = icon;
        this.particle = particle;
    }
}
