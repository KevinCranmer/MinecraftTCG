package me.crazycranberry.minecrafttcg.events;

import me.crazycranberry.minecrafttcg.model.Collection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CardsViewRequestEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Collection.SortBy sortBy;

    public CardsViewRequestEvent(Player player, Collection.SortBy sortBy) {
        this.player = player;
        this.sortBy = sortBy;
    }

    public Player getPlayer() {
        return player;
    }

    public Collection.SortBy sortBy() {
        return sortBy;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
