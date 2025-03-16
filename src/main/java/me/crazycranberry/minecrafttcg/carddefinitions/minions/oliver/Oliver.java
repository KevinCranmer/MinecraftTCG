package me.crazycranberry.minecrafttcg.carddefinitions.minions.oliver;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

public class Oliver extends Minion {
    public Oliver(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo);
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
