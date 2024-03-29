package me.crazycranberry.minecrafttcg.carddefinitions.minions.theknapper;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;

public class TheKnapperDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 3;
    }

    @Override
    public String cardName() {
        return "The Knapper";
    }

    @Override
    public String cardDescription() {
        return "At the start of combat, swap strength with the enemy minion in front of this minion if it has higher strength";
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
        return EntityType.PILLAGER;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return TheKnapper.class;
    }
}
