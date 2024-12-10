package me.crazycranberry.minecrafttcg.carddefinitions.minions;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftLivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hoglin;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static me.crazycranberry.minecrafttcg.CommonFunctions.registerGenericAttribute;
import static me.crazycranberry.minecrafttcg.MinecraftTCG.logger;
import static org.bukkit.attribute.Attribute.ATTACK_DAMAGE;

public interface MinionCardDefinition extends Card {
    Integer strength();
    Integer maxHealth();
    EntityType minionType();
    default Consumer<LivingEntity> entityAdjustment() {return e -> {};}
    default boolean isRanged() {
        return false;
    }
    default boolean isFlying() {
        return false;
    }
    default boolean hasRally() {
        return false;
    }
    default boolean hasRush() {
        return false;
    }
    default Map<EquipmentSlot, ItemStack> equipment() {
        return null;
    }
    Class<? extends Minion> minionClass();
    default void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        onCast(stadium, caster, targets, equipment());
    }
    default void onCast(Stadium stadium, Player caster, List<Spot> targets, Map<EquipmentSlot, ItemStack> equipment) {
        summonMinion(targets.get(0), stadium, caster, minionClass(), this);
    }

    static Minion summonMinion(Spot target, Stadium stadium, Player caster, Class<? extends Minion> minionClass, MinionCardDefinition minionDefClass) {
        return summonMinion(target, stadium, caster, minionClass, minionDefClass.minionType(), minionDefClass.equipment(), minionDefClass.entityAdjustment(), true);
    }

    static Minion summonMinion(Spot target, Stadium stadium, Player caster, Class<? extends Minion> minionClass, EntityType minionType, Map<EquipmentSlot, ItemStack> equipment, Consumer<LivingEntity> entityAdjustment, boolean triggerOnEnter) {
        if (stadium.minionFromSpot(target) != null) {
            caster.sendMessage(String.format("%sA minion tried to be summoned on a spot that already has a minion. The new minion was not summoned.%s", ChatColor.GRAY, ChatColor.RESET));
            return null;
        }
        try {
            if (equipment == null) {
                equipment = new HashMap<>();
                if (minionType.equals(EntityType.SKELETON)) {
                    equipment.put(EquipmentSlot.HAND, new ItemStack(Material.BOW));
                }
            }
            Constructor<? extends Minion> c = minionClass.getConstructor(MinionInfo.class);
            c.setAccessible(true);
            Minion minion;
            LivingEntity entity = (LivingEntity) caster.getWorld().spawnEntity(stadium.locOfSpot(target), minionType, false);
            entity.getEquipment().clear();
            for (Map.Entry<EquipmentSlot, ItemStack> entry : equipment.entrySet()) {
                entity.getEquipment().setItem(entry.getKey(), entry.getValue());
            }
            if (entity.getAttribute(ATTACK_DAMAGE) == null) {
                registerGenericAttribute(((CraftLivingEntity)entity).getHandle(), Attributes.ATTACK_DAMAGE.value());
            }
            if (entity instanceof Piglin piglin) piglin.setImmuneToZombification(true);
            if (entity instanceof Hoglin hoglin) hoglin.setImmuneToZombification(true);
            entityAdjustment.accept(entity);
            minion = c.newInstance(new MinionInfo(stadium, target, entity, caster));
            stadium.setMinionAtSpot(target, minion, false);
            stadium.showName(target);
            if (triggerOnEnter) {
                minion.onEnter();
            }
            return minion;
        } catch (NoSuchMethodException | NoSuchFieldException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {
            logger().severe(String.format("Unable to create a %s\nException: %s\n%s", minionClass, ex.getClass().getSimpleName(), ex.getMessage()));
            return null;
        }
    }
}
