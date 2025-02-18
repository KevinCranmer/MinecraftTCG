package me.crazycranberry.minecrafttcg.carddefinitions;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import me.crazycranberry.minecrafttcg.model.TurnPhase;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static me.crazycranberry.minecrafttcg.managers.StadiumManager.PLAYER_PROXY_ENTITY_TYPE;

public class CardUtils {
    public static List<Minion> minionsFromSpots(List<Spot> spots, Stadium stadium) {
        return spots.stream()
            .map(stadium::minionFromSpot)
            .filter(Objects::nonNull)
            .toList();
    }

    public static Map.Entry<EquipmentSlot, ItemStack> armor(EquipmentSlot slot, Material armorMaterial, TrimMaterial trimMaterial, TrimPattern trimPattern) {
        ItemStack item = new ItemStack(armorMaterialMap.get(armorMaterial).get(slot));
        if (trimMaterial != null && trimPattern != null) {
            ArmorTrim trim = new ArmorTrim(trimMaterial, trimPattern);
            ArmorMeta meta = (ArmorMeta) item.getItemMeta();
            meta.setTrim(trim);
            item.setItemMeta(meta);
        }
        return new AbstractMap.SimpleEntry<>(slot, item);
    }

    public static Map<EquipmentSlot, ItemStack> armorSet(Material armorMaterial, TrimMaterial trimMaterial, TrimPattern trimPattern) {
        Map<EquipmentSlot, ItemStack> armorSet = new HashMap<>(); // Let it be mutable what's the worst that could happen
        var armorPiece = armor(EquipmentSlot.HEAD, armorMaterial, trimMaterial, trimPattern);
        armorSet.put(armorPiece.getKey(), armorPiece.getValue());
        armorPiece = armor(EquipmentSlot.CHEST, armorMaterial, trimMaterial, trimPattern);
        armorSet.put(armorPiece.getKey(), armorPiece.getValue());
        armorPiece = armor(EquipmentSlot.LEGS, armorMaterial, trimMaterial, trimPattern);
        armorSet.put(armorPiece.getKey(), armorPiece.getValue());
        armorPiece = armor(EquipmentSlot.FEET, armorMaterial, trimMaterial, trimPattern);
        armorSet.put(armorPiece.getKey(), armorPiece.getValue());
        return armorSet;
    }

    public static void handleOverkillDamage(Minion damageReceiver, int damageRemaining, LivingEntity damager, boolean wasCombatAttack) {
        if (damageRemaining <= 0) {
            return;
        }
        int damageToOriginal = Math.min(damageRemaining, damageReceiver.health());
        damageRemaining = damageRemaining - damageToOriginal;
        Stadium stadium = damageReceiver.minionInfo().stadium();
        //Deal normal damage
        damageReceiver.onDamageReceived(damager, damageToOriginal, damageReceiver.isProtected());
        Optional<Minion> maybeDamagerMinion = stadium.minionFromEntity(damager);
        maybeDamagerMinion.ifPresent(minion -> minion.onDamageDealt(damageReceiver.minionInfo().entity(), damageToOriginal, wasCombatAttack, damageReceiver.isProtected()));
        //Get what's behind to do damage to them too
        LivingEntity targetBehindEntity = stadium.getEntityBehind(damageReceiver.minionInfo().spot());
        if (targetBehindEntity == null) {
            return;
        }
        Optional<Minion> targetBehind = stadium.minionFromEntity(targetBehindEntity);
        if (targetBehind.isEmpty() && targetBehindEntity.getType().equals(PLAYER_PROXY_ENTITY_TYPE)) {
            //Damage to player (a base case)
            Optional<Player> targetPlayer = stadium.getPlayerFromChicken(targetBehindEntity);
            if (targetPlayer.isEmpty() || damageRemaining <= 0) {
                return;
            }
            if (stadium.phase().equals(TurnPhase.COMBAT_PHASE)) {
                stadium.pendingDamageForPlayer(targetPlayer.get(), damageRemaining);
            } else {
                targetPlayer.get().damage(damageRemaining);
            }
            if (maybeDamagerMinion.isPresent()) {
                maybeDamagerMinion.get().onDamageDealt(targetPlayer.get(), damageRemaining, wasCombatAttack, false);
            }
            targetBehindEntity.damage(0);
        } else if (targetBehind.isPresent() && !targetBehind.get().isProtected()) {
            //Damage to minion (might have excess damage once again)
            handleOverkillDamage(targetBehind.get(), damageRemaining, damager, wasCombatAttack);
        }
    }

    public static void dealDamage(LivingEntity target, LivingEntity source, Stadium stadium, int damage) {
        if (target instanceof Player) {
            target.damage(3);
        } else {
            stadium.minionFromEntity(target).ifPresent(m -> m.onDamageReceived(source, damage, m.isProtected()));
        }
    }

