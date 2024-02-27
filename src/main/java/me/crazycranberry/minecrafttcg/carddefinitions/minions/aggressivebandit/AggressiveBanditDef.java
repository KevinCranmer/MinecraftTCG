package me.crazycranberry.minecrafttcg.carddefinitions.minions.aggressivebandit;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

public class AggressiveBanditDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 3;
    }

    @Override
    public String cardName() {
        return "Aggressive Bandit";
    }

    @Override
    public String cardDescription() {
        return String.format("%sMulti-Attack 2%s", ChatColor.BOLD, ChatColor.RESET);
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.COMMON;
    }

    @Override
    public Integer strength() {
        return 2;
    }

    @Override
    public Integer maxHealth() {
        return 2;
    }

    @Override
    public EntityType minionType() {
        return EntityType.PILLAGER;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return AggressiveBandit.class;
    }

    @Override
    public String signDescription() {
        return "Multi-Attack 2";
    }
}
