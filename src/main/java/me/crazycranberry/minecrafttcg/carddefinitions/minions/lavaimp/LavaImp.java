package me.crazycranberry.minecrafttcg.carddefinitions.minions.lavaimp;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

import static me.crazycranberry.minecrafttcg.carddefinitions.minions.lavaimp.LavaImpDef.DAMAGE_TO_CONTROLLER;

public class LavaImp extends Minion {
    public LavaImp(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo);
    }

    @Override
    public void onEnter() {
        super.onEnter();
        this.minionInfo().master().damage(DAMAGE_TO_CONTROLLER);
    }

    @Override
    public String signDescription() {
        return "Dealt 3 to\nits controller\nwhen it entered";
    }
}
