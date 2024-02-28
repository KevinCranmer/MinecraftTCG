package me.crazycranberry.minecrafttcg.carddefinitions.minions.kevinthesmith;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.THE_DUKE;

public class KevinTheSmithDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 5;
    }

    @Override
    public String cardName() {
        return "Kevin The Smith";
    }

    @Override
    public String cardDescription() {
        return String.format("One of two Champions of %sThe Duke%s", THE_DUKE.card().rarity().color(), ChatColor.RESET);
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.RARE;
    }

    @Override
    public Integer strength() {
        return 6;
    }

    @Override
    public Integer maxHealth() {
        return 5;
    }

    @Override
    public EntityType minionType() {
        return EntityType.ZOGLIN;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return KevinTheSmith.class;
    }

    @Override
    public String signDescription() {
        return "One of the\nhog trio";
    }
}
