package me.crazycranberry.minecrafttcg.carddefinitions.cantrips;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.ParticleBeamInfo;
import me.crazycranberry.minecrafttcg.carddefinitions.ParticleBeamTracker;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.newAnimationStarted;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.oneAnimationFinished;
import static me.crazycranberry.minecrafttcg.carddefinitions.CardUtils.handleOverkillDamage;
import static me.crazycranberry.minecrafttcg.managers.StadiumManager.PLAYER_PROXY_ENTITY_TYPE;

public class FireBlast implements CantripCardDefinition {
    private static final int damage = 5;

    @Override
    public Integer cost() {
        return 4;
    }

    @Override
    public String cardName() {
        return "Fire Blast";
    }

    @Override
    public String cardDescription() {
        return String.format("Deal %s %sOverkill%s damage to target enemy minion", damage, ChatColor.BOLD, ChatColor.RESET);
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.UNCOMMON;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        newAnimationStarted(stadium, caster, 1);
        new ParticleBeamTracker(stadium, caster, List.of(targets.get(0).minionRef().apply(stadium).minionInfo().entity()), Particle.LAVA, null, 0.8, 3, FireBlast::beamCollided);
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(false, true, false, false, false);
    }

    @Override
    public Boolean canCastDuringCombat() {
        return true;
    }

    public static void beamCollided(Stadium stadium, Player caster, ParticleBeamInfo beam) {
        Optional<Minion> maybeTarget = stadium.minionFromEntity(beam.target());
        maybeTarget.ifPresent(minion -> handleOverkillDamage(minion, damage, null, false));
        oneAnimationFinished(stadium, caster);
    }
}
