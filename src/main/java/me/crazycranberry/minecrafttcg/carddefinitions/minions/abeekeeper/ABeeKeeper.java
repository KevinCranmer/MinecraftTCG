package me.crazycranberry.minecrafttcg.carddefinitions.minions.abeekeeper;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.A_BEE;

public class ABeeKeeper extends Minion {
    public ABeeKeeper(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo);
    }

    @Override
    public void onDeath() {
        super.onDeath();
        MinionCardDefinition abhi = (MinionCardDefinition) A_BEE.card();
        MinionCardDefinition.summonMinion(minionInfo().spot(), minionInfo().stadium(), minionInfo().master(), abhi.minionClass(), abhi);
    }

    @Override
    public String signDescription() {
        return "On death \nSummon A Bee";
    }
}
