package me.crazycranberry.minecrafttcg.carddefinitions.minions.farmer;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

public class Farmer extends Minion {
    public Farmer(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo);
    }

    @Override
    public void onAllyMinionEntered(Minion otherMinion) {
        super.onAllyMinionEntered(otherMinion);
        this.minionInfo().stadium().draw(this.minionInfo().master());
    }

    @Override
    public String signDescription() {
        return "Draws when\nally animal\nenters battlefield";
    }
}
