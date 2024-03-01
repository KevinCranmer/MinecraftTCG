package me.crazycranberry.minecrafttcg.carddefinitions.minions.abee;

import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

public class ABee extends Minion {
    public ABee(MinionInfo minionInfo) {
        super(CardEnum.A_BEE, minionInfo);
        this.setPermanentLifesteal(true);
        this.setPermanentFlying(true);
    }
}
