package me.crazycranberry.minecrafttcg.carddefinitions.minions.sewerzombie;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

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
        return "Grrrrr\nImmaZombie!";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.COMMON;
    }

    @Override
    public String signDescription() {
        return "Vanilla Ass\nZombie";
    }

    @Override
    public Integer strength() {
        return 1;
    }

    @Override
    public Integer maxHealth() {
        return 2;
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
