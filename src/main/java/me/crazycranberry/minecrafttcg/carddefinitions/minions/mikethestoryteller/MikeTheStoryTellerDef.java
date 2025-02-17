package me.crazycranberry.minecrafttcg.carddefinitions.minions.mikethestoryteller;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.THE_DUKE;

public class MikeTheStoryTellerDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 5;
    }

    @Override
    public String cardName() {
        return "Mike The StoryTeller";
    }

    @Override
    public String cardDescription() {
        return String.format("One of two Champions of %s%sThe Duke%s", ChatColor.RESET, THE_DUKE.card().rarity().color(), ChatColor.RESET);
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.RARE;
    }

    @Override
    public Integer strength() {
        return 5;
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
        return MikeTheStoryTeller.class;
    }
}
