package me.crazycranberry.minecrafttcg.carddefinitions.minions.boneabomination;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Map;

public class BoneAbominationDef implements MinionCardDefinition {
    public static final int DEATH_DAMAGE = 2;

    @Override
    public Integer cost() {
        return 5;
    }

    @Override
    public String cardName() {
        return "Bone Abomination";
    }

    @Override
    public String cardDescription() {
        return String.format("When this minion dies, it deals %s damage to all non-%sundead%s minions", DEATH_DAMAGE, ChatColor.BOLD, ChatColor.RESET);
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
        return BoneAbomination.class;
    }

    private ItemStack greenArmorPiece(Material armor) {
        ItemStack a = new ItemStack(armor);
        LeatherArmorMeta meta = (LeatherArmorMeta) a.getItemMeta();
        meta.setColor(Color.OLIVE);
        a.setItemMeta(meta);
        return a;
    }

    @Override
    public Map<EquipmentSlot, ItemStack> equipment() {
        return Map.of(
            EquipmentSlot.HEAD, greenArmorPiece(Material.LEATHER_HELMET),
            EquipmentSlot.CHEST, greenArmorPiece(Material.LEATHER_CHESTPLATE),
            EquipmentSlot.LEGS, greenArmorPiece(Material.LEATHER_LEGGINGS),
            EquipmentSlot.FEET, greenArmorPiece(Material.LEATHER_BOOTS),
            EquipmentSlot.HAND, new ItemStack(Material.IRON_AXE)
        );
    }
}
