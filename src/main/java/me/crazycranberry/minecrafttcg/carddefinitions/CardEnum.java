package me.crazycranberry.minecrafttcg.carddefinitions;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.dingyskeleton.DingySkeletonDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.sewerzombie.SewerZombieDef;

public enum CardEnum {
    SEWER_ZOMBIE(new SewerZombieDef()),
    DINGY_SKELETON(new DingySkeletonDef());

    final Card card;

    CardEnum(Card minionCard) {
        this.card = minionCard;
    }

    public Card card(){
        return card;
    }
}
