package me.crazycranberry.minecrafttcg.carddefinitions.spells;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static me.crazycranberry.minecrafttcg.CommonFunctions.randomFromList;
import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static org.bukkit.Color.BLACK;
import static org.bukkit.Color.MAROON;
import static org.bukkit.Color.OLIVE;
import static org.bukkit.Sound.BLOCK_SLIME_BLOCK_HIT;
import static org.bukkit.Sound.BLOCK_SLIME_BLOCK_PLACE;
import static org.bukkit.Sound.BLOCK_SLIME_BLOCK_STEP;

public class FlingSmallPoops implements SpellCardDefinition {
    private final static Integer numParticlesPerTick = 2;
    private final static Integer numParticlesPerSpawnParticle = 3;
    private final static Integer damage = 3;
    private final static List<Sound> poopSounds = List.of(BLOCK_SLIME_BLOCK_HIT, BLOCK_SLIME_BLOCK_PLACE, BLOCK_SLIME_BLOCK_STEP);
    @Override
    public Integer cost() {
        return 5;
    }

    @Override
    public String cardName() {
        return "Fling Small Poops";
    }

    @Override
    public String cardDescription() {
        return "Deal 3 damage to yourself and all enemy minions.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.LEGENDARY;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        new ThePoopTracker(stadium, caster);
        caster.getWorld().playSound(caster.getLocation(), randomFromList(poopSounds).get(), 1, 1);
        caster.damage(damage);
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(false, false, false, false);
    }

    private static class ThePoopTracker {
        private Integer taskId;
        private final World stadiumWorld;
        private final List<PoopFlingInfo> minionsToPoopOn;
        private final static List<Color> poopColors = List.of(BLACK, MAROON, OLIVE);
        private final static double blocksTraveledPerTick = 0.33;
        private final static int particlesPerTick = 3;

        private ThePoopTracker(Stadium stadium, Player caster) {
            this.stadiumWorld = stadium.startingCorner().getWorld();
            this.minionsToPoopOn = new ArrayList<>();
            for (Spot spot : stadium.enemyMinionSpots(caster)) {
                Minion minion = spot.minionRef().apply(stadium);
                if (minion != null) {
                    minionsToPoopOn.add(new PoopFlingInfo(caster.getLocation().clone(), minion));
                }
            }
            startLoop(caster);
        }

        public void startLoop(Player caster) {
            taskId = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
                for (PoopFlingInfo poop : minionsToPoopOn) {
                    Vector direction = poop.target().minionInfo().entity().getEyeLocation().toVector().subtract(poop.particleLoc().toVector()).normalize().multiply(blocksTraveledPerTick / (double) particlesPerTick);
                    for (int i = 0; i < particlesPerTick; i++) {
                        if (poop.alreadyPooped()) {
                            continue;
                        }
                        spawnParticles(poop.particleLoc());
                        if (poop.particleLoc().distanceSquared(poop.target().minionInfo().entity().getEyeLocation()) < 0.5) {
                            stadiumWorld.playSound(poop.target().minionInfo().entity().getEyeLocation(), randomFromList(poopSounds).get(), 1, 1);
                            poop.target().onDamageReceived(caster, damage, poop.target().isProtected());
                            poop.target().minionInfo().entity().damage(0);
                            poop.pooped();
                            break;
                        }
                        poop.particleLoc().add(direction);
                    }
                }
                if (minionsToPoopOn.stream().allMatch(PoopFlingInfo::alreadyPooped)) {
                    Bukkit.getScheduler().cancelTask(taskId);
                }
            }, 0 /*<-- the initial delay */, 1 /*<-- the interval */).getTaskId();
        }

        private void spawnParticles(Location loc) {
            stadiumWorld.spawnParticle(Particle.REDSTONE, loc, numParticlesPerSpawnParticle, new Particle.DustOptions(randomFromList(poopColors).get(), 1));
        }
    }

    private static class PoopFlingInfo {
        private final Minion target;
        private Location particleLoc;
        private boolean alreadyPooped;

        private PoopFlingInfo(Location particleLoc, Minion target) {
            this.particleLoc = particleLoc;
            this.target = target;
            this.alreadyPooped = false;
        }

        public Location particleLoc() {
            return particleLoc;
        }

        public void setParticleLoc(Location particleLoc) {
            this.particleLoc = particleLoc;
        }

        public Minion target() {
            return target;
        }

        public boolean alreadyPooped() {
            return alreadyPooped;
        }

        public void pooped() {
            this.alreadyPooped = true;
        }
    }
}
