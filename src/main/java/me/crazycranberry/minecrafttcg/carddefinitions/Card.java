package me.crazycranberry.minecrafttcg.carddefinitions;

import me.crazycranberry.minecrafttcg.carddefinitions.cantrips.CantripCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.spells.SpellCardDefinition;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.List;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;

public interface Card {
    NamespacedKey IS_CARD_KEY = new NamespacedKey(getPlugin(), "card");
    NamespacedKey CARD_NAME_KEY = new NamespacedKey(getPlugin(), "cardName");
    NamespacedKey CARD_COST_KEY = new NamespacedKey(getPlugin(), "cardCost");
    NamespacedKey RANDOM_UUID_KEY = new NamespacedKey(getPlugin(), "random"); //Makes the books not stackable
    Integer cost();
    String cardName();
    String cardDescription();
    CardRarity rarity();
    void onCast(Stadium stadium, Player caster, List<Spot> targets);

    default void onCast(Stadium stadium, Player caster, List<Spot> targets, MinionCardDefinition cardBeingCast) {
        onCast(stadium, caster, targets);
    }

    default void onCast(Stadium stadium, Player caster, List<Spot> targets, CantripCardDefinition cardBeingCast) {
        onCast(stadium, caster, targets);
    }

    default void onCast(Stadium stadium, Player caster, List<Spot> targets, SpellCardDefinition cardBeingCast) {
        onCast(stadium, caster, targets);
    }
}
