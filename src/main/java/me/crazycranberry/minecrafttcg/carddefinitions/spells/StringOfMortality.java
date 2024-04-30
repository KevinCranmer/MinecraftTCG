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
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.newAnimationStarted;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.oneAnimationFinished;
import static org.bukkit.Color.BLACK;
import static org.bukkit.Sound.BLOCK_NOTE_BLOCK_FLUTE;

public class StringOfMortality implements SpellCardDefinition {
    private final static Integer particleBeamNumParticles = 9;
    private final static double particleBeamBlocksTraveledPerTick = 1;
    private final static List<Note> song = List.of(
        new Note(0.594604f, 4),
        new Note(0.707107f, 8),
        new Note(0.890899f, 12),
        new Note(0.943874f, 16),
        new Note(0.890899f, 20)
    );
    private final static Sound instrument = BLOCK_NOTE_BLOCK_FLUTE;
    private static final Particle.DustOptions dustOptions = new Particle.DustOptions(BLACK, 1);

    @Override
    public Integer cost() {
        return 1;
    }

    @Override
    public String cardName() {
        return "String of Mortality";
    }

    @Override
    public String cardDescription() {
        return "Deal 1 damage to target minion. If it kills that minion, draw a card.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.UNCOMMON;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        Minion target = stadium.minionFromSpot(targets.get(0));
        newAnimationStarted(stadium, caster, 1);
        new ParticleBeamTracker(stadium, caster, List.of(target.minionInfo().entity()), Particle.REDSTONE, List.of(dustOptions), particleBeamBlocksTraveledPerTick, particleBeamNumParticles, StringOfMortality::onCollide);
        new Song(song, instrument, caster.getEyeLocation()).play();
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(true, true, false, false, false);
    }

    public static void onCollide(Stadium stadium, Player caster, ParticleBeamInfo beam) {
        Optional<Minion> targetMinion = stadium.minionFromEntity(beam.target());
        if (targetMinion.isPresent()) {
            Minion target = targetMinion.get();
            target.onDamageReceived(caster, 1, target.isProtected());
            if (target.health() <= 0) {
                stadium.draw(caster);
                caster.getWorld().spawnParticle(Particle.REDSTONE, target.minionInfo().entity().getEyeLocation(), 7, 0.5, 0.75, 0.5, dustOptions);
            }
        }
        oneAnimationFinished(stadium, caster);
    }
}
