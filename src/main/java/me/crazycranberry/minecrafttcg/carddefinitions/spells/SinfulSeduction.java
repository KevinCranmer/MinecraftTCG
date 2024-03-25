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
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.newAnimationStarted;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.oneAnimationFinished;
import static org.bukkit.Sound.BLOCK_NOTE_BLOCK_HARP;

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
        return CardRarity.RARE;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        newAnimationStarted(stadium, caster, 1);
        new ParticleBeamTracker(stadium, caster, List.of(targets.get(0).minionRef().apply(stadium).minionInfo().entity()), Particle.HEART, null, particleBeamBlocksTraveledPerTick, particleBeamNumParticles, SinfulSeduction::onParticleBeamCollided);
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
        Optional<Minion> targetMinion = stadium.minionFromEntity(beam.target());
        if (targetMinion.isEmpty()) {
            return;
        }
        Minion target = targetMinion.get();
        Spot oldHome = target.minionInfo().spot();
        Spot newHome = getNewHome(stadium, oldHome);
        target.minionInfo().setSpot(newHome);
        target.minionInfo().setMaster(caster);
        newHome.minionSetRef().accept(stadium, target, false);
        oldHome.minionSetRef().accept(stadium, null, false);
        target.setupGoals();
        stadium.updateCustomName(target);
        if (stadium.isWalled(target)) {
            target.minionInfo().entity().teleport(stadium.locOfSpot(target.minionInfo().spot()));
        }
        oneAnimationFinished(stadium, caster);
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
