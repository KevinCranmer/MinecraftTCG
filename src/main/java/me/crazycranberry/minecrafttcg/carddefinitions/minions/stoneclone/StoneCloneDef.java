package me.crazycranberry.minecrafttcg.carddefinitions.minions.stoneclone;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.MultiTargetCard;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import java.util.List;

public class StoneCloneDef implements MinionCardDefinition, MultiTargetCard {
    @Override
    public Integer cost() {
        return 4;
    }

    @Override
    public String cardName() {
        return "Stone Clone";
    }

    @Override
    public String cardDescription() {
        return "Copies another minions abilities and stats, and gives it ranged.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.RARE;
    }

    @Override
    public List<TargetRules> targetRulesForExtraTargets() {
        return List.of(new TargetRules(true, true, false, false,false));
    }

    @Override
    public boolean isValidAdditionalTarget(Player p, Stadium stadium, Card card, List<Spot> targets, Spot newTarget) {
        return true;
    }

    @Override
    public Integer strength() {
        return 0;
    }

    @Override
    public Integer maxHealth() {
        return 0;
    }

    @Override
    public EntityType minionType() {
        return EntityType.HUSK;
    }

    // This should never be called because we're using the cloned minions class
    @Override
    public Class<? extends Minion> minionClass() {
        return null;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        if (!targets.isEmpty()) {
            Spot targetSpot = targets.get(0);
            if (targetSpot != null) {
                Minion targetMinion = targetSpot.minionRef().apply(stadium);
                if (targetMinion != null) {
                    //MinionCardDefinition.summonMinion(targets.get(0), stadium, caster, minionClass(), minionType(), null, entityAdjustment());   <-- this method is part of what I refactored. So new params, shorter list now.
                }
            }
        }
        // Since we're giving him ranged, I'd say you should give him a bow, look at some of the other ranged skeleton minions as an example for giving them a bow

        // Since this is a multitargetcard, you'll have more than 1 targets.
        // For a minion card, the first target will always be the spot to summon the minion
        // then in this case, the second target will be the spot of the minion you want to copy
        // So you need to summon a minion at targets.get(0) and the minionClass should be the minion you want to copy from targets.get(1)
        // And then this instance of this class would be the final parameter in summonMinion()

        // You also want this minion to be named Stone Clone when sign examined, so look for a way to rename a minion
        // Then we want this minion to be ranged, so find a way to make him ranged

        // Then bonus if you want, you could add some particles to the minion being copied and/or the stone clone
    }
}
