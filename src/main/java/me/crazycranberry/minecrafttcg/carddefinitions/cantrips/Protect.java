package me.crazycranberry.minecrafttcg.carddefinitions.cantrips;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.Player;

import java.util.List;

public class Protect implements CantripCardDefinition {
    @Override
    public Integer cost() {
        return 2;
    }

    @Override
    public String cardName() {
        return "Protect";
    }

    @Override
    public String cardDescription() {
        return "Prevent damage to targeted Minion for the rest of turn";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.COMMON;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        stadium.targetedMinion(caster).setProtected(1);
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(true, true, false, false);
    }

    @Override
    public Boolean canCastDuringCombat() {
        return true;
    }
}
