package me.crazycranberry.minecrafttcg.carddefinitions.minions;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_20_R3.attribute.CraftAttribute;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftLivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.crazycranberry.minecrafttcg.CommonFunctions.registerGenericAttribute;
import static me.crazycranberry.minecrafttcg.MinecraftTCG.logger;
import static org.bukkit.attribute.Attribute.GENERIC_ATTACK_DAMAGE;

public interface MinionCardDefinition extends Card {
    Integer strength();
    Integer maxHealth();
    EntityType minionType();
    default boolean isRanged() {
        return false;
    }
    default boolean isFlying() {
        return false;
    }
    Class<? extends Minion> minionClass();
    /**
     *  Use '\n' for where a line break in the Signs should be, only 5 \n's allowed.
     *  \n can be used as many times as you want, as it'll only be utilized in the cards book.
     */
    String signDescription();
    default void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        onCast(stadium, caster, targets, null);
    }
    default void onCast(Stadium stadium, Player caster, List<Spot> targets, Map<EquipmentSlot, ItemStack> equipment) {
        summonMinion(targets.get(0), stadium, caster, minionClass(), minionType(), equipment);
    }

    static void summonMinion(Spot target, Stadium stadium, Player caster, Class<? extends Minion> minionClass, EntityType minionType, Map<EquipmentSlot, ItemStack> equipment) {
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
            if (entity.getAttribute(GENERIC_ATTACK_DAMAGE) == null) {
                registerGenericAttribute(((CraftLivingEntity)entity).getHandle(), Attributes.ATTACK_DAMAGE);
            }
            minion = c.newInstance(new MinionInfo(stadium, target, entity, caster));
            target.minionSetRef().accept(stadium, minion);
            stadium.showName(target);
            minion.onEnter();
        } catch (NoSuchMethodException | NoSuchFieldException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {
            logger().severe(String.format("Unable to create a %s\nException: %s\n%s", minionClass, ex.getClass().getSimpleName(), ex.getMessage()));
        }
    }
}
