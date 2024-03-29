package me.crazycranberry.minecrafttcg.carddefinitions.minions.scaredduelist;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ScaredDuelistDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 2;
    }

    @Override
    public String cardName() {
        return "Scared Duelist";
    }

    @Override
    public String cardDescription() {
        return String.format("%sMulti-Attack 3%s", ChatColor.BOLD, ChatColor.RESET);
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.RARE;
    }

    @Override
    public Integer strength() {
        return 0;
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
        return ScaredDuelist.class;
    }

    @Override
    public Map<EquipmentSlot, ItemStack> equipment() {
        return Map.of(
            EquipmentSlot.HAND, new ItemStack(Material.GOLDEN_SWORD)
        );
    }
}
