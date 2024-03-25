package me.crazycranberry.minecrafttcg.carddefinitions.minions.yellowpanther;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionWithStaticEffect;

import java.util.List;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.YELLOW_PANTHER;

public class YellowPanther extends MinionWithStaticEffect {
    public YellowPanther(MinionInfo minionInfo) {
        super(YELLOW_PANTHER.card(), minionInfo, YellowPanther::getTargets, YellowPanther::effectForTargets, YellowPanther::removeEffect);
    }

    public static List<Minion> getTargets(Minion m) {
        return List.of(m);
    }

    public static void effectForTargets(Minion source, Minion target) {
        target.setStaticStrengthBonus(source, target.maxHealth() - target.health());
    }

    public static void removeEffect(Minion source, Minion target) {
        target.setStaticStrengthBonus(source, 0);
    }
}
