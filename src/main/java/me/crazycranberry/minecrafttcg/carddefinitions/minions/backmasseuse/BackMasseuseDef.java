package me.crazycranberry.minecrafttcg.carddefinitions.minions.backmasseuse;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;

public class BackMasseuseDef implements MinionCardDefinition {
    public static final int BONUS_STRENGTH = 2;
    public static final int BONUS_HEALTH = 3;

    @Override
    public Integer cost() {
        return 3;
    }

    @Override
    public String cardName() {
        return "Back Masseuse";
    }

    @Override
    public String cardDescription() {
        return String.format("Gives the ally minion in front of this minion +%s strength and +%s health", BONUS_STRENGTH, BONUS_HEALTH);
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.UNCOMMON;
    }

    @Override
    public Integer strength() {
        return 2;
    }

    @Override
    public Integer maxHealth() {
        return 2;
    }

    @Override
    public EntityType minionType() {
        return EntityType.VILLAGER;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return BackMasseuse.class;
    }

    @Override
    public String signDescription() {
        return "Buffs ally\nin front";
    }
}
