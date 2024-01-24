package me.crazycranberry.minecrafttcg.carddefinitions;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.sewerzombie.SewerZombieDef;

public enum CardType {
    SEWER_ZOMBIE(new SewerZombieDef());

    final Card card;

    CardType(Card minionCard) {
        this.card = minionCard;
    }

    public Card card(){
        return card;
    }
}
