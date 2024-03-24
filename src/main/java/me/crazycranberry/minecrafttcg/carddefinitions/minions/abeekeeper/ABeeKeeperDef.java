package me.crazycranberry.minecrafttcg.carddefinitions.minions.abeekeeper;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;

public class ABeeKeeperDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 4;
    }

    @Override
    public String cardName() {
        return "A Bee Keeper";
    }

    @Override
    public String cardDescription() {
        return "When destroyed, the keeper will spawn A Bee.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.UNCOMMON;
    }

    @Override
    public Integer strength() {
        return 0;
    }

    @Override
    public Integer maxHealth() {
        return 6;
    }

    @Override
    public EntityType minionType() {
        return EntityType.VILLAGER;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return ABeeKeeper.class;
    }

    @Override
    public String signDescription() {
        return "On death \nSummon A Bee";
    }
}
