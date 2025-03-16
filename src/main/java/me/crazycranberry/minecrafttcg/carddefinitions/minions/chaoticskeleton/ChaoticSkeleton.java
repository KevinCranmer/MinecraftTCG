package me.crazycranberry.minecrafttcg.carddefinitions.minions.chaoticskeleton;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;

import static me.crazycranberry.minecrafttcg.CommonFunctions.randomFromList;
import static me.crazycranberry.minecrafttcg.carddefinitions.CardUtils.swapTwoSpots;

public class ChaoticSkeleton extends Minion {
    public ChaoticSkeleton(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo);
    }

    @Override
    public String signDescription() {
        return "Randomly swaps\npositions on turn\nstart";
    }

    @Override
    public void onTurnStart() {
        Stadium stadium = this.minionInfo().stadium();
        Spot spot = this.minionInfo().spot();
        Spot newSpot = randomFromList(stadium.allyMinionSpots(this.minionInfo().master()).stream().filter(s -> !s.equals(spot)).toList()).get();
        swapTwoSpots(stadium, spot, newSpot);
    }
}
