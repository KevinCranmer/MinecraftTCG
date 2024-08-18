package me.crazycranberry.minecrafttcg.carddefinitions.minions.boneabomination;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

import static me.crazycranberry.minecrafttcg.CommonFunctions.randomFromList;
import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.newAnimationStarted;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.oneAnimationFinished;
import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.BONE_ABOMINATION;
import static me.crazycranberry.minecrafttcg.carddefinitions.CardUtils.UNDEAD_TYPES;
import static me.crazycranberry.minecrafttcg.carddefinitions.minions.boneabomination.BoneAbominationDef.DEATH_DAMAGE;
import static org.bukkit.Color.BLACK;
import static org.bukkit.Color.MAROON;
import static org.bukkit.Color.OLIVE;

public class BoneAbomination extends Minion {
    private final static Integer numTicksToWaitForParticles = 10; // Because the sound is slowish
    private final static Integer numTicks = 20;
    private final static Integer numParticlesPerTick = 40;
    private final static Integer numParticlesPerSpawnParticle = 3;
    private final static List<Particle.DustOptions> dustOptions = List.of(
        new Particle.DustOptions(OLIVE, 1),
        new Particle.DustOptions(MAROON, 1),
        new Particle.DustOptions(BLACK, 1)
    );

    public BoneAbomination(MinionInfo minionInfo) {
        super(BONE_ABOMINATION.card(), minionInfo);
    }

    @Override
    public String signDescription() {
        return String.format("When dies,\nDeals %s to each\nnon-undead minion.", DEATH_DAMAGE);
    }

    @Override
    public void onDeath() {
        super.onDeath();
        newAnimationStarted(this.minionInfo().stadium(), this.minionInfo().master(), 1);
        new SludgeTracker(this.minionInfo().stadium(), this.minionInfo().master());
    }

    private static class SludgeTracker {
        private Integer progressInTicks = 0;
        private Integer taskId;
        private final World stadiumWorld;
        private final double minX;
        private final double maxX;
        private final double minY;
        private final double maxY;
        private final double minZ;
        private final double maxZ;

        private SludgeTracker(Stadium stadium, Player caster) {
            minX = stadium.locOfSpot(Spot.RED_1_BACK).getX() - 1;
            maxX = stadium.locOfSpot(Spot.GREEN_2_BACK).getX() + 1;
            minY = stadium.locOfSpot(Spot.RED_1_BACK).getY();
            maxY = stadium.locOfSpot(Spot.GREEN_2_BACK).getY() + 1.5;
            minZ = stadium.locOfSpot(Spot.RED_1_BACK).getZ() - 1;
            maxZ = stadium.locOfSpot(Spot.GREEN_2_BACK).getZ() + 1;
            stadiumWorld = stadium.startingCorner().getWorld();
            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> startLoop(stadium, caster), numTicksToWaitForParticles);
        }

        public void startLoop(Stadium stadium, Player caster) {
            taskId = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
                if (progressInTicks >= numTicks) {
                    oneAnimationFinished(stadium, caster);
                    Bukkit.getScheduler().cancelTask(taskId);
                    return;
                } else if (progressInTicks == (numTicks / 2)) {
                    stadiumWorld.playSound(new Location(stadiumWorld, (minX + maxX) / 2, (minY + maxY) / 2, (minZ + maxZ) / 2), Sound.BLOCK_SLIME_BLOCK_PLACE, 2, 1);
                    stadium.allyMinionSpots(caster).stream()
                        .map(stadium::minionFromSpot)
                        .filter(Objects::nonNull)
                        .filter(m -> !UNDEAD_TYPES.contains(m.minionInfo().entity().getType()))
                        .forEach(m -> m.onDamageReceived(caster, DEATH_DAMAGE, m.isProtected()));
                    stadium.enemyMinionSpots(caster).stream()
                        .map(stadium::minionFromSpot)
                        .filter(Objects::nonNull)
                        .filter(m -> !UNDEAD_TYPES.contains(m.minionInfo().entity().getType()))
                        .forEach(m -> m.onDamageReceived(caster, DEATH_DAMAGE, m.isProtected()));
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
                stadiumWorld.spawnParticle(Particle.DUST, new Location(stadiumWorld, x, y, z), numParticlesPerSpawnParticle, randomFromList(dustOptions).get());
            }
        }
    }
}
