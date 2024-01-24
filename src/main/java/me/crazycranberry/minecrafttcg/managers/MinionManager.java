package me.crazycranberry.minecrafttcg.managers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;

public class MinionManager implements Listener {
    @EventHandler
    private void onBurn(EntityCombustEvent event) {
        if (StadiumManager.stadium(event.getEntity().getWorld()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onRandomSpawn(CreatureSpawnEvent event) {
        if (!event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM)) {
            event.setCancelled(true);
        }
    }
}
