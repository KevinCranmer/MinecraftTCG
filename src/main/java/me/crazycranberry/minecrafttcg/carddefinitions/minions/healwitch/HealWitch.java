package me.crazycranberry.minecrafttcg.carddefinitions.minions.healwitch;

import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.model.Spot;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static me.crazycranberry.minecrafttcg.CommonFunctions.randomFromList;

public class HealWitch extends Minion {
    public HealWitch(MinionInfo minionInfo) {
        super(CardEnum.HEAL_WITCH.card(), minionInfo);
    }

    @Override
    public void onTurnEnd() {
        List<Spot> allySpots = minionInfo().stadium().allyMinionSpots(minionInfo().master());
        Optional<Minion> allyToHeal = randomFromList(allySpots.stream()
            .map(s -> this.minionInfo().stadium().minionFromSpot(s))
            .filter(Objects::nonNull)
            .filter(m -> m.health() < m.maxHealth())
            .toList());
        allyToHeal.ifPresent(minion -> minion.onHeal(1));
    }

    @Override
    public String signDescription() {
        return "Randomly\nheals 1\nally minion";
    }
}
