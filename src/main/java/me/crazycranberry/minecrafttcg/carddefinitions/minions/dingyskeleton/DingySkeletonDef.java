package me.crazycranberry.minecrafttcg.carddefinitions.minions.dingyskeleton;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

public class DingySkeletonDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 2;
    }

    @Override
    public String cardName() {
        return "Dingy Skeleton";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.COMMON;
    }

    @Override
    public String cardDescription() {
        return String.format("%sRally%s", ChatColor.BOLD, ChatColor.RESET);
    }

    @Override
    public Integer strength() {
        return 2;
    }

    @Override
    public Integer maxHealth() {
        return 1;
    }

    @Override
    public EntityType minionType() {
        return EntityType.SKELETON;
    }

    @Override
    public boolean isRanged() {
        return true;
    }

    @Override
    public boolean hasRally() {
        return true;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return DingySkeleton.class;
    }
}
