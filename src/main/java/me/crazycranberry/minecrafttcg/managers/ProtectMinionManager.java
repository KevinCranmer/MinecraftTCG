package me.crazycranberry.minecrafttcg.managers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;

public class ProtectMinionManager implements Listener {
    @EventHandler
    private void onBurn(EntityCombustEvent event) {
        event.setCancelled(true);
    }
}
