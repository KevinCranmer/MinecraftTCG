package me.crazycranberry.minecrafttcg.carddefinitions.spells;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.Player;

import java.util.List;

public class Unalive implements SpellCardDefinition {
    @Override
    public Integer cost() {
        return 4;
    }

    @Override
    public String cardName() {
        return "Unalive";
    }

    @Override
    public String cardDescription() {
        return "Destroy target minion.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.UNCOMMON;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        Minion minion = targets.get(0).minionRef().apply(stadium);
        if (minion != null) {
            minion.onDeath();
        }
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(true, true, false, false);
    }
}
