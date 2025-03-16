package me.crazycranberry.minecrafttcg.carddefinitions.minions.pumpernickel;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.PUMPERNICKEL;

public class Pumpernickel extends Minion {
    public Pumpernickel(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo);
    }

    @Override
    public String signDescription() {
        return PUMPERNICKEL.card().cardDescription();
    }
}
