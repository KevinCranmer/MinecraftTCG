package me.crazycranberry.minecrafttcg.carddefinitions.minions.chaoticskeleton;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.Map;

public class ChaoticSkeletonDef implements MinionCardDefinition {
    private static final ArmorTrim trim = new ArmorTrim(TrimMaterial.NETHERITE, TrimPattern.SHAPER);

    @Override
    public Integer cost() {
        return 3;
    }

    @Override
    public String cardName() {
        return "Chaotic Skeleton";
    }

    @Override
    public String cardDescription() {
        return "At the beginning of each turn, this minion randomly swaps positions with an ally minion or an empty spot";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.RARE;
    }

    @Override
    public Integer strength() {
        return 5;
    }

    @Override
    public Integer maxHealth() {
        return 4;
    }

    @Override
    public EntityType minionType() {
        return EntityType.SKELETON;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return ChaoticSkeleton.class;
    }

    @Override
    public Map<EquipmentSlot, ItemStack> equipment() {
        ItemStack helm = new ItemStack(Material.GOLDEN_HELMET);
        ArmorMeta helmArmor = (ArmorMeta) helm.getItemMeta();
        helmArmor.setTrim(trim);
        helm.setItemMeta(helmArmor);
        ItemStack pants = new ItemStack(Material.GOLDEN_HELMET);
        ArmorMeta pantsArmor = (ArmorMeta) helm.getItemMeta();
        pantsArmor.setTrim(trim);
        helm.setItemMeta(pantsArmor);
        return Map.of(
            EquipmentSlot.HEAD, helm,
            EquipmentSlot.LEGS, pants,
            EquipmentSlot.HAND, new ItemStack(Material.WOODEN_AXE)
        );
    }
}
