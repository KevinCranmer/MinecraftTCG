package me.crazycranberry.minecrafttcg.events;

import me.crazycranberry.minecrafttcg.managers.StadiumManager;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DuelEndEvent extends Event {
    private static List<Stadium> stadiumsEnding = new ArrayList<>();
    private static final HandlerList HANDLERS = new HandlerList();

    private final Stadium stadium;
    private final Player winner;
    private final Player loser;
    private final Boolean isTie;

    public DuelEndEvent(Player loser, boolean isTie) {
        this.loser = loser;
        this.stadium = StadiumManager.stadium(loser.getLocation());
        this.winner = stadium.player1().equals(loser) ? stadium().player2() : stadium().player1();
        this.isTie = isTie;
        stadiumsEnding.add(stadium);
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

    public Boolean isTie() {
        return isTie;
    }

    public static Boolean isEndable(Stadium stadium) {
        return !stadiumsEnding.contains(stadium);
    }

    public static void duelClosed(Stadium stadium) {
        stadiumsEnding.remove(stadium);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
