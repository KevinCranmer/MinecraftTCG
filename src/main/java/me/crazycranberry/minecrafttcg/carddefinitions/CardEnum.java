package me.crazycranberry.minecrafttcg.carddefinitions;

import me.crazycranberry.minecrafttcg.carddefinitions.cantrips.Adrenaline;
import me.crazycranberry.minecrafttcg.carddefinitions.cantrips.Heal;
import me.crazycranberry.minecrafttcg.carddefinitions.cantrips.Protect;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.aggressivebandit.AggressiveBanditDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.bulldozer.BullDozerDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.dingyskeleton.DingySkeletonDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.healwitch.HealWitchDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.hungryzombie.HungryZombieDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.packleader.PackLeaderDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.sewerzombie.SewerZombieDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.unstablepyro.UnstablePyroDef;
import me.crazycranberry.minecrafttcg.carddefinitions.spells.DigDeeper;
import me.crazycranberry.minecrafttcg.carddefinitions.spells.TheVoid;
import me.crazycranberry.minecrafttcg.carddefinitions.spells.ToxicSpikes;

public enum CardEnum {
    // Cantrips
    ADRENALINE(new Adrenaline()),
    HEAL(new Heal()),
    PROTECT(new Protect()),

    // Minions
    AGGRESSIVE_BANDIT(new AggressiveBanditDef()),
    BULLDOZER(new BullDozerDef()),
    DINGY_SKELETON(new DingySkeletonDef()),
    HEAL_WITCH(new HealWitchDef()),
    HUNGRY_ZOMBIE(new HungryZombieDef()),
    PACK_LEADER(new PackLeaderDef()),
    SEWER_ZOMBIE(new SewerZombieDef()),
    UNSTABLE_PYRO(new UnstablePyroDef()),

    // Spells
    DIG_DEEPER(new DigDeeper()),
    THE_VOID(new TheVoid()),
    TOXIC_SPIKES(new ToxicSpikes());

    final Card card;

    CardEnum(Card minionCard) {
        this.card = minionCard;
    }

    public Card card(){
        return card;
    }

    public static CardEnum fromString(String s) {
        for (CardEnum cardEnum : CardEnum.values()) {
            if (cardEnum.name().equals(s)) {
                return cardEnum;
            }
        }
        return null;
    }
}
