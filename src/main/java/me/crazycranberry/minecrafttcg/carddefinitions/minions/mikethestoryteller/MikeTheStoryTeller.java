package me.crazycranberry.minecrafttcg.carddefinitions.minions.mikethestoryteller;

import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

public class MikeTheStoryTeller extends Minion {
    public MikeTheStoryTeller(MinionInfo minionInfo) {
        super(CardEnum.MIKE_THE_STORY_TELLER.card(), minionInfo);
    }

    @Override
    public String signDescription() {
        return "One of the\nhog trio";
    }
}
