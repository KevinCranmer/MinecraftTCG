package me.crazycranberry.minecrafttcg.events;

import me.crazycranberry.minecrafttcg.managers.StadiumManager;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class DuelEndEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Stadium stadium;
    private final Player winner;
    private final Player loser;

    public DuelEndEvent(Player loser) {
        this.loser = loser;
        this.stadium = StadiumManager.stadium(loser.getLocation());
        this.winner = stadium.player1().equals(loser) ? stadium().player2() : stadium().player1();
    }

    public Stadium stadium() {
        return stadium;
    }

    public Player winner() {
        return winner;
    }

    public Player loser() {
        return loser;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
