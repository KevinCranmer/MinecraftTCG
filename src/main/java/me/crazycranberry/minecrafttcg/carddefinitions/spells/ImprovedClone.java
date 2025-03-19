package me.crazycranberry.minecrafttcg.carddefinitions.spells;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.MultiTargetCard;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.Player;

import java.util.List;

import static me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition.summonMinion;

public class ImprovedClone implements SpellCardDefinition, MultiTargetCard {
    @Override
    public Integer cost() {
        return 4;
    }

    @Override
    public String cardName() {
        return "Improved Clone";
    }

    @Override
    public String cardDescription() {
        return "Summon a copy of target minion at target spot. Give that minion +1/+1";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.UNCOMMON;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        if (targets.size() < 2) {
            return;
        }
        Minion toBeCloned = stadium.minionFromSpot(targets.get(0));
        if (toBeCloned == null) {
            return;
        }
        Minion newMinion = summonMinion(targets.get(1), stadium, caster, toBeCloned.getClass(), toBeCloned.cardDef());
        newMinion.loadAllMinionStats(toBeCloned);
        newMinion.addPermanentStrength(1);
        newMinion.addPermanentMaxHealth(1);
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(true, true, false, false, false);
    }

    @Override
    public List<TargetRules> targetRulesForExtraTargets() {
        return List.of(new TargetRules(false, false, false, true, false));
    }

    @Override
    public boolean isValidAdditionalTarget(Player p, Stadium stadium, Card card, List<Spot> targets, Spot newTarget) {
        return true;
    }
}
