package me.crazycranberry.minecrafttcg.managers;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.goals.LookForwardGoal;
import me.crazycranberry.minecrafttcg.goals.WalkToLocationGoal;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.animal.Chicken;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftChicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Optional;

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
        if (event.getEntity().getType().equals(EntityType.CHICKEN)) {
            System.out.println("A chicken did spawn!");
            CraftChicken chicken = (CraftChicken) event.getEntity();
            Chicken nmsChicken = chicken.getHandle();
            nmsChicken.goalSelector.getRunningGoals().forEach(WrappedGoal::stop);
            nmsChicken.removeAllGoals(g -> true);
            nmsChicken.goalSelector.addGoal(5, new WalkToLocationGoal(nmsChicken, chicken.getLocation()));
        }
    }

    @EventHandler
    private void onDamage(EntityDamageByEntityEvent event) {
        // Calculate for damager
        Stadium stadium = StadiumManager.stadium(event.getDamager().getWorld());
        Optional<Minion> maybeMinion = stadium.minionFromEntity((LivingEntity) event.getDamager());
        if (maybeMinion.isEmpty()) {
            System.out.println("a Non-minion attacked. Whatever, I guess.");
            return;
        }
        Minion minion = maybeMinion.get();
        minion.onDamageDealt((LivingEntity) event.getEntity(), minion.strength());
    }
}
