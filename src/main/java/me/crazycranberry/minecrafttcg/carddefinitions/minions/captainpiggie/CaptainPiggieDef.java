package me.crazycranberry.minecrafttcg.carddefinitions.minions.captainpiggie;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.Material;
import org.bukkit.entity.Breedable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Pig;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.Consumer;

public class CaptainPiggieDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 4;
    }

    @Override
    public String cardName() {
        return "Captain Piggie";
    }

    @Override
    public String cardDescription() {
        return "When this minion is healed, you also heal for that much. When you heal, this minion gets +1/+1.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.RARE;
    }

    @Override
    public Integer strength() {
        return 2;
    }

    @Override
    public Integer maxHealth() {
        return 5;
    }

    @Override
    public EntityType minionType() {
        return EntityType.PIG;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return CaptainPiggie.class;
    }

    @Override
    public Consumer<LivingEntity> entityAdjustment() {
        return e -> ((Pig) e).setSaddle(true);
    }
}