    public static void swapTwoSpots(Stadium stadium, Spot spot1, Spot spot2) {
        Minion firstMinion = stadium.minionFromSpot(spot1);
        Minion secondMinion = stadium.minionFromSpot(spot2);
        stadium.setMinionAtSpot(spot1, secondMinion, false);
        stadium.setMinionAtSpot(spot2, firstMinion, false);
        if (firstMinion != null) {
            firstMinion.minionInfo().entity().teleport(stadium.locOfSpot(spot2));
            firstMinion.minionInfo().setSpot(spot2);
            firstMinion.setupGoals();
        }
        if (secondMinion != null) {
            secondMinion.minionInfo().entity().teleport(stadium.locOfSpot(spot1));
            secondMinion.minionInfo().setSpot(spot1);
            secondMinion.setupGoals();
        }
    }

    public static final List<EntityType> ANIMAL_TYPES = List.of(
        EntityType.ARMADILLO,
        EntityType.AXOLOTL,
        EntityType.BEE,
        EntityType.CAMEL,
        EntityType.CAT,
        EntityType.CHICKEN,
        EntityType.DONKEY,
        EntityType.FOX,
        EntityType.FROG,
        EntityType.GOAT,
        EntityType.HORSE,
        EntityType.LLAMA,
        EntityType.MOOSHROOM,
        EntityType.MULE,
        EntityType.OCELOT,
        EntityType.PANDA,
        EntityType.PIG,
        EntityType.RABBIT,
        EntityType.SHEEP,
        EntityType.TRADER_LLAMA,
        EntityType.TURTLE,
        EntityType.WOLF,
        EntityType.BEE,
        EntityType.DOLPHIN,
        EntityType.POLAR_BEAR
    );

    public static final List<EntityType> UNDEAD_TYPES = List.of(
        EntityType.SKELETON,
        EntityType.SKELETON_HORSE,
        EntityType.ZOMBIE,
        EntityType.ZOMBIE_HORSE,
        EntityType.ZOMBIE_VILLAGER,
        EntityType.ZOMBIFIED_PIGLIN,
        EntityType.WITHER_SKELETON,
        EntityType.STRAY,
        EntityType.HUSK,
        EntityType.PHANTOM,
        EntityType.DROWNED,
        EntityType.ZOGLIN,
        EntityType.WITHER
    );

    public static final List<Spot> FRONT_ROW_SPOTS = List.of(
        Spot.RED_1_FRONT,
        Spot.RED_2_FRONT,
        Spot.BLUE_1_FRONT,
        Spot.BLUE_2_FRONT,
        Spot.GREEN_1_FRONT,
        Spot.GREEN_2_FRONT
    );

    public static final List<Spot> BACK_ROW_SPOTS = List.of(
        Spot.RED_1_BACK,
        Spot.RED_2_BACK,
        Spot.BLUE_1_BACK,
        Spot.BLUE_2_BACK,
        Spot.GREEN_1_BACK,
        Spot.GREEN_2_BACK
    );

    private static final Map<EquipmentSlot, Material> netheriteArmor = Map.of(
        EquipmentSlot.HEAD, Material.NETHERITE_HELMET,
        EquipmentSlot.CHEST, Material.NETHERITE_CHESTPLATE,
        EquipmentSlot.LEGS, Material.NETHERITE_LEGGINGS,
        EquipmentSlot.FEET, Material.NETHERITE_BOOTS
    );

    private static final Map<EquipmentSlot, Material> goldArmor = Map.of(
        EquipmentSlot.HEAD, Material.GOLDEN_HELMET,
        EquipmentSlot.CHEST, Material.GOLDEN_CHESTPLATE,
        EquipmentSlot.LEGS, Material.GOLDEN_LEGGINGS,
        EquipmentSlot.FEET, Material.GOLDEN_BOOTS
    );

    private static final Map<EquipmentSlot, Material> diamondArmor = Map.of(
        EquipmentSlot.HEAD, Material.DIAMOND_HELMET,
        EquipmentSlot.CHEST, Material.DIAMOND_CHESTPLATE,
        EquipmentSlot.LEGS, Material.DIAMOND_LEGGINGS,
        EquipmentSlot.FEET, Material.DIAMOND_BOOTS
    );

    private static final Map<EquipmentSlot, Material> ironArmor = Map.of(
        EquipmentSlot.HEAD, Material.IRON_HELMET,
        EquipmentSlot.CHEST, Material.IRON_CHESTPLATE,
        EquipmentSlot.LEGS, Material.IRON_LEGGINGS,
        EquipmentSlot.FEET, Material.IRON_BOOTS
    );

    private static final Map<EquipmentSlot, Material> leatherArmor = Map.of(
        EquipmentSlot.HEAD, Material.LEATHER_HELMET,
        EquipmentSlot.CHEST, Material.LEATHER_CHESTPLATE,
        EquipmentSlot.LEGS, Material.LEATHER_LEGGINGS,
        EquipmentSlot.FEET, Material.LEATHER_BOOTS
    );

    private static final Map<Material, Map<EquipmentSlot, Material>> armorMaterialMap = Map.of(
        Material.NETHERITE_INGOT, netheriteArmor,
        Material.DIAMOND, diamondArmor,
        Material.GOLD_INGOT, goldArmor,
        Material.IRON_INGOT, ironArmor,
        Material.LEATHER, leatherArmor
    );
}
