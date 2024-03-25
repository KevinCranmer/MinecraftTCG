package me.crazycranberry.minecrafttcg.carddefinitions.cantrips;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.Player;

import java.util.List;

public class MysticSurge implements CantripCardDefinition {
    public static final int BONUS_MANA = 2;

    @Override
    public Integer cost() {
        return 0;
    }

    @Override
    public String cardName() {
        return "Mystic Surge";
    }

    @Override
    public String cardDescription() {
        return String.format("Gain %s mana (not visually tracked by the mana lamps).", BONUS_MANA);
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.UNCOMMON;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        stadium.addMana(caster, BONUS_MANA);
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
