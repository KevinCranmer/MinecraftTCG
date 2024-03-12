package me.crazycranberry.minecrafttcg.carddefinitions.minions.skiturtle;

import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

public class SkiTurtle extends Minion {
    public SkiTurtle(MinionInfo minionInfo) {
        super(CardEnum.SKI_TURTLE, minionInfo);
    }

    @Override
    public void onCombatEnd() {
        super.onCombatEnd();
        Minion ally = this.minionInfo().stadium().getAllyMinionInFront(this.minionInfo().spot());
        if (ally.health() < ally.maxHealth()) {
            ally.onHeal(ally.maxHealth() - ally.health());
        }
    }
}
