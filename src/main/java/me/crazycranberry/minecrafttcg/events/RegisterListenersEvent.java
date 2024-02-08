package me.crazycranberry.minecrafttcg.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RegisterListenersEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Boolean shouldRegister;
    public RegisterListenersEvent(boolean shouldRegister) {
        this.shouldRegister = shouldRegister;
    }

    public Boolean shouldRegister() {
        return shouldRegister;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
