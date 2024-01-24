package me.crazycranberry.minecrafttcg.events;

import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SecondPreCombatPhaseStartedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Stadium stadium;

    public SecondPreCombatPhaseStartedEvent(Stadium stadium) {
        this.stadium = stadium;
    }

    public Stadium getStadium() {
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
