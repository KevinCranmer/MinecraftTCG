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

import java.util.List;
import java.util.Optional;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardUtils.dealDamage;

public class LightningStrike implements CantripCardDefinition {
    private static final int damage = 3;

    @Override
    public Integer cost() {
        return 2;
    }

    @Override
    public String cardName() {
        return "Lightning Strike";
    }

    @Override
    public String cardDescription() {
        return String.format("Deal %s damage to target minion or player.", damage);
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.UNCOMMON;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        Optional<LivingEntity> maybeTarget = stadium.livingEntityFromSpot(targets.get(0));
        if (maybeTarget.isPresent()) {
            LivingEntity target = maybeTarget.get();
            target.getLocation().getWorld().strikeLightningEffect(target.getLocation());
            dealDamage(target, caster, stadium, damage);
        }
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(true, true, true, false, false);
    }

    @Override
    public Boolean canCastDuringCombat() {
        return true;
    }

    public static void damage(Stadium stadium, Player caster, ParticleBeamInfo beam) {
        if (beam.target() instanceof Player) {
            beam.target().damage(3);
        } else {
            Optional<Minion> targetMinion = stadium.minionFromEntity(beam.target());
            targetMinion.ifPresent(minion -> minion.onDamageReceived(caster, 3, targetMinion.get().isProtected()));
        }
    }
}
