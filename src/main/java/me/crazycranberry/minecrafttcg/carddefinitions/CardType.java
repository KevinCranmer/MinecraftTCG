package me.crazycranberry.minecrafttcg.carddefinitions;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.SewerZombie;

public enum CardType {
    SEWER_ZOMBIE(new SewerZombie());

    final Card card;

    CardType(Card minionCard) {
        this.card = minionCard;
    }

    public Card card(){
        return card;
    }
}
