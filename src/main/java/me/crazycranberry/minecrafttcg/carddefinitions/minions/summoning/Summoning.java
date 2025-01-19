package me.crazycranberry.minecrafttcg.carddefinitions.minions.summoning;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

public class Summoning extends Minion {
    public Summoning(MinionInfo minionInfo) {
        super(new SummoningDef(), minionInfo);
    }
    @Override
    public String signDescription() {
        return "";
    }
}
