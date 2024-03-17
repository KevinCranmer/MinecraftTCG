package me.crazycranberry.minecrafttcg.carddefinitions;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.apache.commons.lang3.function.TriConsumer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import static me.crazycranberry.minecrafttcg.CommonFunctions.randomFromList;
import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;

public class ParticleBeamTracker {
    private Integer taskId;
    private final Stadium stadium;
    private final List<ParticleBeamInfo> minionsToBeam;
    private final List<Particle.DustOptions> dustOptions;
    private final Particle particle;
    private final TriConsumer<Stadium, Player, ParticleBeamInfo> doThisWhenBeamHits;
    private final double blocksTraveledPerTick;
    private final int particlesPerTick;

    public ParticleBeamTracker(Stadium stadium, Player caster, List<LivingEntity> targetsToBeam, Particle particle, List<Particle.DustOptions> dustOptions, double blocksTraveledPerTick, int particlesPerTick, TriConsumer<Stadium, Player, ParticleBeamInfo> doThisWhenBeamHits) {
        this.stadium = stadium;
        this.particle = particle;
        this.dustOptions = dustOptions == null ? List.of() : dustOptions;
        this.blocksTraveledPerTick = blocksTraveledPerTick;
        this.particlesPerTick = particlesPerTick;
        this.doThisWhenBeamHits = doThisWhenBeamHits;
        this.minionsToBeam = new ArrayList<>();
        for (LivingEntity target : targetsToBeam) {
            if (target != null && !target.isDead()) {
                this.minionsToBeam.add(new ParticleBeamInfo(caster.getLocation().clone(), target));
            }
        }
        startLoop(caster);
    }

    public ParticleBeamTracker(Stadium stadium, Player caster, List<LivingEntity> targetsToBeam, Particle particle, List<Particle.DustOptions> dustOptions, double blocksTraveledPerTick, int particlesPerTick, TriConsumer<Stadium, Player, ParticleBeamInfo> doThisWhenBeamHits, Location beamStartingLocation) {
        this.stadium = stadium;
        this.particle = particle;
        this.dustOptions = dustOptions == null ? List.of() : dustOptions;
        this.blocksTraveledPerTick = blocksTraveledPerTick;
        this.particlesPerTick = particlesPerTick;
        this.doThisWhenBeamHits = doThisWhenBeamHits;
        this.minionsToBeam = new ArrayList<>();
        for (LivingEntity target : targetsToBeam) {
            if (target != null && !target.isDead()) {
                this.minionsToBeam.add(new ParticleBeamInfo(beamStartingLocation.clone(), target));
            }
        }
        startLoop(caster);
    }

    public void startLoop(Player caster) {
        taskId = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
            for (ParticleBeamInfo beam : minionsToBeam) {
                Vector direction = beam.target().getEyeLocation().toVector().subtract(beam.particleLoc().toVector()).normalize().multiply(blocksTraveledPerTick / (double) particlesPerTick);
                for (int i = 0; i < particlesPerTick; i++) {
                    if (beam.alreadyHit()) {
                        continue;
                    }
                    spawnParticles(beam.particleLoc());
                    if (beam.particleLoc().distanceSquared(beam.target().getEyeLocation()) < 0.5) {
                        doThisWhenBeamHits.accept(stadium, caster, beam);
                        beam.hit();
                        break;
                    }
                    beam.particleLoc().add(direction);
                }
            }
            if (minionsToBeam.stream().allMatch(ParticleBeamInfo::alreadyHit)) {
                Bukkit.getScheduler().cancelTask(taskId);
            }
        }, 0 /*<-- the initial delay */, 1 /*<-- the interval */).getTaskId();
    }

    private void spawnParticles(Location loc) {
        Optional<Particle.DustOptions> maybeDustOptions = randomFromList(dustOptions);
        if (maybeDustOptions.isPresent()) {
            loc.getWorld().spawnParticle(particle, loc, particlesPerTick, maybeDustOptions.get());
        } else {
            loc.getWorld().spawnParticle(particle, loc, particlesPerTick);
        }
    }
}
