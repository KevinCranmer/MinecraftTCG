package me.crazycranberry.minecrafttcg.carddefinitions.minions.stinkygroaner;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionWithStaticEffect;
import me.crazycranberry.minecrafttcg.model.Stadium;

import java.util.List;
import java.util.Objects;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.STINKY_GROANER;
import static me.crazycranberry.minecrafttcg.carddefinitions.minions.stinkygroaner.StinkyGroanerDef.MINUS_HEALTH;
import static me.crazycranberry.minecrafttcg.carddefinitions.minions.stinkygroaner.StinkyGroanerDef.MINUS_STRENGTH;

public class StinkyGroaner extends MinionWithStaticEffect {
    public StinkyGroaner(MinionInfo minionInfo) {
        super(STINKY_GROANER.card(), minionInfo, StinkyGroaner::getTargets, StinkyGroaner::effectForTargets, StinkyGroaner::removeEffect, true);
    }

    public static List<Minion> getTargets(Minion groaner) {
        Stadium stadium = groaner.minionInfo().stadium();
        return stadium.enemyMinionSpots(groaner.minionInfo().master())
            .stream()
            .filter(s -> s.column().equals(groaner.minionInfo().spot().column()))
            .map(stadium::minionFromSpot)
            .filter(Objects::nonNull)
            .toList();
    }

    public static void effectForTargets(Minion source, Minion target) {
        target.setStaticStrengthBonus(source, -MINUS_STRENGTH);
        target.setStaticMaxHealthBonus(source, -MINUS_HEALTH);
    }

    public static void removeEffect(Minion source, Minion target) {
        target.setStaticStrengthBonus(source, 0);
        target.setStaticMaxHealthBonus(source, 0);
    }

    @Override
    public String signDescription() {
        return String.format("Gives enemy minions\nin same lane -%s/-%s", MINUS_STRENGTH, MINUS_HEALTH);
    }
}
