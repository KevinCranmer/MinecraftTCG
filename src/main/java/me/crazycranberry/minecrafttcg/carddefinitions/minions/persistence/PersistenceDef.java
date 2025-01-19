package me.crazycranberry.minecrafttcg.carddefinitions.minions.persistence;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.Map;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardUtils.armor;
import static me.crazycranberry.minecrafttcg.carddefinitions.CardUtils.armorSet;

public class PersistenceDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 3;
    }

    @Override
    public String cardName() {
        return "Persistence";
    }

    @Override
    public String cardDescription() {
        return "This minion resurrects itself when it dies during combat.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.LEGENDARY;
    }

    @Override
    public Integer strength() {
        return 1;
    }

    @Override
    public Integer maxHealth() {
        return 1;
    }

    @Override
    public EntityType minionType() {
        return EntityType.SKELETON;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return Persistence.class;
    }

    @Override
    public Map<EquipmentSlot, ItemStack> equipment() {
        return armorSet(Material.LEATHER, TrimMaterial.NETHERITE, TrimPattern.BOLT);
    }
}
