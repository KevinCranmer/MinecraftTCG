package me.crazycranberry.minecrafttcg.carddefinitions.cantrips;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class Heal implements CantripCardDefinition {
    private static final int healsFor = 3;
    @Override
    public Integer cost() {
        return 1;
    }

    @Override
    public String cardName() {
        return "Heal";
    }

    @Override
    public String cardDescription() {
        return String.format("Heals a Minion or Player for %s health", healsFor);
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.COMMON;
    }

    @Override
    public void onCast(Stadium stadium, Player caster) {
        Spot spot = stadium.playerTargetSpot(caster);
        if (spot.equals(Spot.PLAYER_1_OUTLOOK)) {
            stadium.player1().setHealth(Math.min(stadium.player1().getHealth() + healsFor, stadium.player1().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
        } else if (spot.equals(Spot.PLAYER_2_OUTLOOK)) {
            stadium.player2().setHealth(Math.min(stadium.player2().getHealth() + healsFor, stadium.player2().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
        } else {
            Minion minion = stadium.targetedMinion(caster);
            minion.onHeal(healsFor);
        }
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(true, true, true, false);
    }
}
