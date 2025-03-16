package me.crazycranberry.minecrafttcg.carddefinitions.minions.packleader;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionWithStaticEffect;
import me.crazycranberry.minecrafttcg.model.Spot;

import java.util.List;
import java.util.Objects;

public class PackLeader extends MinionWithStaticEffect {
    public PackLeader(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo, PackLeader::getTargets, PackLeader::effectForTargets, PackLeader::removeEffect, true);
    }

    public static List<Minion> getTargets(Minion m) {
        List<Spot> adjacentSpots = m.minionInfo().stadium().adjacentSpots(m.minionInfo().spot());
        return adjacentSpots.stream()
            .map(s -> m.minionInfo().stadium().minionFromSpot(s))
            .filter(Objects::nonNull)
            .toList();
    }

    public static void effectForTargets(Minion source, Minion target) {
        target.setStaticStrengthBonus(source, 1);
    }

    public static void removeEffect(Minion source, Minion target) {
        target.setStaticStrengthBonus(source, 0);
    }

    @Override
    public String signDescription() {
        return "Gives adjacent\nminions +1\nStrength";
    }
}
