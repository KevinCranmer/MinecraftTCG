package me.crazycranberry.minecrafttcg.carddefinitions.minions.oliver;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;

public class OliverDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 3;
    }

    @Override
    public String cardName() {
        return "Oliver";
    }

    @Override
    public String cardDescription() {
        return "This minion gets +1 strength whenever an enemy minion enters the battlefield";
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
        return EntityType.WOLF;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return Oliver.class;
    }
}
