package me.crazycranberry.minecrafttcg.carddefinitions.minions.mournfulstrix;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

public class MournfulStrix extends Minion {
    public MournfulStrix(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo);
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
