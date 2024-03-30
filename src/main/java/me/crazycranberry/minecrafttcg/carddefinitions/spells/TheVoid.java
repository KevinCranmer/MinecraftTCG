package me.crazycranberry.minecrafttcg.carddefinitions.spells;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.newAnimationStarted;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.oneAnimationFinished;
import static org.bukkit.Sound.ENTITY_WARDEN_SONIC_CHARGE;

public class TheVoid implements SpellCardDefinition {
    private final static Integer numTicksToWaitForParticles = 10; // Because the sound is slowish
    private final static Integer numTicks = 20;
    private final static Integer numParticlesPerTick = 40;
    private final static Integer numParticlesPerSpawnParticle = 3;
    @Override
    public Integer cost() {
        return 8;
    }

    @Override
    public String cardName() {
        return "The Void";
    }

    @Override
    public String cardDescription() {
        return "Destroy all minions.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.RARE;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        newAnimationStarted(stadium, caster, 1);
        new TheVoidTracker(stadium, caster);
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(false, false, false, false, false);
    }

    private static class TheVoidTracker {
        private Integer progressInTicks = 0;
        private Integer taskId;
        private final World stadiumWorld;
        private final double minX;
        private final double maxX;
        private final double minY;
        private final double maxY;
        private final double minZ;
        private final double maxZ;

        private TheVoidTracker(Stadium stadium, Player caster) {
            minX = stadium.locOfSpot(Spot.RED_1_BACK).getX() - 1;
            maxX = stadium.locOfSpot(Spot.GREEN_2_BACK).getX() + 1;
            minY = stadium.locOfSpot(Spot.RED_1_BACK).getY();
            maxY = stadium.locOfSpot(Spot.GREEN_2_BACK).getY() + 1.5;
            minZ = stadium.locOfSpot(Spot.RED_1_BACK).getZ() - 1;
            maxZ = stadium.locOfSpot(Spot.GREEN_2_BACK).getZ() + 1;
            stadiumWorld = stadium.startingCorner().getWorld();
            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> startLoop(stadium, caster), numTicksToWaitForParticles);
            stadiumWorld.playSound(new Location(stadiumWorld, (minX + maxX) / 2, (minY + maxY) / 2, (minZ + maxZ) / 2), ENTITY_WARDEN_SONIC_CHARGE, 2, 1);
        }

        public void startLoop(Stadium stadium, Player caster) {
            taskId = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
                if (progressInTicks >= numTicks) {
                    oneAnimationFinished(stadium, caster);
                    Bukkit.getScheduler().cancelTask(taskId);
                    return;
                } else if (progressInTicks == (numTicks / 2)) {
                    stadium.allyMinionSpots(caster).stream()
                        .map(stadium::minionFromSpot)
                        .filter(Objects::nonNull)
                        .forEach(Minion::onDeath);
                    stadium.enemyMinionSpots(caster).stream()
                        .map(stadium::minionFromSpot)
                        .filter(Objects::nonNull)
                        .forEach(Minion::onDeath);
                }
                spawnParticles(minX, maxX, minY, maxY, minZ, maxZ);
                progressInTicks++;
            }, 0 /*<-- the initial delay */, 1 /*<-- the interval */).getTaskId();
        }

        private void spawnParticles(double minX, double maxX, double minY, double maxY, double minZ, double maxZ) {
            for (int i = 0; i < numParticlesPerTick; i++) {
                double x = (Math.random() * (maxX - minX)) + minX;
                double y = (Math.random() * (maxY - minY)) + minY;
                double z = (Math.random() * (maxZ - minZ)) + minZ;
                stadiumWorld.spawnParticle(Particle.SQUID_INK, new Location(stadiumWorld, x, y, z), numParticlesPerSpawnParticle);
            }
        }
    }
}
