package me.crazycranberry.minecrafttcg.managers;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.goals.WalkToLocationGoal;
import me.crazycranberry.minecrafttcg.model.Stadium;
import me.crazycranberry.minecrafttcg.model.TurnPhase;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.animal.Animal;
import org.bukkit.block.data.Levelled;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftAnimals;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import java.util.List;
import java.util.Optional;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardUtils.handleOverkillDamage;
import static me.crazycranberry.minecrafttcg.managers.StadiumManager.PLAYER_PROXY_ENTITY_TYPE;

public class MinionManager implements Listener {
    private static final List<EntityDamageEvent.DamageCause> damageTypesToIgnore = List.of(
        EntityDamageEvent.DamageCause.DROWNING,
        EntityDamageEvent.DamageCause.LIGHTNING
    );

    @EventHandler
    private void onBurn(EntityCombustEvent event) {
        if (StadiumManager.stadium(event.getEntity().getLocation()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onTarget(EntityTargetLivingEntityEvent event) {
        if (StadiumManager.stadium(event.getEntity().getLocation()) != null && !event.getReason().equals(EntityTargetEvent.TargetReason.CUSTOM)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWaterFlow(BlockFromToEvent event) {
        if (StadiumManager.stadium(event.getBlock().getLocation()) != null && event.getBlock().getBlockData() instanceof Levelled) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onSpawn(CreatureSpawnEvent event) {
        if (StadiumManager.stadium(event.getEntity().getLocation()) != null && !event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM)) {
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
        if (damageTypesToIgnore.contains(event.getCause())) {
            event.setCancelled(true);
            return;
        }
        Stadium stadium = StadiumManager.stadium(event.getDamager().getLocation());
        if (stadium == null || !(event.getDamager() instanceof LivingEntity)) {
            return;
        }
        Optional<Minion> maybeMinion = stadium.minionFromEntity((LivingEntity) event.getDamager());
        Optional<Minion> maybeTarget = stadium.minionFromEntity((LivingEntity) event.getEntity());
        if (event.getEntity().getType().equals(PLAYER_PROXY_ENTITY_TYPE)) {
            maybeMinion.ifPresent(minion -> handleChickenAttacked(stadium, minion, (LivingEntity) event.getEntity()));
            event.setCancelled(true);
        } else if (maybeMinion.isPresent() && maybeTarget.isPresent()){
            handleMinionAttacked(maybeMinion.get(), maybeTarget.get());
            if (!maybeTarget.get().isProtected()) {
                ((LivingEntity) event.getEntity()).damage(0);
            }
            event.setCancelled(true);
        } else if (maybeTarget.isPresent()) {
            handleMinionAttackedByPlayer((LivingEntity) event.getDamager(), maybeTarget.get(), (int) event.getDamage());
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onDeath(EntityDeathEvent event) {
        Stadium stadium = StadiumManager.stadium(event.getEntity().getLocation());
        if (stadium == null) {
            return;
        }
        event.setDroppedExp(0);
        event.getDrops().removeAll(event.getDrops());
    }

    private void handleMinionAttackedByPlayer(LivingEntity p, Minion damagee, int damage) {
        damagee.onDamageReceived(p, damage, damagee.isProtected());
    }

    private void handleMinionAttacked(Minion damager, Minion damagee) {
        if (damager.hasOverkill() && !damagee.isProtected()) {
            handleOverkillDamage(damagee, damager.strength(), damager.minionInfo().entity(), true);
        } else {
            damagee.onDamageReceived(damager.minionInfo().entity(), damager.strength(), damagee.isProtected());
            damager.onDamageDealt(damagee.minionInfo().entity(), damager.strength(), true, damagee.isProtected());
        }
    }

    private void handleChickenAttacked(Stadium stadium, Minion damager, LivingEntity chicken) {
        Optional<Player> targetPlayer = stadium.getPlayerFromChicken(chicken);
        if (targetPlayer.isEmpty()) {
            return;
        }
        if (stadium.phase().equals(TurnPhase.COMBAT_PHASE)) {
            stadium.pendingDamageForPlayer(targetPlayer.get(), damager.strength());
        }
        damager.onDamageDealt(targetPlayer.get(), damager.strength(), true, false);
    }
}
