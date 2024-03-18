package me.crazycranberry.minecrafttcg.carddefinitions.minions.apprenticearcher;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;

public class ApprenticeArcherDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 4;
    }

    @Override
    public String cardName() {
        return "Apprentice Archer";
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
        return 3;
    }

    @Override
    public EntityType minionType() {
        return EntityType.SKELETON;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return ApprenticeArcher.class;
    }

    @Override
    public String signDescription() {
        return "";
    }

    @Override
    public boolean isRanged() {
        return true;
    }
}
