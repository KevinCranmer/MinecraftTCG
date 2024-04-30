package me.crazycranberry.minecrafttcg.carddefinitions.minions.oliver;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.OLIVER;

public class Oliver extends Minion {
    public Oliver(MinionInfo minionInfo) {
        super(OLIVER.card(), minionInfo);
    }

    @Override
    public String signDescription() {
        return "Gets +1 strength\nwhenever an enemy\nminion enters";
    }

    @Override
    public void onEnemyMinionEntered(Minion otherMinion) {
        this.addPermanentStrength(1);
    }
}
