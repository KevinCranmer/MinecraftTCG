package me.crazycranberry.minecrafttcg.carddefinitions.minions.billy;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;

public class BillyDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 3;
    }

    @Override
    public String cardName() {
        return "Billy";
    }

    @Override
    public String cardDescription() {
        return "If this minion hits a front row minion, it knocks it back to the back row.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.UNCOMMON;
    }

    @Override
    public Integer strength() {
        return 2;
    }

    @Override
    public Integer maxHealth() {
        return 4;
    }

    @Override
    public EntityType minionType() {
        return EntityType.GOAT;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return Billy.class;
    }
}
