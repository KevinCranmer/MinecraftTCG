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

public class DoubleKill implements SpellCardDefinition, MultiTargetCard {
    @Override
    public Integer cost() {
        return 6;
    }

    @Override
    public String cardName() {
        return "Double Kill";
    }

    @Override
    public String cardDescription() {
        return "Destroy two target minions.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.RARE;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        Minion minion = targets.get(0).minionRef().apply(stadium);
        if (minion != null) {
            minion.onDeath();
        }
        Minion minion2 = targets.get(1).minionRef().apply(stadium);
        if (minion2 != null) {
            minion2.onDeath();
        }
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(true, true, false, false, false);
    }

    @Override
    public List<TargetRules> targetRulesForExtraTargets() {
        return List.of(new TargetRules(true, true, false, false, false));
    }

    @Override
    public boolean isValidAdditionalTarget(Player p, Stadium stadium, Card card, List<Spot> targets, Spot newTarget) {
        return !targets.get(0).equals(newTarget);
    }
}
