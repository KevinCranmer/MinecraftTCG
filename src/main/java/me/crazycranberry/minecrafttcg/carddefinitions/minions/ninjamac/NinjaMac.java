package me.crazycranberry.minecrafttcg.carddefinitions.minions.ninjamac;

import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

public class NinjaMac extends Minion {
    public NinjaMac(MinionInfo minionInfo) {
        super(CardEnum.NINJA_MAC, minionInfo);
        this.setPermanentFlying(true);
    }
}
