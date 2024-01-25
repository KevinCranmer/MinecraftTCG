package me.crazycranberry.minecrafttcg.carddefinitions;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.sewerzombie.SewerZombieDef;

public enum CardEnum {
    SEWER_ZOMBIE(new SewerZombieDef());

    final Card card;

    CardEnum(Card minionCard) {
        this.card = minionCard;
    }

    public Card card(){
        return card;
    }
}
