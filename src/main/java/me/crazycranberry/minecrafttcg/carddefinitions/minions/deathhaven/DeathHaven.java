package me.crazycranberry.minecrafttcg.carddefinitions.minions.deathhaven;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.model.Note;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import me.crazycranberry.minecrafttcg.utils.Song;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Objects;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.newAnimationStarted;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.oneAnimationFinished;
import static me.crazycranberry.minecrafttcg.carddefinitions.Card.IS_CARD_KEY;
import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.DEATH_HAVEN;
import static me.crazycranberry.minecrafttcg.model.Note.Octave1.C;
import static me.crazycranberry.minecrafttcg.model.Note.Octave1.Dsharp;
import static me.crazycranberry.minecrafttcg.model.Note.Octave1.Fsharp;
import static org.bukkit.Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO;
import static org.bukkit.Sound.BLOCK_NOTE_BLOCK_FLUTE;

public class DeathHaven extends Minion {
    private final static Integer numTicksToWaitForParticles = 22;
    private final static Integer numTicks = 30;
    private final static Integer numParticlesPerTick = 60;
    private final static Integer numParticlesPerSpawnParticle = 3;

    public DeathHaven(MinionInfo minionInfo) {
        super(DEATH_HAVEN.card(), minionInfo);
    }

    @Override
    public String signDescription() {
        return "";
    }

    @Override
    public void onEnter() {
        super.onEnter();
        newAnimationStarted(this.minionInfo().stadium(), this.minionInfo().master(), 1);
        new DeathHavenTracker(this.minionInfo().stadium(), this.minionInfo().master(), this);
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> discardHand(this.minionInfo().master()), numTicksToWaitForParticles);
    }

    private void discardHand(Player master) {
        for (int i = 0; i < master.getInventory().getContents().length; i++) {
            ItemStack item = master.getInventory().getContents()[i];
            if (item == null) {
                continue;
            }
            if (Boolean.TRUE.equals(item.getItemMeta().getPersistentDataContainer().get(IS_CARD_KEY, PersistentDataType.BOOLEAN))) {
                master.getInventory().remove(item);
            }
        }
    }

    private static class DeathHavenTracker {
        private Integer progressInTicks = 0;
        private Integer taskId;
        private final World stadiumWorld;
        private final Minion deathHaven;
        private final double minX;
        private final double maxX;
        private final double minY;
        private final double maxY;
        private final double minZ;
        private final double maxZ;
        private final List<Note> song = List.of(
            new Note(Dsharp, 0),
            new Note(C, 10),
            new Note(Fsharp, 22)
//            new Note(Note.Octave2.A, 22),
//            new Note(Note.Octave2.C, 22),
//            new Note(Note.Octave2.Dsharp, 22),
//            new Note(Note.Octave2.Fsharp, 22),
        );
        private final List<Note> song2 = List.of(
            new Note(Note.Octave2.A, 32),
            new Note(Note.Octave2.C, 35),
            new Note(Note.Octave2.Dsharp, 38),
            new Note(Note.Octave2.Fsharp, 41),
            new Note(Note.Octave2.A, 44),
            new Note(Note.Octave2.C, 47),
            new Note(Note.Octave2.Dsharp, 50),
            new Note(Note.Octave2.Fsharp, 53)
        );

        private DeathHavenTracker(Stadium stadium, Player caster, Minion deathHaven) {
            this.deathHaven = deathHaven;
            minX = stadium.locOfSpot(Spot.RED_1_BACK).getX() - 3;
            maxX = stadium.locOfSpot(Spot.GREEN_2_BACK).getX() + 3;
            minY = stadium.locOfSpot(Spot.RED_1_BACK).getY();
            maxY = stadium.locOfSpot(Spot.GREEN_2_BACK).getY() + 1.5;
            minZ = stadium.locOfSpot(Spot.RED_1_BACK).getZ() - 3;
            maxZ = stadium.locOfSpot(Spot.GREEN_2_BACK).getZ() + 3;
            stadiumWorld = stadium.startingCorner().getWorld();
            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> startLoop(stadium, caster), numTicksToWaitForParticles);
            new Song(song, BLOCK_NOTE_BLOCK_DIDGERIDOO, caster.getLocation()).play();
            new Song(song2, BLOCK_NOTE_BLOCK_FLUTE, caster.getLocation()).play();
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
                        .filter(m -> !m.equals(deathHaven))
                        .forEach(Minion::onDeath);
                    stadium.enemyMinionSpots(caster).stream()
                        .map(stadium::minionFromSpot)
                        .filter(Objects::nonNull)
                        .filter(m -> !m.equals(deathHaven))
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
                stadiumWorld.spawnParticle(Particle.LAVA, new Location(stadiumWorld, x, y, z), numParticlesPerSpawnParticle);
            }
        }
    }
}
