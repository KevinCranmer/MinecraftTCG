package me.crazycranberry.minecrafttcg;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.attribute.CraftAttributeMap;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;

public class CommonFunctions {
    public static final int TICKS_PER_SECOND = 20;
    public static <T> Optional<T> randomFromList(List<T> list) {
        if (list.isEmpty()) {
            return Optional.empty();
        }
        int randomIndex = (int) (Math.random() * list.size());
        return Optional.of(list.get(randomIndex));
    }

    public static String nthSuffix(int i) {
        return i == 0 ? "1st" : i == 1 ? "2nd" : i == 2 ? "3rd" : i + "th";
    }

    public static void registerGenericAttribute(LivingEntity entity, Attribute attribute) throws IllegalAccessException, NoSuchFieldException {
        AttributeMap attributeMapBase = entity.getAttributes();
        Field f = AttributeMap.class.getDeclaredField("b"); // b instead of "attributes" because we have to remap back to obfuscated before publishing a jar
        f.setAccessible(true);
        Map<Attribute, AttributeInstance> map = (Map<Attribute, AttributeInstance>) f.get(attributeMapBase);
        AttributeInstance instance = new AttributeInstance(attribute, AttributeInstance::getAttribute);
        map.put(attribute, instance);
    }
}
