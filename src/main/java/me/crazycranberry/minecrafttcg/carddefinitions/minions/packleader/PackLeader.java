package me.crazycranberry.minecrafttcg.carddefinitions.minions.packleader;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.model.Spot;

import java.util.List;
import java.util.Objects;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.PACK_LEADER;

public class PackLeader extends Minion {
    public PackLeader(MinionInfo minionInfo) {
        super(PACK_LEADER.card(), minionInfo);
    }

    @Override
    public void onEnter() {
        super.onEnter();
        List<Spot> adjacentSpots = minionInfo().stadium().adjacentSpots(minionInfo().spot());
        adjacentSpots.stream()
            .map(Spot::minionRef)
            .filter(Objects::nonNull)
            .map(mr -> mr.apply(minionInfo().stadium()))
            .filter(Objects::nonNull)
            .forEach(m -> m.addPermanentStrength(1));
    }

    @Override
    public void onAllyMinionEntered(Minion otherMinion) {
        super.onAllyMinionEntered(otherMinion);
        List<Spot> adjacentSpots = minionInfo().stadium().adjacentSpots(minionInfo().spot());
        if (adjacentSpots.contains(otherMinion.minionInfo().spot())) {
            otherMinion.addPermanentStrength(1);
        }
    }

    @Override
    public void onDeath() {
        super.onDeath();
        List<Spot> adjacentSpots = minionInfo().stadium().adjacentSpots(minionInfo().spot());
        adjacentSpots.stream()
            .map(Spot::minionRef)
            .filter(Objects::nonNull)
            .map(mr -> mr.apply(minionInfo().stadium()))
            .filter(Objects::nonNull)
            .forEach(m -> m.addPermanentStrength(-1));
    }
}
