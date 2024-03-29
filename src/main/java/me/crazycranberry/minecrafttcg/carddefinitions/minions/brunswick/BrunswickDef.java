package me.crazycranberry.minecrafttcg.carddefinitions.minions.brunswick;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;

public class BrunswickDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 4;
    }

    @Override
    public String cardName() {
        return "Brunswick";
    }

    @Override
    public String cardDescription() {
        return "";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.UNCOMMON;
    }

    @Override
    public Integer strength() {
        return 4;
    }

    @Override
    public Integer maxHealth() {
        return 5;
    }

    @Override
    public EntityType minionType() {
        return EntityType.POLAR_BEAR;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return Brunswick.class;
    }
}
