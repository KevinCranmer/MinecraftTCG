package me.crazycranberry.minecrafttcg.carddefinitions.minions.comradepig;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionWithStaticEffect;
import me.crazycranberry.minecrafttcg.model.Stadium;

import java.util.List;
import java.util.Objects;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.COMRADE_PIG;
import static me.crazycranberry.minecrafttcg.carddefinitions.minions.comradepig.ComradePigDef.BONUS_HEALTH;
import static me.crazycranberry.minecrafttcg.carddefinitions.minions.comradepig.ComradePigDef.BONUS_STRENGTH;

public class ComradePig extends MinionWithStaticEffect {
    public ComradePig(MinionInfo minionInfo) {
        super(COMRADE_PIG.card(), minionInfo, ComradePig::getTargets, ComradePig::effectForTargets, ComradePig::removeEffect, true);
    }

    public static List<Minion> getTargets(Minion pig) {
        Stadium stadium = pig.minionInfo().stadium();
        return stadium.allyMinionSpots(pig.minionInfo().master())
            .stream()
            .map(stadium::minionFromSpot)
            .filter(Objects::nonNull)
            .filter(m -> !m.equals(pig))
            .toList();
    }

    public static void effectForTargets(Minion source, Minion target) {
        target.setStaticStrengthBonus(source, BONUS_STRENGTH);
        target.setStaticMaxHealthBonus(source, BONUS_HEALTH);
    }

    public static void removeEffect(Minion source, Minion target) {
        target.setStaticStrengthBonus(source, 0);
        target.setStaticMaxHealthBonus(source, 0);
    }

    @Override
    public String signDescription() {
        return String.format("Gives allies\n+%s/+%s", BONUS_STRENGTH, BONUS_HEALTH);
    }
}
