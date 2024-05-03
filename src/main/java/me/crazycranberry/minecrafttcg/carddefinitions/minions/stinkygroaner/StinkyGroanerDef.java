package me.crazycranberry.minecrafttcg.carddefinitions.minions.stinkygroaner;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class StinkyGroanerDef implements MinionCardDefinition {
    public static final int MINUS_HEALTH = 1;
    public static final int MINUS_STRENGTH = 1;

    @Override
    public Integer cost() {
        return 5;
    }

    @Override
    public String cardName() {
        return "Stinky Groaner";
    }

    @Override
    public String cardDescription() {
        return String.format("Gives enemy minions in the same column -%s/-%s", MINUS_STRENGTH, MINUS_HEALTH);
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
        return EntityType.ZOMBIE;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return StinkyGroaner.class;
    }

    @Override
    public Map<EquipmentSlot, ItemStack> equipment() {
        return Map.of(
            EquipmentSlot.FEET, new ItemStack(Material.LEATHER_BOOTS),
            EquipmentSlot.HAND, new ItemStack(Material.WOODEN_SHOVEL),
            EquipmentSlot.OFF_HAND, new ItemStack(Material.WOODEN_PICKAXE)
        );
    }
}
