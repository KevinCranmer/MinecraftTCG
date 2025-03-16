package me.crazycranberry.minecrafttcg.carddefinitions.minions.bulldozer;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

public class BullDozer extends Minion {
    public BullDozer(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo);
        this.setPermanentOverkill(true);
    }

    @Override
    public String signDescription() {
        return "Overkill";
    }
}
