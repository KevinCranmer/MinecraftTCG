package me.crazycranberry.minecrafttcg.carddefinitions.minions.scaredduelist;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

public class ScaredDuelist extends Minion {
    public ScaredDuelist(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo);
        setAttacksPerTurn(3);
    }

    @Override
    public String signDescription() {
        return "Multi-Attack 3";
    }
}
