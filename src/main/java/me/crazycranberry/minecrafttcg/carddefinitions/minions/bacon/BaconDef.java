package me.crazycranberry.minecrafttcg.carddefinitions.minions.bacon;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;

public class BaconDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 2;
    }

    @Override
    public String cardName() {
        return "Bacon";
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
        return 2;
    }

    @Override
    public Integer maxHealth() {
        return 3;
    }

    @Override
    public EntityType minionType() {
        return EntityType.PIG;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return Bacon.class;
    }
}
