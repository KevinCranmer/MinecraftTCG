package me.crazycranberry.minecrafttcg.carddefinitions.minions.scaredduelist;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.SCARED_DUELIST;

public class ScaredDuelist extends Minion {
    public ScaredDuelist(MinionInfo minionInfo) {
        super(SCARED_DUELIST.card(), minionInfo);
        setAttacksPerTurn(3);
    }
}
