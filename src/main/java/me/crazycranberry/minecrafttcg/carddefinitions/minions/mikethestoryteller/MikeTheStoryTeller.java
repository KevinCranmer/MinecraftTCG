package me.crazycranberry.minecrafttcg.carddefinitions.minions.mikethestoryteller;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

public class MikeTheStoryTeller extends Minion {
    public MikeTheStoryTeller(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo);
    }

    @Override
    public String signDescription() {
        return "One of the\nDonkey trio";
    }
}
