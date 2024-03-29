package me.crazycranberry.minecrafttcg.carddefinitions.minions.hungryzombie;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class HungryZombieDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 4;
    }

    @Override
    public String cardName() {
        return "Hungry Zombie";
    }

    @Override
    public String cardDescription() {
        return "";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.COMMON;
    }

    @Override
    public Integer strength() {
        return 3;
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
        return HungryZombie.class;
    }

    @Override
    public Map<EquipmentSlot, ItemStack> equipment() {
        return Map.of(
            EquipmentSlot.CHEST, new ItemStack(Material.LEATHER_CHESTPLATE)
        );
    }
}
