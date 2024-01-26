package me.crazycranberry.minecrafttcg.goals;

import me.crazycranberry.minecrafttcg.events.CombatEndEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class ShootParticlesGoal <T> extends Goal {
    private final LivingEntity shooter;
    private final LivingEntity target;
    private final Particle particleType;
    private final int damage;
    private final T data;
    private Location currentParticleStreamLocation;

    public ShootParticlesGoal(LivingEntity shooter, LivingEntity target, Particle particleType, int damage, T data) {
        this.shooter = shooter;
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
        return !shooter.isDead();
    }

    @Override
    public void tick() {
        if (currentParticleStreamLocation == null) {
            currentParticleStreamLocation = shooter.getEyeLocation().clone();
        }
        double blocksTraveledPerTick = 0.75;
        int particlesPerTick = 3;
        Vector direction = target.getEyeLocation().toVector().subtract(currentParticleStreamLocation.toVector()).normalize().multiply(blocksTraveledPerTick / (double) particlesPerTick);
        for (int i = 0; i < particlesPerTick; i++) {
            shooter.getWorld().spawnParticle(particleType, currentParticleStreamLocation, 5, data);
            if (currentParticleStreamLocation.distanceSquared(target.getEyeLocation()) < 0.5) {
                target.damage(damage, shooter);
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
