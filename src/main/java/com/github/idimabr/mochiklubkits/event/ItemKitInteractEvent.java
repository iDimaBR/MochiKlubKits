package com.github.idimabr.mochiklubkits.event;


import com.github.idimabr.mochiklubkits.enuns.Clicked;
import com.github.idimabr.mochiklubkits.models.PlayerKit;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor @Data
public class ItemKitInteractEvent extends Event {

    private final Player player;
    private final PlayerKit playerKit;
    private final ItemStack item;
    private final Clicked clickType;
    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
