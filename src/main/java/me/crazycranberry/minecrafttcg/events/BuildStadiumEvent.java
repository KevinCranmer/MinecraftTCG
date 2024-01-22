package me.crazycranberry.minecrafttcg.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BuildStadiumEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Location startingCorner;
    private final Player player1;
    private final Player player2;

    public BuildStadiumEvent(Location startingCorner, Player player1, Player player2) {
        this.startingCorner = startingCorner;
        this.player1 = player1;
        this.player2 = player2;
    }

    public Location getStartingCorner() {
        return startingCorner;
    }

    public Player player1() {
        return player1;
    }

    public Player player2() {
        return player2;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
