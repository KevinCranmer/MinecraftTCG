package me.crazycranberry.minecrafttcg.managers;

import me.crazycranberry.minecrafttcg.model.Spot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Optional;

public class LookAndHighlightManager implements Listener {
    @EventHandler
    private void onPlayerLook(PlayerMoveEvent event) {
        World w = event.getPlayer().getWorld();
        Optional<Spot> closestSpotLookedAt = Arrays.stream(Spot.values())
            .min((s1, s2) -> facingDistance(StadiumManager.locOfSpot(w, s1), event.getPlayer()) - facingDistance(StadiumManager.locOfSpot(w, s2), event.getPlayer()));
        closestSpotLookedAt.ifPresent(s -> StadiumManager.playerLookingAt(event.getPlayer(), s));
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
