package me.crazycranberry.minecrafttcg.carddefinitions.minions.emovitro;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionWithStaticEffect;

import java.util.List;
import java.util.Objects;

public class EmoVitro extends MinionWithStaticEffect {
    public EmoVitro(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo, EmoVitro::getTargets, EmoVitro::effectForTargets, EmoVitro::removeEffect, false);
    }

    public static List<Minion> getTargets(Minion m) {
        return List.of(m);
    }

    public static void effectForTargets(Minion source, Minion target) {
        long numAllies = source.minionInfo().stadium().allyMinionSpots(source.minionInfo().master())
            .stream()
            .map(s -> source.minionInfo().stadium().minionFromSpot(s))
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

    @Override
    public String signDescription() {
        return "Gets +2 str\nand +2 health\nwhen it has\n3 allies";
    }
}
