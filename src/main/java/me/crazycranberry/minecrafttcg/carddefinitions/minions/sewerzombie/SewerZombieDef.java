package me.crazycranberry.minecrafttcg.carddefinitions.minions.sewerzombie;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;

public class SewerZombieDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 1;
    }

    @Override
    public String cardName() {
        return "Sewer Zombie";
    }

    @Override
    public String cardDescription() {
        return "";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.COMMON;
    }

    @Override
    public String signDescription() {
        return "";
    }

    @Override
    public Integer strength() {
        return 20;
    }

    @Override
    public Integer maxHealth() {
        return 3;
    }

    @Override
    public EntityType minionType() {
        return EntityType.ZOMBIE;
    }

    @Override
    public boolean isRanged() {
        return false;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return SewerZombie.class;
    }
}
