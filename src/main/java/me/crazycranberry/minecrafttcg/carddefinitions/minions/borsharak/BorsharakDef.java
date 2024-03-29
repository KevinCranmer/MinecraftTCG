package me.crazycranberry.minecrafttcg.carddefinitions.minions.borsharak;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;

public class BorsharakDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 4;
    }

    @Override
    public String cardName() {
        return "Borsharak";
    }

    @Override
    public String cardDescription() {
        return "When this minion deals damage, it also deals that much damage to minions that are adjacent to its target";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.LEGENDARY;
    }

    @Override
    public Integer strength() {
        return 4;
    }

    @Override
    public Integer maxHealth() {
        return 3;
    }

    @Override
    public EntityType minionType() {
        return EntityType.WITHER_SKELETON;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return Borsharak.class;
    }
}
