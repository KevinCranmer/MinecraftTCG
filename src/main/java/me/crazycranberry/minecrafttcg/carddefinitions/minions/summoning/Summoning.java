package me.crazycranberry.minecrafttcg.carddefinitions.minions.summoning;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

public class Summoning extends Minion {
    public Summoning(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo);
    }
    @Override
    public String signDescription() {
        return "";
    }
}
