package me.crazycranberry.minecrafttcg.carddefinitions.minions.stoneclone;

import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

public class StoneClone extends Minion {
    public StoneClone(MinionInfo minionInfo){super(CardEnum.STONE_CLONE.card(), minionInfo);}

    @Override
    public String signDescription() {
        return "Player targets\n minion and will\n copy target\n card";
    }
}
