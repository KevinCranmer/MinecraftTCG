package me.crazycranberry.minecrafttcg.carddefinitions.cantrips;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.newAnimationStarted;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.oneAnimationFinished;

public class BloodSacrifice implements CantripCardDefinition {
    private final static Integer numTicks = 30;
    private final static Integer numParticles = 50;
    private final static Integer xOffset = 15;
    private final static Integer zOffset = 12;
    private final static Particle.DustOptions dustOptions = new Particle.DustOptions(Color.RED, 1.5f);

    @Override
    public Integer cost() {
        return 3;
    }

    @Override
    public String cardName() {
        return "Blood Sacrifice";
    }

    @Override
    public String cardDescription() {
        return "Sacrifice target ally minion, deal damage equal to its strength to all enemy minions.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.RARE;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        Minion sacrificialLamb = stadium.minionFromSpot(targets.get(0));
        if (sacrificialLamb == null) {
            return;
        }
        int damage = sacrificialLamb.strength();
        sacrificialLamb.onDeath();
        newAnimationStarted(stadium, caster, 1);
        new BloodTracker(targets.get(0), stadium, caster, damage);
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(true, false, false, false, false);
    }

    @Override
    public Boolean canCastDuringCombat() {
        return true;
    }

    private static class BloodTracker {
        private Integer progressInTicks = 0;
        private Integer taskId;
        private final Location startingLoc;
        private final double endX;
        private final int damage;

        private BloodTracker(Spot startingSpot, Stadium stadium, Player caster, int damage) {
            this.damage = damage;
            this.startingLoc = stadium.locOfSpot(startingSpot).clone().add(0, 1.5, 0);
            this.endX = startingLoc.getX() + xOffset;
            startLoop(stadium, caster);
        }

        public void startLoop(Stadium stadium, Player caster) {
            taskId = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
                if (progressInTicks >= numTicks) {
                    stadium.enemyMinionSpots(caster).stream()
                        .filter(s -> s.name().endsWith("BACK"))
                        .map(stadium::minionFromSpot)
                        .filter(Objects::nonNull)
                        .forEach(m -> m.minionInfo().entity().damage(2, caster));
                    Bukkit.getScheduler().cancelTask(taskId);
                    oneAnimationFinished(stadium, caster);
                    return;
                }
                if (progressInTicks == numTicks / 2) {
                    stadium.enemyMinionSpots(caster).stream()
                        .filter(s -> s.name().endsWith("FRONT"))
                        .map(stadium::minionFromSpot)
                        .filter(Objects::nonNull)
                        .forEach(m -> m.minionInfo().entity().damage(2, caster));
                }
                double progressPercentage = (double) progressInTicks / (double) numTicks;
                spawnParticles(progressPercentage * xOffset, progressPercentage * zOffset, this.startingLoc.clone());
                progressInTicks++;
            }, 0 /*<-- the initial delay */, 1 /*<-- the interval */).getTaskId();
        }

        private static void spawnParticles(double currentXOffset, double zOffset, Location startingLoc) {
            Location middleOfParticleRow = startingLoc.add(currentXOffset, 0, 0);
            startingLoc.getWorld().spawnParticle(Particle.REDSTONE, middleOfParticleRow.getX(), middleOfParticleRow.getY(), middleOfParticleRow.getZ(), numParticles, 0.3, 0.5, zOffset, dustOptions);
        }
    }
}
