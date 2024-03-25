package me.crazycranberry.minecrafttcg.carddefinitions.minions.yousefssoulmender;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;

public class YousefsSoulMenderDef implements MinionCardDefinition {
    public static final int HEAL_AMOUNT = 2;
    @Override
    public Integer cost() {
        return 3;
    }

    @Override
    public String cardName() {
        return "Yousefs Soul Mender";
    }

    @Override
    public String cardDescription() {
        return String.format("At the end of the turn, this minion heals you for %s health.", HEAL_AMOUNT);
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
        return 3;
    }

    @Override
    public EntityType minionType() {
        return EntityType.VILLAGER;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return YousefsSoulMender.class;
    }

    @Override
    public String signDescription() {
        return "Heals its\ncontroller 2\nat end of turn";
    }
}
