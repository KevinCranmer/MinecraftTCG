package me.crazycranberry.minecrafttcg.carddefinitions.minions.ninjamac;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;

public class NinjaMacDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 4;
    }

    @Override
    public String cardName() {
        return "Ninja Mac";
    }

    @Override
    public String cardDescription() {
        return "When this minion deals damage to a player, you draw a card";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.RARE;
    }

    @Override
    public Integer strength() {
        return 3;
    }

    @Override
    public Integer maxHealth() {
        return 2;
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
}
