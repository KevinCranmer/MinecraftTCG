package me.crazycranberry.minecrafttcg.carddefinitions;

import me.crazycranberry.minecrafttcg.carddefinitions.cantrips.Adrenaline;
import me.crazycranberry.minecrafttcg.carddefinitions.cantrips.Heal;
import me.crazycranberry.minecrafttcg.carddefinitions.cantrips.Protect;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.aggressivebandit.AggressiveBanditDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.dingyskeleton.DingySkeletonDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.healwitch.HealWitchDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.hungryzombie.HungryZombieDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.sewerzombie.SewerZombieDef;
import me.crazycranberry.minecrafttcg.carddefinitions.spells.DigDeeper;
import me.crazycranberry.minecrafttcg.carddefinitions.spells.ToxicSpikes;

public enum CardEnum {
    // Cantrips
    ADRENALINE(new Adrenaline()),
    HEAL(new Heal()),
    PROTECT(new Protect()),

    // Minions
    AGGRESSIVE_BANDIT(new AggressiveBanditDef()),
    DINGY_SKELETON(new DingySkeletonDef()),
    HEAL_WITCH(new HealWitchDef()),
    HUNGRY_ZOMBIE(new HungryZombieDef()),
    SEWER_ZOMBIE(new SewerZombieDef()),

    // Spells
    DIG_DEEPER(new DigDeeper()),
    TOXIC_SPIKES(new ToxicSpikes());

    final Card card;

    CardEnum(Card minionCard) {
        this.card = minionCard;
    }

    public Card card(){
        return card;
    }
}
