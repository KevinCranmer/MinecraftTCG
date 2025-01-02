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
import java.util.Map;
import java.util.Objects;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.newAnimationStarted;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.oneAnimationFinished;
import static me.crazycranberry.minecrafttcg.model.Spot.BLUE_1_BACK;
import static me.crazycranberry.minecrafttcg.model.Spot.BLUE_2_BACK;
import static me.crazycranberry.minecrafttcg.model.Spot.LENGTH_OF_BATTLEFIELD;
import static me.crazycranberry.minecrafttcg.model.Spot.MIDDLE_X;

public class BloodWave implements SpellCardDefinition {

    @Override
    public Integer cost() {
        return 2;
    }

    @Override
    public String cardName() {
        return "Blood Wave";
    }

    @Override
    public String cardDescription() {
        return "Deal 1 damage to all minions. Then repeat this process if any minion died.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.RARE;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        newAnimationStarted(stadium, caster, 1);
        new BloodWaveTracker(stadium, caster);
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(false, false, false, false, false);
    }


    private static class BloodWaveTracker {
        private static final Map<Spot, Spot> oppositeStartingSpot = Map.of(BLUE_1_BACK, BLUE_2_BACK, BLUE_2_BACK, BLUE_1_BACK);
        private final static Particle.DustOptions dustOptions = new Particle.DustOptions(Color.RED, 1);
        private static final double blocksPerTick = 0.4;
        private static final int bloodQuantity = 100;
        private static final int damage = 1;
        private final Stadium stadium;
        private final Player caster;
        private int direction;
        private Spot startingSpot;
        private int taskId;
        private Boolean minionDied;
        private final int xOffset = 2;

        public BloodWaveTracker(Stadium stadium, Player caster) {
            this.stadium = stadium;
            this.caster = caster;
            this.minionDied = null;
            direction = 1;
            startingSpot = BLUE_1_BACK;
            if (!stadium.player1().equals(caster)) {
                direction = -1;
                startingSpot = oppositeStartingSpot.get(BLUE_1_BACK);
            }
            summonWave();
        }

        private void summonWave() {
            Location currentLoc = stadium.locOfSpot(startingSpot).add(-xOffset * direction, 0, 0);
            taskId = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
                drawBloodLine(currentLoc);
                if ((currentLoc.getX() * direction) > (stadium.locOfSpot(oppositeStartingSpot.get(startingSpot)).getX() * direction) + xOffset) {
                    // Have we made it to the other side
                    if (minionDied) {
                        direction = direction * -1;
                        startingSpot = oppositeStartingSpot.get(startingSpot);
                        minionDied = null;
                    } else {
                        Bukkit.getScheduler().cancelTask(taskId);
                        oneAnimationFinished(stadium, caster);
                        return;
                    }
                } else if (minionDied == null && (currentLoc.getX() * direction) > (MIDDLE_X * direction)) {
                    // Have we made it half way
                    minionDied = damageAllMinions();
                }
                currentLoc.add(direction * blocksPerTick, 0, 0);
            }, 0 /*<-- the initial delay */, 1 /*<-- the interval */).getTaskId();
        }

        private boolean damageAllMinions() {
            boolean minionDied = stadium.allMinions().stream()
                .filter(Objects::nonNull)
                .anyMatch(m -> m.health() <= damage);
            stadium.allMinions().stream()
                .filter(Objects::nonNull)
                .forEach(m -> m.onDamageReceived(caster, damage, m.isProtected()));
            return minionDied;
        }

        private void drawBloodLine(Location startingLoc) {
            startingLoc.getWorld().spawnParticle(Particle.DUST, startingLoc, bloodQuantity, 0.1, 0.6, LENGTH_OF_BATTLEFIELD / 1.9, dustOptions);
        }
    }
}
