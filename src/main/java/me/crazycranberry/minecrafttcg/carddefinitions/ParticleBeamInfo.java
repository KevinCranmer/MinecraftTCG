package me.crazycranberry.minecrafttcg.carddefinitions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public class ParticleBeamInfo {
    private final LivingEntity target;
    private Location particleLoc;
    private boolean alreadyHit;

    public ParticleBeamInfo(Location particleLoc, LivingEntity target) {
        this.particleLoc = particleLoc;
        this.target = target;
        this.alreadyHit = false;
    }

    public Location particleLoc() {
        return particleLoc;
    }

    public void setParticleLoc(Location particleLoc) {
        this.particleLoc = particleLoc;
    }

    public LivingEntity target() {
        return target;
    }

    public boolean alreadyHit() {
        return alreadyHit;
    }

    public void hit() {
        this.alreadyHit = true;
    }
}