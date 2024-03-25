package me.crazycranberry.minecrafttcg.carddefinitions;

import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.Player;

public interface SpellOrCantripCardDefinition extends Card {
    TargetRules targetRules();

    // Use this is the default targeting rules aren't enough. See SinfulSeduction for an example
    default boolean isValidInitialTarget(Player p, Stadium stadium, Card card, Spot newTarget) {
        return true;
    }
}
