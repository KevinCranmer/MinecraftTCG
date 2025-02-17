package me.crazycranberry.minecrafttcg.carddefinitions.minions.theduke;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.KEVIN_THE_SMITH;
import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.MIKE_THE_STORY_TELLER;

public class TheDukeDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 6;
    }

    @Override
    public String cardName() {
        return "The Duke";
    }

    @Override
    public String cardDescription() {
        return String.format("%sPacifist%s. If you control %s%sMike The Storyteller%s and " +
            "%s%sKevin The Smith%s at the end of your turn, deal 1000 damage to your opponent.",
            ChatColor.BOLD, ChatColor.RESET, ChatColor.RESET, MIKE_THE_STORY_TELLER.card().rarity().color(),
            ChatColor.RESET, ChatColor.RESET, KEVIN_THE_SMITH.card().rarity().color(), ChatColor.RESET);
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.LEGENDARY;
    }

    @Override
    public Integer strength() {
        return 1;
    }

    @Override
    public Integer maxHealth() {
        return 8;
    }

    @Override
    public EntityType minionType() {
        return EntityType.ZOGLIN;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return TheDuke.class;
    }
}
