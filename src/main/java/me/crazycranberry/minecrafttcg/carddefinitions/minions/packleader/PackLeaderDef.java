package me.crazycranberry.minecrafttcg.carddefinitions.minions.packleader;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Wolf;

import java.util.function.Consumer;

public class PackLeaderDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 3;
    }

    @Override
    public String cardName() {
        return "Pack Leader";
    }

    @Override
    public String cardDescription() {
        return "Gives adjacent minions +1 Strength";
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
        return 3;
    }

    @Override
    public EntityType minionType() {
        return EntityType.WOLF;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return PackLeader.class;
    }

    @Override
    public Consumer<LivingEntity> entityAdjustment() {
        return e -> ((Wolf) e).setVariant(Wolf.Variant.CHESTNUT);
    }
}
