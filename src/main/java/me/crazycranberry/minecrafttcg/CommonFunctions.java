package me.crazycranberry.minecrafttcg;

import java.util.List;
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

    public static String nthSuffix(int i) {
        return i == 0 ? "1st" : i == 1 ? "2nd" : i == 2 ? "3rd" : i + "th";
    }
}
