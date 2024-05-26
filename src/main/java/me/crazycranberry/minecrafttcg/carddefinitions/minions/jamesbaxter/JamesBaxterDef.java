package me.crazycranberry.minecrafttcg.carddefinitions.minions.jamesbaxter;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;

public class JamesBaxterDef implements MinionCardDefinition {
    public static final int HEAL_AMOUNT = 2;

    @Override
    public Integer cost() {
        return 5;
    }

    @Override
    public String cardName() {
        return "James Baxter";
    }

    @Override
    public String cardDescription() {
        return String.format("When this minion enters, it heals all allies %s health", HEAL_AMOUNT);
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
        return 4;
    }

    @Override
    public EntityType minionType() {
        return EntityType.HORSE;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return JamesBaxter.class;
    }
}
