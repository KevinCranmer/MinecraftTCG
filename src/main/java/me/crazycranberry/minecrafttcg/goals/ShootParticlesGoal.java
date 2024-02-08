package me.crazycranberry.minecrafttcg.goals;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class ShootParticlesGoal <T> extends Goal {
    private final Minion shooter;
    private final LivingEntity shooterEntity;
    private final LivingEntity target;
    private final Particle particleType;
    private final int damage;
    private final T data;
    private Location currentParticleStreamLocation;

    public ShootParticlesGoal(Minion shooter, LivingEntity target, Particle particleType, int damage, T data) {
        this.shooter = shooter;
        this.shooterEntity = shooter.minionInfo().entity();
        this.target = target;
        this.particleType = particleType;
        this.damage = damage;
        this.data = data;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canUse() {
        return !shooterEntity.isDead() && shooter.attacksLeft() > 0;
    }

    @Override
    public void tick() {
        if (currentParticleStreamLocation == null) {
            currentParticleStreamLocation = shooterEntity.getEyeLocation().clone();
        }
        double blocksTraveledPerTick = 0.75;
        int particlesPerTick = 3;
        Vector direction = target.getEyeLocation().toVector().subtract(currentParticleStreamLocation.toVector()).normalize().multiply(blocksTraveledPerTick / (double) particlesPerTick);
        for (int i = 0; i < particlesPerTick; i++) {
            shooterEntity.getWorld().spawnParticle(particleType, currentParticleStreamLocation, 5, data);
            if (currentParticleStreamLocation.distanceSquared(target.getEyeLocation()) < 0.5) {
                target.damage(damage, shooterEntity);
                currentParticleStreamLocation = null;
                break;
            }
            currentParticleStreamLocation.add(direction);
        }
    }

    @Override
    public void start() {

    }
}
