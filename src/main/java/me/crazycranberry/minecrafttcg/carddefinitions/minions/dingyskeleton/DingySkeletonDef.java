package me.crazycranberry.minecrafttcg.carddefinitions.minions.dingyskeleton;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

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
        return "";
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
    public Class<? extends Minion> minionClass() {
        return DingySkeleton.class;
    }

    @Override
    public String signDescription() {
        return "Shoots a weak bow";
    }
}
