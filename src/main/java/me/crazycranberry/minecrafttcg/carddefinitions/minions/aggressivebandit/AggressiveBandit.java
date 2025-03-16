package me.crazycranberry.minecrafttcg.carddefinitions.minions.aggressivebandit;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

public class AggressiveBandit extends Minion {
    public AggressiveBandit(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo);
        setAttacksPerTurn(2);
    }

    @Override
    public String signDescription() {
        return "Multi-Attack 2";
    }
}
