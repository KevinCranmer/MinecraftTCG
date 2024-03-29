package me.crazycranberry.minecrafttcg.carddefinitions.minions.unstablepyro;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;

public class UnstablePyroDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 3;
    }

    @Override
    public String cardName() {
        return "Unstable Pyro";
    }

    @Override
    public String cardDescription() {
        return "When this minion spawns and dies, it deals 1 damage to a random enemy.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.UNCOMMON;
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
        return EntityType.ZOMBIE;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return UnstablePyro.class;
    }
}
