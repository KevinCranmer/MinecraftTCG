package me.crazycranberry.minecrafttcg.carddefinitions.minions.aggressivebandit;

import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

public class AggressiveBandit extends Minion {
    public AggressiveBandit(MinionInfo minionInfo) {
        super(CardEnum.AGGRESSIVE_BANDIT, minionInfo);
        setAttacksPerTurn(2);
    }
}
