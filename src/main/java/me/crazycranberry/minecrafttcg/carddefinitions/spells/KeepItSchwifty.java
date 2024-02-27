package me.crazycranberry.minecrafttcg.carddefinitions.spells;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.Player;

import java.util.List;

public class KeepItSchwifty implements SpellCardDefinition {
    @Override
    public Integer cost() {
        return 4;
    }

    @Override
    public String cardName() {
        return "Keep It Schwifty";
    }

    @Override
    public String cardDescription() {
        return "Give all of your minions +2 strength and Overkill until end of turn.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.LEGENDARY;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        for (Spot spot : stadium.allyMinionSpots(caster)) {
            Minion minion = spot.minionRef().apply(stadium);
            if (minion != null) {
                minion.giveTemporaryStrength(2);
                minion.setTemporaryOverkill(1);
            }
        }
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(false, false, false, false);
    }
}
