package me.crazycranberry.minecrafttcg.carddefinitions.minions.unstablepyro;

import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.model.Spot;
import org.bukkit.Bukkit;
import org.bukkit.Particle;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static me.crazycranberry.minecrafttcg.CommonFunctions.randomFromList;
import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;

public class UnstablePyro extends Minion {
    public UnstablePyro(MinionInfo minionInfo) {
        super(CardEnum.UNSTABLE_PYRO.card(), minionInfo);
    }

    @Override
    public void onDeath() {
        super.onDeath();
        fireballEnemyMinion();
    }

    @Override
    public void onEnter() {
        super.onEnter();
        fireballEnemyMinion();
    }

    private void fireballEnemyMinion() {
        List<Spot> enemySpots = minionInfo().stadium().enemyMinionSpots(minionInfo().master());
        Optional<Minion> enemyToFireball = randomFromList(enemySpots.stream()
            .map(s -> this.minionInfo().stadium().minionFromSpot(s))
            .filter(Objects::nonNull)
            .toList());
        if (enemyToFireball.isEmpty()) {
            return;
        }
        minionInfo().entity().getWorld().spawnParticle(Particle.LAVA, enemyToFireball.get().minionInfo().entity().getEyeLocation(), 7, 0.5, 0.75, 0.5);
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> minionInfo().entity().getWorld().spawnParticle(Particle.LAVA, enemyToFireball.get().minionInfo().entity().getEyeLocation(), 7, 0.5, 0.75, 0.5), 10);
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            boolean isProtected = enemyToFireball.get().isProtected();
            enemyToFireball.ifPresent(minion -> minion.onDamageReceived(this.minionInfo().entity(), 1, isProtected));
            this.onDamageDealt(enemyToFireball.get().minionInfo().entity(), 1, false, isProtected);
            minionInfo().entity().getWorld().spawnParticle(Particle.LAVA, enemyToFireball.get().minionInfo().entity().getEyeLocation(), 7, 0.5, 0.75, 0.5);
            }, 20);
    }

    @Override
    public String signDescription() {
        return "";
    }
}
