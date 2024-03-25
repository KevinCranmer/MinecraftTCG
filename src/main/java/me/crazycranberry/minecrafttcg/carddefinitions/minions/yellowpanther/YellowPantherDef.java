package me.crazycranberry.minecrafttcg.carddefinitions.minions.yellowpanther;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;

public class YellowPantherDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 5;
    }

    @Override
    public String cardName() {
        return "Yellow Panther";
    }

    @Override
    public String cardDescription() {
        return "This minions gains strength equal to the amount of health it's missing";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.LEGENDARY;
    }

    @Override
    public Integer strength() {
        return 0;
    }

    @Override
    public Integer maxHealth() {
        return 12;
    }

    @Override
    public EntityType minionType() {
        return EntityType.OCELOT;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return YellowPanther.class;
    }

    @Override
    public String signDescription() {
        return "Strength bonus\nequal to amount\nof health missing";
    }
}
