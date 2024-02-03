package me.crazycranberry.minecrafttcg.managers;

import me.crazycranberry.minecrafttcg.events.DuelEndEvent;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Optional;

public class PlayerManager implements Listener {
    @EventHandler
    private void onPlayerLook(PlayerMoveEvent event) {
        if (StadiumManager.stadium(event.getPlayer().getLocation()) == null) {
            return;
        }
        Location loc = event.getPlayer().getLocation();
        Optional<Spot> closestSpotLookedAt = Arrays.stream(Spot.values()).filter(Spot::isTargetable)
            .min((s1, s2) -> facingDistance(StadiumManager.locOfSpot(loc, s1), event.getPlayer()) - facingDistance(StadiumManager.locOfSpot(loc, s2), event.getPlayer()));
        closestSpotLookedAt.ifPresent(s -> StadiumManager.playerLookingAt(event.getPlayer(), s));
    }

    @EventHandler
    private void onPlayerTakeDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && StadiumManager.stadium(event.getEntity().getLocation()) != null && !event.getCause().equals(EntityDamageEvent.DamageCause.CUSTOM)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onHungerDeplete(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player && StadiumManager.stadium(event.getEntity().getLocation()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {
        Stadium stadium = StadiumManager.stadium(event.getEntity().getLocation());
        if (stadium != null && DuelEndEvent.isEndable(stadium)) {
            event.setDeathMessage(String.format("%s has died in a duel against %s.", event.getEntity().getName(), event.getEntity().equals(stadium.player1()) ? stadium.player2().getName() : stadium.player1().getName()));
            Bukkit.getPluginManager().callEvent(new DuelEndEvent(event.getEntity(), false));
        }
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent event) {
        Stadium stadium = StadiumManager.stadium(event.getPlayer().getLocation());
        if (stadium != null && DuelEndEvent.isEndable(stadium)) {
            Bukkit.getPluginManager().callEvent(new DuelEndEvent(event.getPlayer(), false));
        }
    }

    private int facingDistance(Location target, Player p) {
        Location targetLocation = target.clone();
        Location playerLocation = p.getLocation().clone().add(0, 1, 0); //To go from their eyes and not their legs
        Vector locationDifferenceNomalized = targetLocation.toVector().subtract(playerLocation.toVector()).normalize();
        Vector playerDirection = playerLocation.getDirection();
        Vector difference = locationDifferenceNomalized.subtract(playerDirection);
        return (int) (difference.lengthSquared() * 100);
    }
}
