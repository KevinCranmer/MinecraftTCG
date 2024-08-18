package me.crazycranberry.minecrafttcg.carddefinitions.spells;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.ParticleBeamInfo;
import me.crazycranberry.minecrafttcg.carddefinitions.ParticleBeamTracker;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.model.Note;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import me.crazycranberry.minecrafttcg.utils.Song;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.newAnimationStarted;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.oneAnimationFinished;
import static me.crazycranberry.minecrafttcg.carddefinitions.CardUtils.UNDEAD_TYPES;
import static me.crazycranberry.minecrafttcg.model.Note.Octave1.A;
import static me.crazycranberry.minecrafttcg.model.Note.Octave1.C;
import static me.crazycranberry.minecrafttcg.model.Note.Octave1.Csharp;
import static me.crazycranberry.minecrafttcg.model.Note.Octave1.Dsharp;
import static me.crazycranberry.minecrafttcg.model.Note.Octave1.E;
import static org.bukkit.Color.GRAY;
import static org.bukkit.Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO;

public class DeadlyMobbing implements SpellCardDefinition {
    private final static Integer particleBeamNumParticles = 9;
    private final static double particleBeamBlocksTraveledPerTick = 0.5;
    private final static List<Note> song = List.of(
        new Note(C, 4),
        new Note(E, 8),
        new Note(Dsharp, 12),
        new Note(Csharp, 16),
        new Note(A, 24)
    );
    private final static Sound instrument = BLOCK_NOTE_BLOCK_DIDGERIDOO;
    private static final Particle.DustOptions dustOptions = new Particle.DustOptions(GRAY, 1);

    @Override
    public Integer cost() {
        return 1;
    }

    @Override
    public String cardName() {
        return "Deadly Mobbing";
    }

    @Override
    public String cardDescription() {
        return String.format("Deal X * 2 damage to target minion where X is the number of %sUndead%s minions you control.", ChatColor.BOLD, ChatColor.RESET);
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.UNCOMMON;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        newAnimationStarted(stadium, caster, 1);
        new ParticleBeamTracker(stadium, caster, List.of(stadium.minionFromSpot(targets.get(0)).minionInfo().entity()), Particle.DUST, List.of(dustOptions), particleBeamBlocksTraveledPerTick, particleBeamNumParticles, DeadlyMobbing::onCollide);
        new Song(song, instrument, caster.getEyeLocation()).play();
    }

    public static void onCollide(Stadium stadium, Player caster, ParticleBeamInfo beam) {
        int numUndead = stadium.allyMinionSpots(caster).stream()
            .map(stadium::minionFromSpot)
            .filter(Objects::nonNull)
            .filter(m -> UNDEAD_TYPES.contains(m.minionInfo().entity().getType()))
            .toList().size();
        if (numUndead == 0) {
            return;
        }
        Optional<Minion> targetMinion = stadium.minionFromEntity(beam.target());
        if (targetMinion.isPresent()) {
            Minion target = targetMinion.get();
            target.onDamageReceived(caster, 2 * numUndead, target.isProtected());
        }
        oneAnimationFinished(stadium, caster);
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(false, true, false, false, false);
    }
}
