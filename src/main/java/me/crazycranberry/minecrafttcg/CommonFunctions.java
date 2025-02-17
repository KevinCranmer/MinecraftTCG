package me.crazycranberry.minecrafttcg;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.bukkit.ChatColor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CommonFunctions {
    public static final int TICKS_PER_SECOND = 20;
    public static <T> Optional<T> randomFromList(List<T> list) {
        if (list.isEmpty()) {
            return Optional.empty();
        }
        int randomIndex = (int) (Math.random() * list.size());
        return Optional.of(list.get(randomIndex));
    }

    public static List<String> textToLines(String text, int maxCharsPerLine, ChatColor color) {
        List<String> lines = new ArrayList<>();
        while(true) {
            if (text.length() < maxCharsPerLine) {
                if (!text.isEmpty()) {
                    String line = String.format("%s%s", color == null ? "" : color, text);
                    lines.add(line.replaceAll(ChatColor.RESET + "([a-zA-Z,\\.\\s;])", ChatColor.RESET.toString() + color + "$1"));
                }
                break;
            }
            String laterChunk = text.substring(maxCharsPerLine - 1);
            text = text.replace(laterChunk, laterChunk.replaceFirst(" ", "\n"));
            String[] pieces = text.split("\n");
            lines.add(String.format("%s%s", color == null ? "" : color, pieces[0]).replaceAll(ChatColor.RESET + "([a-zA-Z,\\.\\s;])", ChatColor.RESET.toString() + color + "$1"));
            text = pieces.length > 1 ? pieces[1] : "";
        }
        return lines;
    }

    public static String nthSuffix(int i) {
        return i == 0 ? "1st" : i == 1 ? "2nd" : i == 2 ? "3rd" : i + "th";
    }

    public static void registerGenericAttribute(LivingEntity entity, Holder<Attribute> attribute) throws IllegalAccessException, NoSuchFieldException {
        AttributeMap attributeMapBase = entity.getAttributes();
        Field f = AttributeMap.class.getDeclaredField("b"); // b instead of "attributes" because we have to remap back to obfuscated before publishing a jar
        f.setAccessible(true);
        Map<Holder<Attribute>, AttributeInstance> map = (Map<Holder<Attribute>, AttributeInstance>) f.get(attributeMapBase);
        AttributeInstance instance = new AttributeInstance(attribute, AttributeInstance::getAttribute);
        map.put(attribute, instance);
    }
}
