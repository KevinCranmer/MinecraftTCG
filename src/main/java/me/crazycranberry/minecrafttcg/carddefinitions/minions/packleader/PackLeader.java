package me.crazycranberry.minecrafttcg.carddefinitions.minions.packleader;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionWithStaticEffect;
import me.crazycranberry.minecrafttcg.model.Spot;

import java.util.List;
import java.util.Objects;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.PACK_LEADER;

public class PackLeader extends MinionWithStaticEffect {
    public PackLeader(MinionInfo minionInfo) {
        super(PACK_LEADER.card(), minionInfo, PackLeader::getTargets, PackLeader::effectForTargets, PackLeader::removeEffect, true);
    }

    public static List<Minion> getTargets(Minion m) {
        List<Spot> adjacentSpots = m.minionInfo().stadium().adjacentSpots(m.minionInfo().spot());
        return adjacentSpots.stream()
            .map(Spot::minionRef)
            .filter(Objects::nonNull)
            .map(mr -> mr.apply(m.minionInfo().stadium()))
            .filter(Objects::nonNull)
            .toList();
    }

    public static void effectForTargets(Minion source, Minion target) {
        target.setStaticStrengthBonus(source, 1);
    }

    public static void removeEffect(Minion source, Minion target) {
        target.setStaticStrengthBonus(source, 0);
    }
}
