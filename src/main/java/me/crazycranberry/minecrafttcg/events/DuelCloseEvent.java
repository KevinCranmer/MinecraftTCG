package me.crazycranberry.minecrafttcg.events;

import me.crazycranberry.minecrafttcg.managers.StadiumManager;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class DuelCloseEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Stadium stadium;

    public DuelCloseEvent(Stadium stadium) {
        this.stadium = stadium;
        DuelEndEvent.duelClosed(stadium);
    }

    public Stadium stadium() {
        return stadium;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
