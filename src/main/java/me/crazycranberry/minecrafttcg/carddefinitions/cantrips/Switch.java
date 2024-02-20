package me.crazycranberry.minecrafttcg.carddefinitions.cantrips;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.MultiTargetCard;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.Player;

import java.util.List;

public class Switch implements CantripCardDefinition, MultiTargetCard {
    @Override
    public Integer cost() {
        return 1;
    }

    @Override
    public String cardName() {
        return "Switch";
    }

    @Override
    public String cardDescription() {
        return "Swap the position of two enemy minions, or two ally minions. [Cannot be cast during combat].";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.UNCOMMON;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        Minion firstMinion = targets.get(0).minionRef().apply(stadium);
        Minion secondMinion = targets.get(1).minionRef().apply(stadium);
        targets.get(0).minionSetRef().accept(stadium, secondMinion);
        targets.get(1).minionSetRef().accept(stadium, firstMinion);
        if (firstMinion != null) {
            firstMinion.minionInfo().entity().teleport(stadium.locOfSpot(targets.get(1)));
            firstMinion.minionInfo().setSpot(targets.get(1));
            firstMinion.setupGoals();
        }
        if (secondMinion != null) {
            secondMinion.minionInfo().entity().teleport(stadium.locOfSpot(targets.get(0)));
            secondMinion.minionInfo().setSpot(targets.get(0));
            secondMinion.setupGoals();
        }
    }

    @Override
    public List<TargetRules> targetRulesForExtraTargets() {
        return List.of(new TargetRules(true, true, false, true));
    }

    @Override
    public boolean isValidAdditionalTarget(Player p, Stadium stadium, Card card, List<Spot> targets, Spot newTarget) {
        if (stadium.allyMinionSpots(p).contains(targets.get(0))) {
            return stadium.allyMinionSpots(p).contains(newTarget);
        } else {
            return stadium.enemyMinionSpots(p).contains(newTarget);
        }
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(true, true, false, false);
    }

    @Override
    public Boolean canCastDuringCombat() {
        return false;
    }
}
