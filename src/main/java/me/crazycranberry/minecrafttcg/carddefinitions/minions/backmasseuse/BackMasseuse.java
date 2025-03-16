package me.crazycranberry.minecrafttcg.carddefinitions.minions.backmasseuse;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionWithStaticEffect;

import java.util.List;

import static me.crazycranberry.minecrafttcg.carddefinitions.minions.backmasseuse.BackMasseuseDef.BONUS_HEALTH;
import static me.crazycranberry.minecrafttcg.carddefinitions.minions.backmasseuse.BackMasseuseDef.BONUS_STRENGTH;

public class BackMasseuse extends MinionWithStaticEffect {
    public BackMasseuse(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo, BackMasseuse::getTargets, BackMasseuse::effectForTargets, BackMasseuse::removeEffect, true);
    }

    public static List<Minion> getTargets(Minion m) {
        Minion minionInFront = m.minionInfo().stadium().getAllyMinionInFront(m.minionInfo().spot());
        if (minionInFront != null) {
            return List.of(minionInFront);
        }
        return List.of();
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
        return "Buffs ally\nin front";
    }
}
