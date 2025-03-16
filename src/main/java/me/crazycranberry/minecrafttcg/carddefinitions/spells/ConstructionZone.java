package me.crazycranberry.minecrafttcg.carddefinitions.spells;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.model.Column;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import me.crazycranberry.minecrafttcg.model.Wall;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.newAnimationStarted;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.oneAnimationFinished;
import static me.crazycranberry.minecrafttcg.model.Spot.BLUE_1_FRONT;
import static me.crazycranberry.minecrafttcg.model.Spot.MIDDLE_X;
import static me.crazycranberry.minecrafttcg.model.Spot.RADIUS_X;
import static me.crazycranberry.minecrafttcg.model.Spot.RADIUS_Z;

public class ConstructionZone implements SpellCardDefinition {
    @Override
    public Integer cost() {
        return 3;
    }

    @Override
    public String cardName() {
        return "Construction Zone";
    }

    @Override
    public String cardDescription() {
        return "Deal 1 damage to all minions and create 1 wall at targeted column that lasts 2 turns.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.UNCOMMON;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        newAnimationStarted(stadium, caster, 1);
        new ConstructionZoneTracker(stadium, caster, targets.get(0).column());
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(true, true, false, true, true);
    }

    private static class ConstructionZoneTracker {
        private final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.GRAY, 2);
        private final int numTicks = 30;
        private final int numParticlesPerTick = 60;
        private Integer progressInTicks = 0;
        private Integer taskId;
        private final World stadiumWorld;
        private final Location middleOfStadium;

        private ConstructionZoneTracker(Stadium stadium, Player caster, Column column) {
            stadiumWorld = stadium.startingCorner().getWorld();
            var blue1FrontLoc = stadium.locOfSpot(BLUE_1_FRONT);
            middleOfStadium = new Location(stadiumWorld, stadium.startingCorner().getX() + MIDDLE_X, blue1FrontLoc.getY() + 0.5, blue1FrontLoc.getZ());
            startLoop(stadium, caster, column);
        }

        public void startLoop(Stadium stadium, Player caster, Column column) {
            taskId = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
                if (progressInTicks >= numTicks) {
                    oneAnimationFinished(stadium, caster);
                    Bukkit.getScheduler().cancelTask(taskId);
                    return;
                } else if (progressInTicks == (numTicks / 2)) {
                    stadium.forAllMinions(minion -> minion.onDamageReceived(caster, 1, minion.isProtected()));
                    stadium.setWall(column, new Wall(stadium, column, Material.COBBLESTONE, 2));
                }
                stadiumWorld.spawnParticle(Particle.DUST, middleOfStadium, numParticlesPerTick, RADIUS_X, 0.5, RADIUS_Z, dustOptions);
                progressInTicks++;
            }, 0 /*<-- the initial delay */, 1 /*<-- the interval */).getTaskId();
        }
    }
}
