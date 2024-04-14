package me.crazycranberry.minecrafttcg.carddefinitions.minions.mournfulstrix;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;

public class MournfulStrixDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 2;
    }

    @Override
    public String cardName() {
        return "Mournful Strix";
    }

    @Override
    public String cardDescription() {
        return "When this minion dies, you draw a card.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.UNCOMMON;
    }

    @Override
    public Integer strength() {
        return 1;
    }

    @Override
    public Integer maxHealth() {
        return 1;
    }

    @Override
    public EntityType minionType() {
        return EntityType.ALLAY;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return MournfulStrix.class;
    }

    @Override
    public boolean isFlying() {
        return true;
    }
}
