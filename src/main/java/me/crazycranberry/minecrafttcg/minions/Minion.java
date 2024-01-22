package me.crazycranberry.minecrafttcg.minions;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import me.crazycranberry.minecrafttcg.carddefinitions.CardType;

public class Minion {
    private Integer strength;
    private Integer health;
    private Integer maxHealth;
    private CardType cardtype;

    public Minion(Integer strength, Integer maxHealth, CardType cardtype) {
        this.strength = strength;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.cardtype = cardtype;
    }

    public Integer strength() {
        return strength;
    }

    public Integer health() {
        return health;
    }

    public Integer maxHealth() {
        return maxHealth;
    }

    public CardType cardType() {
        return cardtype;
    }
}
