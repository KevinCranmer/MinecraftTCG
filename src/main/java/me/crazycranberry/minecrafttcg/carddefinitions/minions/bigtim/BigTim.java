package me.crazycranberry.minecrafttcg.carddefinitions.minions.bigtim;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.BIG_TIM;

public class BigTim extends Minion {
    public BigTim(MinionInfo minionInfo) {
        super(BIG_TIM.card(), minionInfo);
    }

    @Override
    public String signDescription() {
        return "";
    }
}
