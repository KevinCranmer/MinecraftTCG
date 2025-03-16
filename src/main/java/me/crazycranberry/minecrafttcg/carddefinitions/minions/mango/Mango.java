package me.crazycranberry.minecrafttcg.carddefinitions.minions.mango;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.MANGO;

public class Mango extends Minion {
    public Mango(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo);
        setAttacksPerTurn(2);
    }

    @Override
    public String signDescription() {
        return MANGO.card().cardDescription();
    }
}
