package me.crazycranberry.minecrafttcg.carddefinitions.minions.mournfulstrix;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.MOURNFUL_STRIX;

public class MournfulStrix extends Minion {
    public MournfulStrix(MinionInfo minionInfo) {
        super(MOURNFUL_STRIX.card(), minionInfo);
    }

    @Override
    public String signDescription() {
        return "Draws when dies";
    }

    @Override
    public void onDeath() {
        super.onDeath();
        this.minionInfo().stadium().draw(this.minionInfo().master());
    }
}
