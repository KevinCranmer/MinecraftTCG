package me.crazycranberry.minecrafttcg.carddefinitions.cantrips;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.ParticleBeamInfo;
import me.crazycranberry.minecrafttcg.carddefinitions.ParticleBeamTracker;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RadicalShift implements CantripCardDefinition {
    private final static Integer particleBeamNumParticles = 2;
    private final static double particleBeamBlocksTraveledPerTick = 0.7;
    private final static int damage = 3;

    @Override
    public Integer cost() {
        return 4;
    }

    @Override
    public String cardName() {
        return "Radical Shift";
    }

    @Override
    public String cardDescription() {
        return String.format("Deal %s damage to target minion and heal all of your minions to max health.", damage);
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.RARE;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        new ParticleBeamTracker(stadium, caster, List.of(targets.get(0).minionRef().apply(stadium).minionInfo().entity()), Particle.LAVA, null, particleBeamBlocksTraveledPerTick, particleBeamNumParticles, RadicalShift::onDamageBeamCollided);
        List<LivingEntity> alliesToHeal = new ArrayList<>();
        for (Spot spot : stadium.allyMinionSpots(caster)) {
            Minion minion = spot.minionRef().apply(stadium);
            if (minion != null && !minion.minionInfo().entity().isDead()) {
                alliesToHeal.add(minion.minionInfo().entity());
            }
        }
        new ParticleBeamTracker(stadium, caster, alliesToHeal, Particle.WAX_ON, null, particleBeamBlocksTraveledPerTick, particleBeamNumParticles, RadicalShift::onHealBeamCollided);
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(false, true, false ,false, false);
    }

    @Override
    public Boolean canCastDuringCombat() {
        return false;
    }

    public static void onDamageBeamCollided(Stadium stadium, Player caster, ParticleBeamInfo beam) {
        Optional<Minion> targetMinion = stadium.minionFromEntity(beam.target());
        targetMinion.ifPresent(minion -> minion.onDamageReceived(caster, damage, targetMinion.get().isProtected()));
    }

    public static void onHealBeamCollided(Stadium stadium, Player caster, ParticleBeamInfo beam) {
        Optional<Minion> targetMinion = stadium.minionFromEntity(beam.target());
        if (targetMinion.isPresent() && targetMinion.get().health() < targetMinion.get().maxHealth()) {
            targetMinion.get().onHeal(targetMinion.get().maxHealth() - targetMinion.get().health());
        }
    }
}
