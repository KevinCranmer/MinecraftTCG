package me.crazycranberry.minecrafttcg.carddefinitions.spells;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.model.Column;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.newAnimationStarted;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.oneAnimationFinished;

public class VerticalFire implements SpellCardDefinition {
    private final static Integer numTicks = 30;
    private final static Integer numParticleIncreasePerTick = 7;

    @Override
    public Integer cost() {
        return 5;
    }

    @Override
    public String cardName() {
        return "Vertical Fire";
    }

    @Override
    public String cardDescription() {
        return "Destroy all minions in a targeted column.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.RARE;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        newAnimationStarted(stadium, caster, 1);
        new FireTracker(targets.get(0).column(), stadium, caster);
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(true, true, false, true, true);
    }

    private static class FireTracker {
        private Integer progressInTicks = 0;
        private Integer taskId;
        private Column column;
        private double xRange;
        private int numParticles;
        private Location middleOfColumn;

        private FireTracker(Column column, Stadium stadium, Player caster) {
            this.column = column;
            double maxX = Arrays.stream(Spot.values())
                .filter(s -> column.equals(s.column()))
                .map(Spot::offset)
                .map(Vector::getX)
                .max(Double::compare)
                .orElse(20.0);
            this.xRange = maxX - Spot.MIDDLE_X;
            double zOffset = Arrays.stream(Spot.values())
                .filter(s -> column.equals(s.column()))
                .map(Spot::offset)
                .map(Vector::getZ)
                .findFirst()
                .orElse(1.0) + 1;
            this.middleOfColumn = stadium.startingCorner().clone().add(Spot.MIDDLE_X, 0.5, zOffset);
            startLoop(stadium, caster);
        }

        public void startLoop(Stadium stadium, Player caster) {
            taskId = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
                if (progressInTicks >= numTicks) {
                    Bukkit.getScheduler().cancelTask(taskId);
                    oneAnimationFinished(stadium, caster);
                    return;
                }
                if (progressInTicks == numTicks / 2) {
                    Arrays.stream(Spot.values())
                        .filter(s -> column.equals(s.column()))
                        .map(stadium::minionFromSpot)
                        .filter(Objects::nonNull)
                        .forEach(Minion::onDeath);
                    numParticles -= numParticleIncreasePerTick;
                } else if (progressInTicks > numTicks / 2) {
                    numParticles -= numParticleIncreasePerTick;
                } else {
                    numParticles += numParticleIncreasePerTick;
                }
                middleOfColumn.getWorld().spawnParticle(Particle.LAVA, middleOfColumn.getX(), middleOfColumn.getY(), middleOfColumn.getZ(), numParticles, xRange, 0.75, 1, null);
                progressInTicks++;
            }, 0 /*<-- the initial delay */, 1 /*<-- the interval */).getTaskId();
        }
    }
}
