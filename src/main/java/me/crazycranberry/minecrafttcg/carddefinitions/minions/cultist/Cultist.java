package me.crazycranberry.minecrafttcg.carddefinitions.minions.cultist;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.baby.Baby;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.baby.BabyDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.summoning.Summoning;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.summoning.SummoningDef;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Map;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.newAnimationStarted;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.oneAnimationFinished;
import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.CULTIST;
import static me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition.summonMinion;
import static me.crazycranberry.minecrafttcg.carddefinitions.minions.cultist.CultistDef.CULTIST_SUMMONING_HEALTH;
import static me.crazycranberry.minecrafttcg.carddefinitions.minions.cultist.CultistDef.CULTIST_SUMMONING_STRENGTH;

public class Cultist extends Minion {
    private static final Map<Spot, Spot> spotMappingCultist = Map.of(
        Spot.RED_1_BACK, Spot.GREEN_1_BACK,
        Spot.RED_1_FRONT, Spot.GREEN_1_FRONT,
        Spot.RED_2_BACK, Spot.GREEN_2_BACK,
        Spot.RED_2_FRONT, Spot.GREEN_2_FRONT
    );

    private static final Map<Spot, Spot> spotMappingSummoning = Map.of(
        Spot.RED_1_BACK, Spot.BLUE_1_BACK,
        Spot.RED_1_FRONT, Spot.BLUE_1_FRONT,
        Spot.RED_2_BACK, Spot.BLUE_2_BACK,
        Spot.RED_2_FRONT, Spot.BLUE_2_FRONT
    );

    public Cultist(MinionInfo minionInfo) {
        super(CULTIST.card(), minionInfo);
    }

    @Override
    public void onCombatEnd() {
        super.onCombatEnd();
        // Let only the red cultist do the summoning otherwise it would double up
        Spot greenCultistSpot = spotMappingCultist.get(this.minionInfo().spot());
        if (greenCultistSpot == null) {
            return;
        }
        Spot summoningSpot = spotMappingSummoning.get(this.minionInfo().spot());
        var greenCultist = this.minionInfo().stadium().minionFromSpot(greenCultistSpot);
        var summoning = this.minionInfo().stadium().minionFromSpot(summoningSpot);
        if (greenCultist != null && summoning == null && this.health() > 0 && greenCultist.health() > 0) {
            performRitual(this, greenCultist, summoningSpot);
        }
    }

    @Override
    public String signDescription() {
        return String.format("Two of 'em will\nsummon a %s/%s", CULTIST_SUMMONING_STRENGTH, CULTIST_SUMMONING_HEALTH);
    }

    private void performRitual(Minion redCultist, Minion greenCultist, Spot summoningSpot) {
        newAnimationStarted(this, 1);
        new RitualTracker(this.minionInfo().stadium(), this.minionInfo().master(), summoningSpot, redCultist, greenCultist);
    }



    private static class RitualTracker {
        private static final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.PURPLE, 1);
        private static final int auraParticlesPerTick = 10;
        private final Stadium stadium;
        private final Player caster;
        private final Spot summoningSpot;
        private final LivingEntity redCultist;
        private final LivingEntity greenCultist;
        private RitualPhase phase;
        private int ticksLeftInPhase;
        private int taskId;

        public RitualTracker(Stadium stadium, Player caster, Spot summoningSpot, Minion redCultist, Minion greenCultist) {
            this.stadium = stadium;
            this.caster = caster;
            this.summoningSpot = summoningSpot;
            this.redCultist = redCultist.minionInfo().entity();
            this.greenCultist = greenCultist.minionInfo().entity();
            this.phase = RitualPhase.AURA;
            this.ticksLeftInPhase = phase.getTimeInPhase();
            startRitual();
        }

        private void startRitual() {
            taskId = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
                switch (this.phase) {
                    case AURA:
                        createAuraParticles();
                        break;
                    case BEAM:
                        createAuraParticles();
                        createBeamParticles();
                        break;
                    case SUMMONING:
                        createSummoningParticles();
                        break;
                }
                this.ticksLeftInPhase--;
                if (this.ticksLeftInPhase <= 0) {
                    progressPhase();
                }
            }, 0 /*<-- the initial delay */, 1 /*<-- the interval */).getTaskId();
        }

        private void createBeamParticles() {
            var redPath = stadium.locOfSpot(summoningSpot).subtract(redCultist.getEyeLocation()).toVector();
            var greenPath = stadium.locOfSpot(summoningSpot).subtract(greenCultist.getEyeLocation()).toVector();
            var timeInPhase = RitualPhase.BEAM.getTimeInPhase();
            var ticksInCurrentPhase = timeInPhase - ticksLeftInPhase;
            redPath.divide(new Vector(timeInPhase, timeInPhase, timeInPhase));
            greenPath.divide(new Vector(timeInPhase, timeInPhase, timeInPhase));
            redPath.multiply(new Vector(ticksInCurrentPhase, ticksInCurrentPhase, ticksInCurrentPhase));
            greenPath.multiply(new Vector(ticksInCurrentPhase, ticksInCurrentPhase, ticksInCurrentPhase));
            redCultist.getWorld().spawnParticle(Particle.DUST, redCultist.getEyeLocation().add(redPath), 1, dustOptions);
            greenCultist.getWorld().spawnParticle(Particle.DUST, greenCultist.getEyeLocation().add(greenPath), 1, dustOptions);
        }

        private void createAuraParticles() {
            redCultist.getWorld().spawnParticle(Particle.PORTAL, redCultist.getEyeLocation(), auraParticlesPerTick, 0.2, 0.75, 0.2);
            greenCultist.getWorld().spawnParticle(Particle.PORTAL, greenCultist.getEyeLocation(), auraParticlesPerTick, 0.2, 0.75, 0.2);
        }

        private void createSummoningParticles() {
            redCultist.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, stadium.locOfSpot(summoningSpot), auraParticlesPerTick, 0.2, 0.3, 0.2);
        }

        private void progressPhase() {
            switch (this.phase) {
                case SUMMONING:
                    Bukkit.getScheduler().cancelTask(taskId);
                    summonTheSummoning();
                    oneAnimationFinished(stadium, caster);
                    return;
                case BEAM:
                    this.phase = RitualPhase.SUMMONING;
                    redCultist.getWorld().playSound(stadium.locOfSpot(summoningSpot), Sound.ENTITY_WARDEN_DIG, 0.75f, 1f);
                    break;
                case AURA:
                    this.phase = RitualPhase.BEAM;
                    break;
            }
            this.ticksLeftInPhase = this.phase.getTimeInPhase();
        }

        private void summonTheSummoning() {
            SummoningDef def = new SummoningDef();
            summonMinion(summoningSpot, stadium, caster, Summoning.class, def);
        }

        private enum RitualPhase {
            AURA(75),
            BEAM(50),
            SUMMONING(25);

            private final int timeInPhase;

            RitualPhase(int timeInPhase) {
                this.timeInPhase = timeInPhase;
            }

            int getTimeInPhase() {
                return timeInPhase;
            }
        }
    }
}
