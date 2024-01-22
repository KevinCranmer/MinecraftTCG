package me.crazycranberry.minecrafttcg.carddefinitions;

import org.bukkit.NamespacedKey;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;

public interface Card {
    public static final NamespacedKey IS_CARD_KEY = new NamespacedKey(getPlugin(), "card");
    public static final NamespacedKey CARD_NAME_KEY = new NamespacedKey(getPlugin(), "cardName");
    public Integer cost();
    public String cardName();
    public String cardDescription();
}
