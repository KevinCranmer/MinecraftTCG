package me.crazycranberry.minecrafttcg.carddefinitions.minions.raidleadertonka;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

public class RaidLeaderTonkaDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 6;
    }

    @Override
    public String cardName() {
        return "Raid Leader Tonka";
    }

    @Override
    public String cardDescription() {
        return String.format("%sBlock 1%s. Give minion behind this minion +1/+1 and %sRally%s.", ChatColor.BOLD, ChatColor.RESET, ChatColor.BOLD, ChatColor.RESET);
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
        return 4;
    }

    @Override
    public EntityType minionType() {
        return EntityType.VILLAGER;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return RaidLeaderTonka.class;
    }
}
