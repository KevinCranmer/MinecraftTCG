package me.crazycranberry.minecrafttcg.goals;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.Particle;

public class ShowProtectionParticlesGoal <T> extends Goal {
    private final Minion minionProtected;

    public ShowProtectionParticlesGoal(Minion minionProtected) {
        this.minionProtected = minionProtected;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canUse() {
        return (minionProtected.turnsProtected() > 0);
    }

    @Override
    public void tick() {
        minionProtected.minionInfo().entity().getWorld().spawnParticle(Particle.GLOW, minionProtected.minionInfo().entity().getEyeLocation(), 2);
        minionProtected.minionInfo().entity().getWorld().spawnParticle(Particle.GLOW, minionProtected.minionInfo().entity().getLocation(), 1);
    }

    @Override
    public void start() {

    }
}
