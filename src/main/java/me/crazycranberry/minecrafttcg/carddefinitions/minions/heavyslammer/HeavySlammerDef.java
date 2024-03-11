package me.crazycranberry.minecrafttcg.carddefinitions.minions.heavyslammer;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;

public class HeavySlammerDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 7;
    }

    @Override
    public String cardName() {
        return "Heavy Slammer";
    }

    @Override
    public String cardDescription() {
        return "Deals combat damage to the minion behind its target (if there is a minion behind).";
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
        return 8;
    }

    @Override
    public EntityType minionType() {
        return EntityType.IRON_GOLEM;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return HeavySlammer.class;
    }

    @Override
    public String signDescription() {
        return "Hits Front\nand back\nminions";
    }
}
