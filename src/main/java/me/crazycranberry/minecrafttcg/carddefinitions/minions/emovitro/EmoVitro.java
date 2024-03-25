package me.crazycranberry.minecrafttcg.carddefinitions.minions.emovitro;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionWithStaticEffect;
import me.crazycranberry.minecrafttcg.model.Spot;

import java.util.List;
import java.util.Objects;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.EMO_VITRO;

public class EmoVitro extends MinionWithStaticEffect {
    public EmoVitro(MinionInfo minionInfo) {
        super(EMO_VITRO.card(), minionInfo, EmoVitro::getTargets, EmoVitro::effectForTargets, EmoVitro::removeEffect, false);
    }

    public static List<Minion> getTargets(Minion m) {
        return List.of(m);
    }

    public static void effectForTargets(Minion source, Minion target) {
        long numAllies = source.minionInfo().stadium().allyMinionSpots(source.minionInfo().master())
            .stream()
            .map(Spot::minionRef)
            .map(mr -> mr.apply(source.minionInfo().stadium()))
            .filter(Objects::nonNull)
            .filter(m -> !m.equals(source))
            .count();
        if (numAllies >= 3) {
            target.setStaticStrengthBonus(source, 2);
            target.setStaticMaxHealthBonus(source, 2);
        }
    }

    public static void removeEffect(Minion source, Minion target) {
        target.setStaticStrengthBonus(source, 0);
        target.setStaticMaxHealthBonus(source, 0);
    }
}
