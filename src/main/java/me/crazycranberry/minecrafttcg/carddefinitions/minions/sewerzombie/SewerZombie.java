package me.crazycranberry.minecrafttcg.carddefinitions.minions.sewerzombie;


import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

public class SewerZombie extends Minion {
    public SewerZombie(MinionInfo minionInfo) {
        super(CardEnum.SEWER_ZOMBIE.card(), minionInfo);
    }

    @Override
    public String signDescription() {
        return "";
    }
}
