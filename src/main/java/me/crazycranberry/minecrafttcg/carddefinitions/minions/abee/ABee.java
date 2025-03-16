package me.crazycranberry.minecrafttcg.carddefinitions.minions.abee;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

public class ABee extends Minion {
    public ABee(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo);
        this.setPermanentLifesteal(true);
        this.setPermanentFlying(true);
    }

    @Override
    public String signDescription() {
        return "Lifesteal";
    }
}
