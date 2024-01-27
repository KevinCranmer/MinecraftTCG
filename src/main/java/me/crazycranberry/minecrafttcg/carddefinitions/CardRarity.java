package me.crazycranberry.minecrafttcg.carddefinitions;

import org.bukkit.ChatColor;

public enum CardRarity {
    COMMON(3, ChatColor.GRAY),
    UNCOMMON(3, ChatColor.BLUE),
    RARE(2, ChatColor.LIGHT_PURPLE),
    LEGENDARY(1, ChatColor.DARK_RED);

    private final Integer numAllowedPerDeck;
    private final ChatColor color;

    CardRarity(Integer numAllowedPerDeck, ChatColor color) {
        this.numAllowedPerDeck = numAllowedPerDeck;
        this.color = color;
    }

    public Integer numAllowedPerDeck() {
        return numAllowedPerDeck;
    }

    public ChatColor color() {
        return color;
    }

    public String toString() {
        return this.name().charAt(0) + this.name().substring(1).toLowerCase();
    }
}
