package me.crazycranberry.minecrafttcg.carddefinitions.minions.brutalsniper;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.BRUTAL_SNIPER;

public class BrutalSniper extends Minion {
    public BrutalSniper(MinionInfo minionInfo) {
        super(BRUTAL_SNIPER.card(), minionInfo);
    }

    @Override
    public String signDescription() {
        return "";
    }
}
