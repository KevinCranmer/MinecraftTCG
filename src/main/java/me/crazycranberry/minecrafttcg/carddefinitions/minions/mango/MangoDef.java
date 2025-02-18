package me.crazycranberry.minecrafttcg.carddefinitions.minions.mango;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

public class MangoDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 2;
    }

    @Override
    public String cardName() {
        return "Mango";
    }

    @Override
    public String cardDescription() {
        return String.format("%sRally, Multi-Attack 2%s", ChatColor.BOLD, ChatColor.RESET);
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.COMMON;
    }

    @Override
    public Integer strength() {
        return 1;
    }

    @Override
    public Integer maxHealth() {
        return 2;
    }

    @Override
    public boolean hasRally() {
        return true;
    }

    @Override
    public EntityType minionType() {
        return EntityType.FOX;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return Mango.class;
    }
}
