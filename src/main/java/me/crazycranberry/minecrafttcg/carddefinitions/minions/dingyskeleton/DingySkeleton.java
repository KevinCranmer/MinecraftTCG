package me.crazycranberry.minecrafttcg.carddefinitions.minions.dingyskeleton;

import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

public class DingySkeleton extends Minion {
    public DingySkeleton(MinionInfo minionInfo) {
        super(CardEnum.DINGY_SKELETON, minionInfo);
    }
    @Override
    public void onTurnStart() {
        super.onTurnStart();
        attacksLeft(2);
    }
    @Override
    public void onCombatStart() {

    }

    @Override
    public void onTurnEnd() {

    }
}
