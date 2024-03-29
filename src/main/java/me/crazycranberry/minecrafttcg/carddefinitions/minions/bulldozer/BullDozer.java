package me.crazycranberry.minecrafttcg.carddefinitions.minions.bulldozer;

import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

public class BullDozer extends Minion {
    public BullDozer(MinionInfo minionInfo) {
        super(CardEnum.BULLDOZER.card(), minionInfo);
        this.setPermanentOverkill(true);
    }

    @Override
    public String signDescription() {
        return "Overkill";
    }
}
