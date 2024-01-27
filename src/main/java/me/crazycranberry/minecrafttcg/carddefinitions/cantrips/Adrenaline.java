package me.crazycranberry.minecrafttcg.carddefinitions.cantrips;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.Player;

public class Adrenaline implements CantripCardDefinition {
    private final int bonusStrength = 2;

    @Override
    public Integer cost() {
        return 1;
    }

    @Override
    public String cardName() {
        return "Adrenaline";
    }

    @Override
    public String cardDescription() {
        return "Gives a targeted Minion +2 Strength until end of turn";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.COMMON;
    }

    @Override
    public void onCast(Stadium stadium, Player caster) {
        stadium.targetedMinion(caster).giveTemporaryStrength(bonusStrength);
    }

    @Override
    public boolean targetsMinion() {
        return true;
    }

    @Override
    public boolean targetsPlayer() {
        return false;
    }

    @Override
    public boolean targetsEmptySpots() {
        return false;
    }
}
