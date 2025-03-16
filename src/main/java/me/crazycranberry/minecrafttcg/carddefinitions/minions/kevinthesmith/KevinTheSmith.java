package me.crazycranberry.minecrafttcg.carddefinitions.minions.kevinthesmith;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

public class KevinTheSmith extends Minion {
    public KevinTheSmith(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo);
    }

    @Override
    public String signDescription() {
        return "One of the\nDonkey trio";
    }
}
