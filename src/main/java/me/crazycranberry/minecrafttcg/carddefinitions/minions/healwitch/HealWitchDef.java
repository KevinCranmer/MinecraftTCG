package me.crazycranberry.minecrafttcg.carddefinitions.minions.healwitch;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;

public class HealWitchDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 2;
    }

    @Override
    public String cardName() {
        return "Heal Witch";
    }

    @Override
    public String cardDescription() {
        return "At the end of each turn, heal a random Minion 1 health";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.COMMON;
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
        return EntityType.WITCH;
    }

    @Override
    public boolean isRanged() {
        return false;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return HealthWitch.class;
    }

    @Override
    public String signDescription() {
        return "Randomly\nheals 1\nally minion";
    }
}
