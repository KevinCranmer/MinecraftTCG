package me.crazycranberry.minecrafttcg.carddefinitions.minions.abeekeeper;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.abee.ABee;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.A_BEE;
import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.A_BEE_KEEPER;

public class ABeeKeeper extends Minion {
    public ABeeKeeper(MinionInfo minionInfo) {
        super(A_BEE_KEEPER.card(), minionInfo);
    }

    @Override
    public void onDeath() {
        super.onDeath();
        MinionCardDefinition abhi = (MinionCardDefinition) A_BEE.card();
        MinionCardDefinition.summonMinion(minionInfo().spot(), minionInfo().stadium(), minionInfo().master(), abhi.minionClass(), abhi);
    }
}
