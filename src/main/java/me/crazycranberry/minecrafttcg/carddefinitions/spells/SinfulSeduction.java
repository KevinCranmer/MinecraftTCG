package me.crazycranberry.minecrafttcg.carddefinitions.spells;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.ParticleBeamInfo;
import me.crazycranberry.minecrafttcg.carddefinitions.ParticleBeamTracker;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.model.Note;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import me.crazycranberry.minecrafttcg.utils.Song;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.newAnimationStarted;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.oneAnimationFinished;
import static me.crazycranberry.minecrafttcg.model.Note.Octave1.A;
import static me.crazycranberry.minecrafttcg.model.Note.Octave1.B;
import static me.crazycranberry.minecrafttcg.model.Note.Octave1.C;
import static me.crazycranberry.minecrafttcg.model.Note.Octave1.D;
import static me.crazycranberry.minecrafttcg.model.Note.Octave1.E;
import static me.crazycranberry.minecrafttcg.model.Note.Octave1.F;
import static me.crazycranberry.minecrafttcg.model.Note.Octave1.G;
import static org.bukkit.Sound.BLOCK_NOTE_BLOCK_HARP;

public class SinfulSeduction implements SpellCardDefinition {
    private final static Integer particleBeamNumParticles = 1;
    private final static double particleBeamBlocksTraveledPerTick = 0.2;
    private final List<Note> song = List.of(
      new Note(C, 3),
      new Note(D, 6),
      new Note(E, 9),
      new Note(F, 12),
      new Note(G, 15),
      new Note(A, 18),
      new Note(B, 21),
      new Note(C, 24)
    );

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
        new ParticleBeamTracker(stadium, caster, List.of(stadium.minionFromSpot(targets.get(0)).minionInfo().entity()), Particle.HEART, null, particleBeamBlocksTraveledPerTick, particleBeamNumParticles, SinfulSeduction::onParticleBeamCollided);
        new Song(song, BLOCK_NOTE_BLOCK_HARP, caster.getLocation()).play();
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
        stadium.setMinionAtSpot(newHome, target, false);
        stadium.setMinionAtSpot(oldHome, null, false);
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
        if (stadium.minionFromSpot(opposingFrontRankSpot) == null) {
            return opposingFrontRankSpot;
        }
        Spot opposingBackRankSpot = Spot.opposingBackRankSpot(target);
        if (stadium.minionFromSpot(opposingBackRankSpot) == null) {
            return opposingBackRankSpot;
        }
        return null;
    }
}
