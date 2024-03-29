package me.crazycranberry.minecrafttcg.events;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CastCardEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Card card;
    private final Stadium stadium;
    private final Player caster;
    private final ItemStack cardItem;
    private final List<Spot> targets;

    public CastCardEvent(Card card, Stadium stadium, Player caster, ItemStack cardItem, List<Spot> targets) {
        this.card = card;
        this.stadium = stadium;
        this.caster = caster;
        this.cardItem = cardItem;
        this.targets = targets;
    }

    public Card card() {
        return card;
    }

    public Stadium stadium() {
        return stadium;
    }

    public Player caster() {
        return caster;
    }

    public ItemStack cardItem() {
        return cardItem;
    }

    public List<Spot> targets() {
        return targets;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
