package me.crazycranberry.minecrafttcg.carddefinitions.minions.yellowpanther;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionWithStaticEffect;

import java.util.List;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.YELLOW_PANTHER;

public class YellowPanther extends MinionWithStaticEffect {
    public YellowPanther(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo, YellowPanther::getTargets, YellowPanther::effectForTargets, YellowPanther::removeEffect, false);
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

    @Override
    public String signDescription() {
        return "Strength bonus\nequal to amount\nof health missing";
    }
}
