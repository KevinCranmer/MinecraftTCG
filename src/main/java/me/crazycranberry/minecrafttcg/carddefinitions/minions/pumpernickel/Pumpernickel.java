package me.crazycranberry.minecrafttcg.carddefinitions.minions.pumpernickel;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.PUMPERNICKEL;

public class Pumpernickel extends Minion {
    public Pumpernickel(MinionInfo minionInfo) {
        super(PUMPERNICKEL.card(), minionInfo);
    }

    @Override
    public String signDescription() {
        return PUMPERNICKEL.card().cardDescription();
    }
}
