package me.crazycranberry.minecrafttcg.carddefinitions.minions.recklessstinker;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;

public class RecklessStinkerDef implements MinionCardDefinition {
    public static final int BONUS_STRENGTH = 2;
    public static final int BONUS_HEALTH = 2;

    @Override
    public Integer cost() {
        return 4;
    }

    @Override
    public String cardName() {
        return "Reckless Stinker";
    }

    @Override
    public String cardDescription() {
        return String.format("This minion has +%s strength and +%s max health when you have 1 or fewer cards in hand.", BONUS_STRENGTH, BONUS_HEALTH);
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.RARE;
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
        return EntityType.DROWNED;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return RecklessStinker.class;
    }
}
