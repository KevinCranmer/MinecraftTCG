package me.crazycranberry.minecrafttcg.carddefinitions.minions.theduke;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.kevinthesmith.KevinTheSmith;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.mikethestoryteller.MikeTheStoryTeller;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.newAnimationStarted;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.oneAnimationFinished;

public class TheDuke extends Minion {
    public TheDuke(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo);
        this.setAttacksPerTurn(0);
    }

    @Override
    public void onTurnEnd() {
        boolean hasMike = false;
        boolean hasKevin = false;
        List<Spot> allySpots = this.minionInfo().stadium().allyMinionSpots(this.minionInfo().master());
        for (Spot spot : allySpots) {
            Minion minion = this.minionInfo().stadium().minionFromSpot(spot);
            if (minion instanceof MikeTheStoryTeller) {
                hasMike = true;
            } else if (minion instanceof KevinTheSmith) {
                hasKevin = true;
            }
        }
        if (hasMike && hasKevin) {
            newAnimationStarted(this.minionInfo().stadium(), this.minionInfo().master(), 1);
            new ExodiaTracker(this.minionInfo().stadium(), this.minionInfo().master());
        }
    }

    @Override
    public String signDescription() {
        return "Auto Wins if Duke,\nMike, and Kevin\nare alive at the\nend of the turn";
    }


    private static class ExodiaTracker {
        private Integer taskId;
        private final World stadiumWorld;
        private final List<ExodiaInfo> exodiaInfos;
        private final static int particlesPerTick = 3;
        private int progressTicks = 0;
        private final static int ticksPhase1 = 60;
        private final static int ticksPhase2 = 40;
        private final static int ticksPhase3 = 30;

        private ExodiaTracker(Stadium stadium, Player caster) {
            this.stadiumWorld = stadium.startingCorner().getWorld();
            this.exodiaInfos = new ArrayList<>();
            for (Spot spot : stadium.allyMinionSpots(caster)) {
                Minion minion = stadium.minionFromSpot(spot);
                if (minion instanceof TheDuke || minion instanceof MikeTheStoryTeller || minion instanceof KevinTheSmith) {
                    exodiaInfos.add(new ExodiaInfo(minion, stadium.opponent(caster)));
                }
            }
            startLoop(stadium.startingCorner(), stadium, caster);
        }

        public void startLoop(Location startingLoc, Stadium stadium, Player master) {
            taskId = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
                for (ExodiaInfo exodia : exodiaInfos) {
                    if (progressTicks > ticksPhase1 + ticksPhase2 + ticksPhase3) {
                        oneAnimationFinished(stadium, master);
                        exodia.target().damage(1000);
                        Bukkit.getScheduler().cancelTask(taskId);
                        break;
                    } else if (progressTicks > ticksPhase1 + ticksPhase2) {
                        spawnParticles(exodia);
                        Vector direction = exodia.target().getEyeLocation().toVector().subtract(exodia.particleLoc().toVector()).multiply((double) (progressTicks - ticksPhase2 - ticksPhase1) / (double) ticksPhase3);
                        exodia.setParticleLoc(exodia.particleLoc().add(direction));
                    } else if (progressTicks > ticksPhase1) {
                        spawnParticles(exodia);
                    } else {
                        spawnParticles(exodia);
                        Location targetLoc = startingLoc.clone().add(exodia.exodiaPiece().offset());
                        Vector direction = targetLoc.toVector().subtract(exodia.startingLoc().toVector());
                        exodia.setParticleLoc(exodia.startingLoc().clone().add(direction.multiply((double) progressTicks / (double) ticksPhase1)));
                    }
                }
                progressTicks++;
            }, 0 /*<-- the initial delay */, 1 /*<-- the interval */).getTaskId();
        }

        private void spawnParticles(ExodiaInfo exodia) {
            stadiumWorld.spawnParticle(Particle.DUST, exodia.particleLoc(), particlesPerTick, new Particle.DustOptions(exodia.exodiaPiece().color(), 1));
        }
    }

    private static class ExodiaInfo {
        private final Player target;
        private Location particleLoc;
        private final Location startingLoc;
        private final ExodiaPiece piece;

        private ExodiaInfo(Minion exodiaMinion, Player target) {
            this.particleLoc = exodiaMinion.minionInfo().entity().getLocation();
            this.startingLoc = exodiaMinion.minionInfo().entity().getLocation().clone();
            this.target = target;
            Class<? extends Minion> exodiaClass = exodiaMinion.getClass();
            if (exodiaClass.equals(KevinTheSmith.class)) {
                this.piece = ExodiaPiece.KEVIN;
            } else if (exodiaClass.equals(MikeTheStoryTeller.class)) {
                this.piece = ExodiaPiece.MIKE;
            } else {
                this.piece = ExodiaPiece.DUKE;
            }
        }

        public Location particleLoc() {
            return particleLoc;
        }

        public void setParticleLoc(Location newLoc) {
            this.particleLoc = newLoc;
        }

        public Location startingLoc() {
            return startingLoc;
        }

        public Player target() {
            return target;
        }

        public ExodiaPiece exodiaPiece() {
            return piece;
        }
    }

    private enum ExodiaPiece {
        KEVIN(new Vector(13, 4.75, 5.25), Color.GREEN),
        MIKE(new Vector(13, 4.75, 5.75), Color.BLUE),
        DUKE(new Vector(13, 5.25, 5.5), Color.RED);

        private final Vector offset;
        private final Color color;

        ExodiaPiece(Vector v, Color color) {
            this.offset = v;
            this.color = color;
        }

        public Vector offset() {
            return offset;
        }

        public Color color() {
            return color;
        }
    }
}
