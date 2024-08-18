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
        return minion.isProtected() || minion.bonusStrength() != 0 || minion.hasOverkill();
    }

    @Override
    public void tick() {
        protectionParticles();
        bonusStrengthParticles();
        overkillParticles();
    }

    private void bonusStrengthParticles() {
        if (minion.bonusStrength() > 0) {
            minion.minionInfo().entity().getWorld().spawnParticle(Particle.GLOW, minion.minionInfo().entity().getEyeLocation(), 2, 0.3, 0.3, 0.3);
            minion.minionInfo().entity().getWorld().spawnParticle(Particle.GLOW, minion.minionInfo().entity().getLocation(), 1, 0.3, 0.3, 0.3);
        } else if (minion.bonusStrength() < 0) {
            minion.minionInfo().entity().getWorld().spawnParticle(Particle.FALLING_OBSIDIAN_TEAR, minion.minionInfo().entity().getEyeLocation(), 2, 0.3, 0.3, 0.3);
            minion.minionInfo().entity().getWorld().spawnParticle(Particle.FALLING_OBSIDIAN_TEAR, minion.minionInfo().entity().getLocation(), 1, 0.3, 0.3, 0.3);
        }
    }

    private void protectionParticles() {
        if (minion.isProtected()) {
            minion.minionInfo().entity().getWorld().spawnParticle(Particle.EFFECT, minion.minionInfo().entity().getEyeLocation(), 2, 0.3, 0.3, 0.3);
            minion.minionInfo().entity().getWorld().spawnParticle(Particle.EFFECT, minion.minionInfo().entity().getLocation(), 1, 0.3, 0.3, 0.3);
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
