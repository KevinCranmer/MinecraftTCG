package me.crazycranberry.minecrafttcg.carddefinitions;

import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;

public interface Card {
    public static final NamespacedKey IS_CARD_KEY = new NamespacedKey(getPlugin(), "card");
    public static final NamespacedKey CARD_NAME_KEY = new NamespacedKey(getPlugin(), "cardName");
    public Integer cost();
    public String cardName();
    /**
     *  Use '$' for where a line break in the Signs should be, only 6 $'s allowed.
     *  \n can be used as many times as you want, as it'll only be utilized in the cards book.
     */
    public String cardDescription();
    public void onCast(Stadium stadium, Player caster);
}
