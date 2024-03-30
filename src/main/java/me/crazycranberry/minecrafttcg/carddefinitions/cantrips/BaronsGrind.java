package me.crazycranberry.minecrafttcg.carddefinitions.cantrips;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.Player;

import java.util.List;

public class BaronsGrind implements CantripCardDefinition {
    @Override
    public Integer cost() {
        return 4;
    }

    @Override
    public String cardName() {
        return "Baron's Grind";
    }

    @Override
    public String cardDescription() {
        return "Draw cards equal to the number of enemy minions.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.LEGENDARY;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        for (Spot spot : stadium.enemyMinionSpots(caster)) {
            Minion minion = stadium.minionFromSpot(spot);
            if (minion != null) {
                stadium.draw(caster);
            }
        }
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(false, false, false, false, false);
    }

    @Override
    public Boolean canCastDuringCombat() {
        return true;
    }
}
