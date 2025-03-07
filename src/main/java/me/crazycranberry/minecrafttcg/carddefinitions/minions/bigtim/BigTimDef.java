package me.crazycranberry.minecrafttcg.carddefinitions.minions.bigtim;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardUtils.armorSet;

public class BigTimDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 10;
    }

    @Override
    public String cardName() {
        return "Big Tim";
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
        return 10;
    }

    @Override
    public Integer maxHealth() {
        return 10;
    }

    @Override
    public EntityType minionType() {
        return EntityType.ZOMBIE;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return BigTim.class;
    }

    @Override
    public Map<EquipmentSlot, ItemStack> equipment() {
        return armorSet(Material.NETHERITE_INGOT, null, null);
    }
}
