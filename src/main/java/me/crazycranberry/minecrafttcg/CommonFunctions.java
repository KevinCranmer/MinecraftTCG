package me.crazycranberry.minecrafttcg;

import java.util.List;
import java.util.Optional;

public class CommonFunctions {
    public static <T> Optional<T> randomFromList(List<T> list) {
        if (list.isEmpty()) {
            return Optional.empty();
        }
        int randomIndex = (int) (Math.random() * list.size());
        return Optional.of(list.get(randomIndex));
    }
}
