package me.crazycranberry.minecrafttcg.carddefinitions.minions;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.Material;
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

import static me.crazycranberry.minecrafttcg.MinecraftTCG.logger;

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
        Map<EquipmentSlot, ItemStack> equipment = new HashMap<>();
        if (minionType().equals(EntityType.SKELETON)) {
            equipment.put(EquipmentSlot.HAND, new ItemStack(Material.BOW));
        }
        onCast(stadium, caster, equipment);
    }
    default void onCast(Stadium stadium, Player caster, Map<EquipmentSlot, ItemStack> equipment) {
        try {
            Constructor<? extends Minion> c = minionClass().getConstructor(MinionInfo.class);
            c.setAccessible(true);
            Minion minion;
            Spot targetSpot = stadium.playerTargetSpot(caster);
            LivingEntity entity = (LivingEntity) caster.getWorld().spawnEntity(stadium.playerTargetLoc(caster), minionType(), false);
            entity.getEquipment().clear();
            for (Map.Entry<EquipmentSlot, ItemStack> entry : equipment.entrySet()) {
                entity.getEquipment().setItem(entry.getKey(), entry.getValue());
            }
            minion = c.newInstance(new MinionInfo(stadium, targetSpot, entity, caster));
            targetSpot.minionSetRef().accept(stadium, minion);
            stadium.showName(targetSpot);
            minion.onEnter();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {
            logger().severe(String.format("Unable to create a %s\nException: %s\n%s", minionClass(), ex.getClass().getSimpleName(), ex.getMessage()));
        }
    }
}
