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
    public String cardDescription();
    public CardRarity rarity();
    public void onCast(Stadium stadium, Player caster);
}
