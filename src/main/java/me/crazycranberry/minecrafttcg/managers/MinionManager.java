package me.crazycranberry.minecrafttcg.managers;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.goals.LookForwardGoal;
import me.crazycranberry.minecrafttcg.goals.WalkToLocationGoal;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftAnimals;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftChicken;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftLivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Optional;

import static me.crazycranberry.minecrafttcg.managers.StadiumManager.PLAYER_PROXY_ENTITY_TYPE;

public class MinionManager implements Listener {
    @EventHandler
    private void onBurn(EntityCombustEvent event) {
        if (StadiumManager.stadium(event.getEntity().getWorld()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onSpawn(CreatureSpawnEvent event) {
        if (!event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM)) {
            event.setCancelled(true);
            return;
        }
        // A chicken spawned, let's make the chicken stay in its spot
        if (event.getEntity().getType().equals(PLAYER_PROXY_ENTITY_TYPE)) {
            CraftAnimals proxy = (CraftAnimals) event.getEntity();
            proxy.setSilent(true);
            Animal nmsAnimal = proxy.getHandle();
            nmsAnimal.goalSelector.getRunningGoals().forEach(WrappedGoal::stop);
            nmsAnimal.removeAllGoals(g -> true);
            nmsAnimal.goalSelector.addGoal(5, new WalkToLocationGoal(nmsAnimal, proxy.getLocation()));
        }
    }

    @EventHandler
    private void onDamage(EntityDamageByEntityEvent event) {
        Stadium stadium = StadiumManager.stadium(event.getDamager().getWorld());
        if (stadium == null || !(event.getDamager() instanceof LivingEntity)) {
            return;
        }
        event.setDamage(0);
        Optional<Minion> maybeMinion = stadium.minionFromEntity((LivingEntity) event.getDamager());
        if (maybeMinion.isEmpty()) {
            return;
        }
        if (event.getEntity().getType().equals(PLAYER_PROXY_ENTITY_TYPE)) {
            handleChickenAttacked(stadium, maybeMinion.get(), (LivingEntity) event.getEntity());
        } else {
            handleMinionAttacked(maybeMinion.get(), stadium.minionFromEntity((LivingEntity) event.getEntity()));
        }
    }

    @EventHandler
    private void onDeath(EntityDeathEvent event) {
        Stadium stadium = StadiumManager.stadium(event.getEntity().getWorld());
        if (stadium == null) {
            return;
        }
        event.setDroppedExp(0);
        event.getDrops().removeAll(event.getDrops());
    }

    private void handleMinionAttacked(Minion damager, Optional<Minion> damagee) {
        if (damagee.isEmpty()) {
            return;
        }
        damagee.get().onDamageReceived(damager.minionInfo().entity(), damager.strength());
        damager.onDamageDealt(damagee.get().minionInfo().entity(), damager.strength());
    }

    private void handleChickenAttacked(Stadium stadium, Minion damager, LivingEntity chicken) {
        Optional<Player> targetPlayer = stadium.getPlayerFromChicken(chicken);
        if (targetPlayer.isEmpty()) {
            return;
        }
        stadium.pendingDamageForPlayer(targetPlayer.get(), damager.strength());
        damager.onDamageDealt(targetPlayer.get(), damager.strength());
    }
}
