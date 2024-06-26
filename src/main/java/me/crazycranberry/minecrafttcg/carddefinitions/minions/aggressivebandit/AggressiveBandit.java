package me.crazycranberry.minecrafttcg.carddefinitions.minions.aggressivebandit;

import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

public class AggressiveBandit extends Minion {
    public AggressiveBandit(MinionInfo minionInfo) {
        super(CardEnum.AGGRESSIVE_BANDIT.card(), minionInfo);
        setAttacksPerTurn(2);
    }

    @Override
    public String signDescription() {
        return "Multi-Attack 2";
    }
}
