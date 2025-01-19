package me.crazycranberry.minecrafttcg.carddefinitions.minions.apprenticearcher;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardUtils.armor;

public class ApprenticeArcherDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 4;
    }

    @Override
    public String cardName() {
        return "Apprentice Archer";
    }

    @Override
    public String cardDescription() {
        return "";
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
        return EntityType.SKELETON;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return ApprenticeArcher.class;
    }

    @Override
    public boolean isRanged() {
        return true;
    }

    @Override
    public Map<EquipmentSlot, ItemStack> equipment() {
        return Map.ofEntries(
            armor(EquipmentSlot.HEAD, Material.GOLD_INGOT, null, null),
            armor(EquipmentSlot.FEET, Material.GOLD_INGOT, null, null)
        );
    }
}
