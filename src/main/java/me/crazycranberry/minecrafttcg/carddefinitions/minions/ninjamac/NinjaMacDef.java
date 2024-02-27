package me.crazycranberry.minecrafttcg.carddefinitions.minions.ninjamac;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;

public class NinjaMacDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 3;
    }

    @Override
    public String cardName() {
        return "Ninja Mac";
    }

    @Override
    public String cardDescription() {
        return "";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.LEGENDARY;
    }

    @Override
    public Integer strength() {
        return 3;
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
    public boolean isFlying() {
        return true;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return NinjaMac.class;
    }

    @Override
    public String signDescription() {
        return "";
    }
}
