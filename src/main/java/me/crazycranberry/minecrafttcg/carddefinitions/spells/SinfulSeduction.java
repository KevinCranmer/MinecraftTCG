package me.crazycranberry.minecrafttcg.carddefinitions.spells;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.ParticleBeamInfo;
import me.crazycranberry.minecrafttcg.carddefinitions.ParticleBeamTracker;
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
import static org.bukkit.Sound.BLOCK_NOTE_BLOCK_HARP;
import static org.bukkit.Sound.BLOCK_SLIME_BLOCK_HIT;
import static org.bukkit.Sound.BLOCK_SLIME_BLOCK_PLACE;
import static org.bukkit.Sound.BLOCK_SLIME_BLOCK_STEP;

public class SinfulSeduction implements SpellCardDefinition {
    private final static Integer particleBeamNumParticles = 1;
    private final static double particleBeamBlocksTraveledPerTick = 0.2;
    private int taskId;
    private int tickProgress = 0;
    private int pitchProgress = 0;
    private int ticksBetweenEachNote = 3;
    private List<Float> pitches = List.of(0.707f, 0.794f, 0.891f, 0.944f, 1.059f, 1.189f, 1.335f, 1.414f);
    @Override
    public Integer cost() {
        return 8;
    }

    @Override
    public String cardName() {
        return "Sinful Seduction";
    }

    @Override
    public String cardDescription() {
        return "Gain Control of target Enemy Minion";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.LEGENDARY;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        new ParticleBeamTracker(stadium, caster, List.of(targets.get(0).minionRef().apply(stadium)), Particle.HEART, null, particleBeamBlocksTraveledPerTick, particleBeamNumParticles, SinfulSeduction::onParticleBeamCollided);
        playHarpScale(caster);
    }

    private void playHarpScale(Player caster) {
        tickProgress = 0;
        pitchProgress = 0;
        taskId = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
            if (pitchProgress >= pitches.size()) {
                Bukkit.getScheduler().cancelTask(taskId);
                return;
            }
            if (tickProgress % ticksBetweenEachNote == 0) {
                caster.getWorld().playSound(caster.getLocation(), BLOCK_NOTE_BLOCK_HARP, 1, pitches.get(pitchProgress));
                pitchProgress++;
            }
            tickProgress++;
        }, 0 /*<-- the initial delay */, 1 /*<-- the interval */).getTaskId();
    }

    public static void onParticleBeamCollided(Stadium stadium, Player caster, ParticleBeamInfo beam) {
        Spot oldHome = beam.target().minionInfo().spot();
        Spot newHome = getNewHome(stadium, oldHome);
        beam.target().minionInfo().setSpot(newHome);
        beam.target().minionInfo().setMaster(caster);
        newHome.minionSetRef().accept(stadium, beam.target(), false);
        oldHome.minionSetRef().accept(stadium, null, false);
        beam.target().setupGoals();
        stadium.updateCustomName(beam.target());
        if (stadium.isWalled(beam.target())) {
            beam.target().minionInfo().entity().teleport(stadium.locOfSpot(beam.target().minionInfo().spot()));
        }
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(false, true, false, false, false);
    }

    @Override
    public boolean isValidInitialTarget(Player p, Stadium stadium, Card card, Spot newTarget) {
        return getNewHome(stadium, newTarget) != null;
    }

    private static Spot getNewHome(Stadium stadium, Spot target) {
        Spot opposingFrontRankSpot = Spot.opposingFrontRankSpot(target);
        if (opposingFrontRankSpot.minionRef().apply(stadium) == null) {
            return opposingFrontRankSpot;
        }
        Spot opposingBackRankSpot = Spot.opposingBackRankSpot(target);
        if (opposingBackRankSpot.minionRef().apply(stadium) == null) {
            return opposingBackRankSpot;
        }
        return null;
    }
}