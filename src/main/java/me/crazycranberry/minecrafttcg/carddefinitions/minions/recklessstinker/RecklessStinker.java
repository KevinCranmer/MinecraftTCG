package me.crazycranberry.minecrafttcg.carddefinitions.minions.recklessstinker;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionWithStaticEffect;

import java.util.List;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.RECKLESS_STINKER;

public class RecklessStinker extends MinionWithStaticEffect {
    public RecklessStinker(MinionInfo minionInfo) {
        super(RECKLESS_STINKER.card(), minionInfo, RecklessStinker::getTargets, RecklessStinker::effectForTargets, RecklessStinker::removeEffect, false);
    }

    public static List<Minion> getTargets(Minion m) {
        return List.of(m);
    }

    public static void effectForTargets(Minion source, Minion target) {
        if (source.minionInfo().stadium().numCardsInHand(source.minionInfo().master()) <= 1) {
            target.setStaticStrengthBonus(source, 1);
            target.setStaticMaxHealthBonus(source, 2);
        }
    }

    public static void removeEffect(Minion source, Minion target) {
        target.setStaticStrengthBonus(source, 0);
        target.setStaticMaxHealthBonus(source, 0);
    }
}
