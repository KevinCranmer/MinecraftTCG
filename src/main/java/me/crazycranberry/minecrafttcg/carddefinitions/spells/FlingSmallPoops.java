package me.crazycranberry.minecrafttcg.carddefinitions.spells;

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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static me.crazycranberry.minecrafttcg.CommonFunctions.randomFromList;
import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static org.bukkit.Color.BLACK;
import static org.bukkit.Color.MAROON;
import static org.bukkit.Color.OLIVE;
import static org.bukkit.Sound.BLOCK_SLIME_BLOCK_HIT;
import static org.bukkit.Sound.BLOCK_SLIME_BLOCK_PLACE;
import static org.bukkit.Sound.BLOCK_SLIME_BLOCK_STEP;

public class FlingSmallPoops implements SpellCardDefinition {
    private final static Integer particleBeamNumParticles = 3;
    private final static double particleBeamBlocksTraveledPerTick = 0.33;
    private final static Integer damage = 3;
    private final static List<Sound> poopSounds = List.of(BLOCK_SLIME_BLOCK_HIT, BLOCK_SLIME_BLOCK_PLACE, BLOCK_SLIME_BLOCK_STEP);
    @Override
    public Integer cost() {
        return 6;
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
        return CardRarity.RARE;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        List<LivingEntity> minionsToPoopOn = new ArrayList<>();
        for (Spot spot : stadium.enemyMinionSpots(caster)) {
            Minion minion = spot.minionRef().apply(stadium);
            if (minion != null && !minion.minionInfo().entity().isDead()) {
                minionsToPoopOn.add(minion.minionInfo().entity());
            }
        }
        List<Particle.DustOptions> dustOptions = List.of(
            new Particle.DustOptions(OLIVE, 1),
            new Particle.DustOptions(MAROON, 1),
            new Particle.DustOptions(BLACK, 1)
        );
        new ParticleBeamTracker(stadium, caster, minionsToPoopOn, Particle.REDSTONE, dustOptions, particleBeamBlocksTraveledPerTick, particleBeamNumParticles, FlingSmallPoops::poopCollided);
        caster.getWorld().playSound(caster.getLocation(), randomFromList(poopSounds).get(), 1, 1);
        caster.damage(damage);
    }

    public static void poopCollided(Stadium stadium, Player caster, ParticleBeamInfo beam) {
        Optional<Minion> targetMinion = stadium.minionFromEntity(beam.target());
        if (targetMinion.isPresent()) {
            caster.getWorld().playSound(beam.target().getEyeLocation(), randomFromList(poopSounds).get(), 1, 1);
            targetMinion.get().onDamageReceived(caster, damage, targetMinion.get().isProtected());
        }
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(false, false, false, false, false);
    }
}
