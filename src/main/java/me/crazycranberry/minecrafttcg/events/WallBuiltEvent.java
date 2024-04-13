package me.crazycranberry.minecrafttcg.events;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.model.Column;
import me.crazycranberry.minecrafttcg.model.Stadium;
import me.crazycranberry.minecrafttcg.model.Wall;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class WallBuiltEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Wall wall;

    public WallBuiltEvent(Wall wall) {
        this.wall = wall;
    }

    public Wall wall() {
        return wall;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
