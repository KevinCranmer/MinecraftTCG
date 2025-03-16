package me.crazycranberry.minecrafttcg.carddefinitions.minions.shrinkadink;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.AbstractMap;
import java.util.Map;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardUtils.armor;

public class ShrinkaDink4Def implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 4;
    }

    @Override
    public String cardName() {
        return "ShrinkaDink";
    }

    @Override
    public String cardDescription() {
        return "When this minion dies, it is resummoned with -1 strength. It can be resummoned 3 times.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.LEGENDARY;
    }

    @Override
    public Integer strength() {
        return 4;
    }

    @Override
    public Integer maxHealth() {
        return 1;
    }

    @Override
    public EntityType minionType() {
        return EntityType.ZOMBIE;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return ShrinkaDink.class;
    }

    @Override
    public Map<EquipmentSlot, ItemStack> equipment() {
        return Map.ofEntries(
            new AbstractMap.SimpleEntry<>(EquipmentSlot.HAND, new ItemStack(Material.DIAMOND_SWORD)),
            armor(EquipmentSlot.FEET, Material.GOLD_INGOT, TrimMaterial.REDSTONE, TrimPattern.EYE)
        );
    }
}
