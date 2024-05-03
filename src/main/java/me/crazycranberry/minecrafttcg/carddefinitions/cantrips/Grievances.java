package me.crazycranberry.minecrafttcg.carddefinitions.cantrips;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.Player;

import java.util.List;

public class Grievances implements CantripCardDefinition {
    @Override
    public Integer cost() {
        return 3;
    }

    @Override
    public String cardName() {
        return "Grievances";
    }

    @Override
    public String cardDescription() {
        return "Draw a number of cards equal to the number of minions that died this turn.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.RARE;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        int numDraws = stadium.graveyard().get(stadium.turn()).size();
        for (int i = 0; i < numDraws; i++) {
            stadium.draw(caster);
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
