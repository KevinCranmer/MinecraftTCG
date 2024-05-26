package me.crazycranberry.minecrafttcg.carddefinitions.minions.flamejuggler;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class FlameJugglerDef implements MinionCardDefinition {
    public static final int DAMAGE = 1;

    @Override
    public Integer cost() {
        return 3;
    }

    @Override
    public String cardName() {
        return "Flame Juggler";
    }

    @Override
    public String cardDescription() {
        return String.format("Whenever an ally enters, Flame Juggler deals %s damage to a random enemy minion", DAMAGE);
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.UNCOMMON;
    }

    @Override
    public Integer strength() {
        return 2;
    }

    @Override
    public Integer maxHealth() {
        return 3;
    }

    @Override
    public EntityType minionType() {
        return EntityType.ZOMBIFIED_PIGLIN;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return FlameJuggler.class;
    }

    @Override
    public Map<EquipmentSlot, ItemStack> equipment() {
        return Map.of(
            EquipmentSlot.HAND, new ItemStack(Material.FIRE_CHARGE),
            EquipmentSlot.OFF_HAND, new ItemStack(Material.FIREWORK_ROCKET)
        );
    }
}
