package me.crazycranberry.minecrafttcg.carddefinitions;

import org.bukkit.NamespacedKey;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;

public interface Card {
    public static final NamespacedKey IS_CARD_KEY = new NamespacedKey(getPlugin(), "card");
    public static final NamespacedKey CARD_NAME_KEY = new NamespacedKey(getPlugin(), "cardName");
    public Integer cost();
    public String cardName();
    /**
     *  Use '$' for where a line break in the Signs should be, only 6 $'s allowed.
     *  \n can be used as many times as you want, as it'll only be utilized in the spells book.
     */
    public String cardDescription();
}
