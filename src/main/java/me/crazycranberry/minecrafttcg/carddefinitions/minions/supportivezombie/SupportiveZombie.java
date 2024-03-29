package me.crazycranberry.minecrafttcg.carddefinitions.minions.supportivezombie;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.SUPPORTIVE_ZOMBIE;
import static me.crazycranberry.minecrafttcg.carddefinitions.minions.supportivezombie.SupportiveZombieDef.BONUS_STRENGTH;

public class SupportiveZombie extends Minion {
    public SupportiveZombie(MinionInfo minionInfo) {
        super(SUPPORTIVE_ZOMBIE.card(), minionInfo);
    }

    @Override
    public String signDescription() {
        return "Gives another\nMinion +" + BONUS_STRENGTH + " strength\nuntil end of turn";
    }
}
