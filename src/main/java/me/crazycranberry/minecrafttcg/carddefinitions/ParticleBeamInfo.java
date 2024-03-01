package me.crazycranberry.minecrafttcg.carddefinitions;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import org.bukkit.Location;

public class ParticleBeamInfo {
    private final Minion target;
    private Location particleLoc;
    private boolean alreadyHit;

    public ParticleBeamInfo(Location particleLoc, Minion target) {
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

    public Minion target() {
        return target;
    }

    public boolean alreadyHit() {
        return alreadyHit;
    }

    public void hit() {
        this.alreadyHit = true;
    }
}