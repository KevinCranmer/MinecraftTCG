package me.crazycranberry.minecrafttcg.carddefinitions.cantrips;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.events.PlayerHealedEvent;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.List;

import static me.crazycranberry.minecrafttcg.carddefinitions.minions.yousefssoulmender.YousefsSoulMenderDef.HEAL_AMOUNT;

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
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        Spot spot = stadium.playerTargetSpot(caster);
        if (spot.equals(Spot.PLAYER_1_OUTLOOK)) {
            stadium.healPlayer(stadium.player1(), healsFor);
        } else if (spot.equals(Spot.PLAYER_2_OUTLOOK)) {
            stadium.healPlayer(stadium.player2(), healsFor);
        } else {
            Minion minion = stadium.targetedMinion(caster);
            minion.onHeal(healsFor);
        }
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(true, true, true, false, false);
    }

    @Override
    public Boolean canCastDuringCombat() {
        return true;
    }
}
