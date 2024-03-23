package me.crazycranberry.minecrafttcg.events;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MinionEnteredEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Stadium stadium;
    private final Player master;
    private final Minion minion;

    public MinionEnteredEvent(Minion minion) {
        this.stadium = minion.minionInfo().stadium();
        this.master = minion.minionInfo().master();
        this.minion = minion;
    }

    public Stadium stadium() {
        return stadium;
    }

    public Player master() {
        return master;
    }

    public Minion minion() {
        return minion;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
