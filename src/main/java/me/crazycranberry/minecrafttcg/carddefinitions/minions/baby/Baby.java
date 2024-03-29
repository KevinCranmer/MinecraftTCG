package me.crazycranberry.minecrafttcg.carddefinitions.minions.baby;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

public class Baby extends Minion {
    public Baby(MinionInfo minionInfo) {
        super(new BabyDef(), minionInfo);
    }

    @Override
    public String signDescription() {
        return "";
    }
}
