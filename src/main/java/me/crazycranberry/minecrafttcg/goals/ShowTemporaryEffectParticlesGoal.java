package me.crazycranberry.minecrafttcg.goals;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.Particle;

public class ShowTemporaryEffectParticlesGoal<T> extends Goal {
    private final Minion minion;

    public ShowTemporaryEffectParticlesGoal(Minion minion) {
        this.minion = minion;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canUse() {
        return minion.turnsProtected() > 0 || minion.hasBonusStrength();
    }

    @Override
    public void tick() {
        protectionParticles();
        bonusStrengthParticles();
    }

    private void bonusStrengthParticles() {
        if (minion.hasBonusStrength()) {
            minion.minionInfo().entity().getWorld().spawnParticle(Particle.GLOW, minion.minionInfo().entity().getEyeLocation(), 2, 0.3, 0.3, 0.3);
            minion.minionInfo().entity().getWorld().spawnParticle(Particle.GLOW, minion.minionInfo().entity().getLocation(), 1, 0.3, 0.3, 0.3);
        }
    }

    private void protectionParticles() {
        if (minion.turnsProtected() > 0) {
            minion.minionInfo().entity().getWorld().spawnParticle(Particle.SPELL, minion.minionInfo().entity().getEyeLocation(), 2, 0.3, 0.3, 0.3);
            minion.minionInfo().entity().getWorld().spawnParticle(Particle.SPELL, minion.minionInfo().entity().getLocation(), 1, 0.3, 0.3, 0.3);
        }
    }

    @Override
    public void start() {

    }
}
