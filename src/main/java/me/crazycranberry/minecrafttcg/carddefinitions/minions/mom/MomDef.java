package me.crazycranberry.minecrafttcg.carddefinitions.minions.mom;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;

public class MomDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 4;
    }

    @Override
    public String cardName() {
        return "Mom";
    }

    @Override
    public String cardDescription() {
        return "Summons a 1/1 baby in front of it at the start of every turn.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.LEGENDARY;
    }

    @Override
    public Integer strength() {
        return 0;
    }

    @Override
    public Integer maxHealth() {
        return 3;
    }

    @Override
    public EntityType minionType() {
        return EntityType.VILLAGER;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return Mom.class;
    }

    @Override
    public String signDescription() {
        return "Summons a\n1/1 in front";
    }

    @Override
    public boolean isRanged() {
        return true;
    }
}
