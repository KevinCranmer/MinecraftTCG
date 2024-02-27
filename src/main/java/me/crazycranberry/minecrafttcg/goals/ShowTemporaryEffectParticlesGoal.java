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
        return minion.isProtected() || minion.hasBonusStrength() || minion.hasOverkill();
    }

    @Override
    public void tick() {
        protectionParticles();
        bonusStrengthParticles();
        overkillParticles();
    }

    private void bonusStrengthParticles() {
        if (minion.hasBonusStrength()) {
            minion.minionInfo().entity().getWorld().spawnParticle(Particle.GLOW, minion.minionInfo().entity().getEyeLocation(), 2, 0.3, 0.3, 0.3);
            minion.minionInfo().entity().getWorld().spawnParticle(Particle.GLOW, minion.minionInfo().entity().getLocation(), 1, 0.3, 0.3, 0.3);
        }
    }

    private void protectionParticles() {
        if (minion.isProtected()) {
            minion.minionInfo().entity().getWorld().spawnParticle(Particle.SPELL, minion.minionInfo().entity().getEyeLocation(), 2, 0.3, 0.3, 0.3);
            minion.minionInfo().entity().getWorld().spawnParticle(Particle.SPELL, minion.minionInfo().entity().getLocation(), 1, 0.3, 0.3, 0.3);
        }
    }

    private void overkillParticles() {
        if (minion.hasOverkill()) {
            minion.minionInfo().entity().getWorld().spawnParticle(Particle.CRIT, minion.minionInfo().entity().getEyeLocation(), 2, 0.3, 0.3, 0.3);
            minion.minionInfo().entity().getWorld().spawnParticle(Particle.CRIT, minion.minionInfo().entity().getLocation(), 1, 0.3, 0.3, 0.3);
        }
    }

    @Override
    public void start() {

    }
}
