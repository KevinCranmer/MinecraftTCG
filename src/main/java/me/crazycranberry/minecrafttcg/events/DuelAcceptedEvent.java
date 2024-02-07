package me.crazycranberry.minecrafttcg.events;

import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class DuelAcceptedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player requester;
    private final Player accepter;
    private final Boolean isRanked;

    public DuelAcceptedEvent(Player requester, Player accepter) {
        this.requester = requester;
        this.accepter = accepter;
        this.isRanked = false;
    }

    public DuelAcceptedEvent(Player requester, Player accepter, Boolean isRanked) {
        this.requester = requester;
        this.accepter = accepter;
        this.isRanked = isRanked;
    }

    public Player requester() {
        return requester;
    }

    public Player accepter() {
        return accepter;
    }

    public Boolean isRanked() {
        return isRanked;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
