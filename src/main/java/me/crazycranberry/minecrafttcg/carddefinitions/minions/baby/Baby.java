package me.crazycranberry.minecrafttcg.carddefinitions.minions.baby;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

public class Baby extends Minion {
    public Baby(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo);
    }

    @Override
    public String signDescription() {
        return "";
    }
}
