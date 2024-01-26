package me.crazycranberry.minecrafttcg.events;

import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ParticleStreamCollidedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final LivingEntity shooter;
    private final LivingEntity target;

    public ParticleStreamCollidedEvent(LivingEntity shooter, LivingEntity target) {
        this.shooter = shooter;
        this.target = target;
    }

    public LivingEntity target() {
        return target;
    }

    public LivingEntity shooter() {
        return shooter;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
