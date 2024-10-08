package me.crazycranberry.minecrafttcg.carddefinitions.spells;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
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

public class ToxicSpikes implements SpellCardDefinition {
    private final static Integer numTicks = 20;
    private final static Double maxHeight = 1.75;
    private final static Particle.DustOptions dustOptions = new Particle.DustOptions(Color.BLACK, 1);

    @Override
    public Integer cost() {
        return 4;
    }

    @Override
    public String cardName() {
        return "Toxic Spikes";
    }

    @Override
    public String cardDescription() {
        return "Deals 2 damage to all minions in a targeted row";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.COMMON;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        newAnimationStarted(stadium, caster, 1);
        new ToxicTracker(stadium.targetedRow(caster), stadium, caster);
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(true, true, false, true, true);
    }

    private static class ToxicTracker {
        private Integer progressInTicks = 0;
        private Integer taskId;
        private final List<Spot> spots;

        private ToxicTracker(List<Spot> spots, Stadium stadium, Player caster) {
            this.spots = spots;
            startLoop(stadium, caster);
        }

        public void startLoop(Stadium stadium, Player caster) {
            taskId = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
                if (progressInTicks >= numTicks) {
                    Bukkit.getScheduler().cancelTask(taskId);
                    oneAnimationFinished(stadium, caster);
                    return;
                } else if (progressInTicks == (numTicks / 2)) {
                    spots.stream()
                            .map(stadium::minionFromSpot)
                            .filter(Objects::nonNull)
                            .forEach(m -> m.minionInfo().entity().damage(2, caster));
                }
                spawnParticles(stadium, spots, progressInTicks);
                progressInTicks++;
            }, 0 /*<-- the initial delay */, 1 /*<-- the interval */).getTaskId();
        }

        private static void spawnParticles(Stadium stadium, List<Spot> spots, Integer progressInTicks) {
            for (Spot spot : spots) {
                Location spotLoc = stadium.locOfSpot(spot);
                int yStep = progressInTicks < (numTicks/2) ? progressInTicks : numTicks - progressInTicks;
                double yOffset = ((double)yStep / (double)(numTicks/2)) * maxHeight;
                spotLoc.getWorld().spawnParticle(Particle.DUST, spotLoc.clone().add(0, yOffset, -1), 2, dustOptions);
                spotLoc.getWorld().spawnParticle(Particle.DUST, spotLoc.clone().add(0, yOffset, -0.5), 2, dustOptions);
                spotLoc.getWorld().spawnParticle(Particle.DUST, spotLoc.clone().add(0, yOffset, 0), 2, dustOptions);
                spotLoc.getWorld().spawnParticle(Particle.DUST, spotLoc.clone().add(0, yOffset, 0.5), 2, dustOptions);
                spotLoc.getWorld().spawnParticle(Particle.DUST, spotLoc.clone().add(0, yOffset, 1), 2, dustOptions);
            }
        }
    }
}
