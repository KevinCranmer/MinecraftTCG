package me.crazycranberry.minecrafttcg.carddefinitions.minions.duodueler;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionWithStaticEffect;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;

import java.util.List;
import java.util.Objects;

import static me.crazycranberry.minecrafttcg.carddefinitions.minions.duodueler.DuoDuelerDef.BLOCK_BUFF;
import static me.crazycranberry.minecrafttcg.carddefinitions.minions.duodueler.DuoDuelerDef.HEALTH_BUFF;
import static me.crazycranberry.minecrafttcg.carddefinitions.minions.duodueler.DuoDuelerDef.STRENGTH_BUFF;

public class DuoDueler extends MinionWithStaticEffect {
    public DuoDueler(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo, DuoDueler::getTargets, DuoDueler::effectForTargets, DuoDueler::removeEffect, true);
    }

    @Override
    public String signDescription() {
        return String.format("Buffs partner\nand gives self\nand partner Block +%s", BLOCK_BUFF);
    }

    public static List<Minion> getTargets(Minion m) {
        Stadium stadium = m.minionInfo().stadium();
        List<Spot> allySpot = stadium.allyMinionSpots(m.minionInfo().master());
        List<Minion> allAllies = allySpot.stream()
            .map(stadium::minionFromSpot)
            .filter(Objects::nonNull)
            .toList();
        if (allAllies.size() != 2) {
            return List.of();
        }
        return allAllies;
    }

    public static void effectForTargets(Minion source, Minion target) {
        target.setStaticStrengthBonus(source, STRENGTH_BUFF);
        target.setStaticMaxHealthBonus(source, HEALTH_BUFF);
        target.setStaticBlockBonus(source, BLOCK_BUFF);
    }

    public static void removeEffect(Minion source, Minion target) {
        target.setStaticStrengthBonus(source, 0);
        target.setStaticMaxHealthBonus(source, 0);
        target.setStaticBlockBonus(source, 0);
    }
}
