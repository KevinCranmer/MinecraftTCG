package me.crazycranberry.minecrafttcg.carddefinitions.minions.comradepig;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.Material;
import org.bukkit.entity.Camel;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class ComradePigDef implements MinionCardDefinition {
    public static final int BONUS_STRENGTH = 1;
    public static final int BONUS_HEALTH = 1;

    @Override
    public Integer cost() {
        return 7;
    }

    @Override
    public String cardName() {
        return "Comrade Pig";
    }

    @Override
    public String cardDescription() {
        return String.format("Gives ally minions +%s/+%s", BONUS_STRENGTH, BONUS_HEALTH);
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.UNCOMMON;
    }

    @Override
    public Integer strength() {
        return 6;
    }

    @Override
    public Integer maxHealth() {
        return 6;
    }

    @Override
    public EntityType minionType() {
        return EntityType.PIG;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return ComradePig.class;
    }
}
