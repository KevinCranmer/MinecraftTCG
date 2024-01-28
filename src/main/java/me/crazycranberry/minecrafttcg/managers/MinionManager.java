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
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

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
    private void onTarget(EntityTargetLivingEntityEvent event) {
        if (StadiumManager.stadium(event.getEntity().getWorld()) != null && !event.getReason().equals(EntityTargetEvent.TargetReason.CUSTOM)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onSpawn(CreatureSpawnEvent event) {
        if (StadiumManager.stadium(event.getEntity().getWorld()) != null && !event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM)) {
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
        Optional<Minion> maybeMinion = stadium.minionFromEntity((LivingEntity) event.getDamager());
        Optional<Minion> maybeTarget = stadium.minionFromEntity((LivingEntity) event.getEntity());
        if (maybeMinion.isPresent() && event.getEntity().getType().equals(PLAYER_PROXY_ENTITY_TYPE)) {
            handleChickenAttacked(stadium, maybeMinion.get(), (LivingEntity) event.getEntity());
            event.setDamage(0);
        } else if (maybeMinion.isPresent() && maybeTarget.isPresent()){
            event.setDamage(0);
            handleMinionAttacked(maybeMinion.get(), maybeTarget.get());
            event.setCancelled(maybeTarget.get().turnsProtected() > 0);
        } else if (maybeTarget.isPresent()) {
            handleMinionAttackedByPlayer((LivingEntity) event.getDamager(), maybeTarget.get(), (int) event.getDamage());
            event.setDamage(0);
            event.setCancelled(maybeTarget.get().turnsProtected() > 0);
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

    private void handleMinionAttackedByPlayer(LivingEntity p, Minion damagee, int damage) {
        damagee.onDamageReceived(p, damage, damagee.turnsProtected() > 0);
    }

    private void handleMinionAttacked(Minion damager, Minion damagee) {
        damagee.onDamageReceived(damager.minionInfo().entity(), damager.strength(), damagee.turnsProtected() > 0);
        damager.onDamageDealt(damagee.minionInfo().entity(), damager.strength(), damagee.turnsProtected() > 0);
    }

    private void handleChickenAttacked(Stadium stadium, Minion damager, LivingEntity chicken) {
        Optional<Player> targetPlayer = stadium.getPlayerFromChicken(chicken);
        if (targetPlayer.isEmpty()) {
            return;
        }
        stadium.pendingDamageForPlayer(targetPlayer.get(), damager.strength());
        damager.onDamageDealt(targetPlayer.get(), damager.strength(), false);
    }
}
