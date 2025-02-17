package me.crazycranberry.minecrafttcg.carddefinitions.minions.deathhaven;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;

public class DeathHavenDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 10;
    }

    @Override
    public String cardName() {
        return "Death Haven";
    }

    @Override
    public String cardDescription() {
        return "When this minion enters, destroy all other minions and discard your hand.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.LEGENDARY;
    }

    @Override
    public Integer strength() {
        return 9;
    }

    @Override
    public Integer maxHealth() {
        return 9;
    }

    @Override
    public EntityType minionType() {
        return EntityType.CHICKEN;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return DeathHaven.class;
    }
}
