package me.crazycranberry.minecrafttcg.carddefinitions;

import me.crazycranberry.minecrafttcg.carddefinitions.cantrips.Protect;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.dingyskeleton.DingySkeletonDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.sewerzombie.SewerZombieDef;
import me.crazycranberry.minecrafttcg.carddefinitions.spells.ToxicSpikes;

public enum CardEnum {
    // Cantrips
    PROTECT(new Protect()),

    // Minions
    SEWER_ZOMBIE(new SewerZombieDef()),
    DINGY_SKELETON(new DingySkeletonDef()),

    // Spells
    TOXIC_SPIKES(new ToxicSpikes());

    final Card card;

    CardEnum(Card minionCard) {
        this.card = minionCard;
    }

    public Card card(){
        return card;
    }
}
