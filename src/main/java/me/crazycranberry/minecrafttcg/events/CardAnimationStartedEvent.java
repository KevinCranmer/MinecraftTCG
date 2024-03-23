package me.crazycranberry.minecrafttcg.events;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CardAnimationStartedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Stadium stadium;
    private final Player caster;

    public CardAnimationStartedEvent(Stadium stadium, Player caster) {
        this.stadium = stadium;
        this.caster = caster;
    }

    public Stadium stadium() {
        return stadium;
    }

    public Player caster() {
        return caster;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
