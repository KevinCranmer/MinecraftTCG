package me.crazycranberry.minecrafttcg.carddefinitions.minions.shrinkadink;

import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.AbstractMap;
import java.util.Map;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardUtils.armor;

public class ShrinkaDink1Def extends ShrinkaDink4Def {
    @Override
    public Integer strength() {
        return 1;
    }

    @Override
    public Map<EquipmentSlot, ItemStack> equipment() {
        return Map.ofEntries(
            new AbstractMap.SimpleEntry<>(EquipmentSlot.HAND, new ItemStack(Material.WOODEN_SWORD)),
            armor(EquipmentSlot.FEET, Material.GOLD_INGOT, TrimMaterial.REDSTONE, TrimPattern.EYE)
        );
    }
}
