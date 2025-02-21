package me.crazycranberry.minecrafttcg.carddefinitions.minions.duodueler;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.Map;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardUtils.armorSet;
import static org.bukkit.Material.IRON_INGOT;

public class DuoDuelerDef implements MinionCardDefinition {
    public static final int STRENGTH_BUFF = 3;
    public static final int HEALTH_BUFF = 3;
    public static final int BLOCK_BUFF = 1;

    @Override
    public Integer cost() {
        return 5;
    }

    @Override
    public String cardName() {
        return "Duo Dueler";
    }

    @Override
    public String cardDescription() {
        return String.format("If Duo Dueler has exactly one other ally minion, give that minion and Duo Dueler +%s/+%s and %sBlock%s +%s", STRENGTH_BUFF, HEALTH_BUFF, ChatColor.BOLD, ChatColor.RESET, BLOCK_BUFF);
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.LEGENDARY;
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
        return EntityType.HUSK;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return DuoDueler.class;
    }

    @Override
    public Map<EquipmentSlot, ItemStack> equipment() {
        Map<EquipmentSlot, ItemStack> equipment = armorSet(IRON_INGOT, TrimMaterial.REDSTONE, TrimPattern.EYE);
        equipment.put(EquipmentSlot.HAND, new ItemStack(Material.IRON_SWORD));
        return equipment;
    }
}
