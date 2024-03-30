package me.crazycranberry.minecrafttcg.events;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlayerHealedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Stadium stadium;
    private final Player player;

    public PlayerHealedEvent(Stadium stadium, Player player) {
        this.stadium = stadium;
        this.player = player;
    }

    public Stadium stadium() {
        return stadium;
    }

    public Player player() {
        return player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
