package me.crazycranberry.minecrafttcg.carddefinitions.minions.recklessstinker;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionWithStaticEffect;

import java.util.List;

import static me.crazycranberry.minecrafttcg.carddefinitions.minions.recklessstinker.RecklessStinkerDef.BONUS_HEALTH;
import static me.crazycranberry.minecrafttcg.carddefinitions.minions.recklessstinker.RecklessStinkerDef.BONUS_STRENGTH;

public class RecklessStinker extends MinionWithStaticEffect {
    public RecklessStinker(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo, RecklessStinker::getTargets, RecklessStinker::effectForTargets, RecklessStinker::removeEffect, false);
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

    @Override
    public String signDescription() {
        return "+" + BONUS_STRENGTH + "/+" + BONUS_HEALTH + " if\ncontroller has\n1 or fewer\ncards in hand";
    }
}
