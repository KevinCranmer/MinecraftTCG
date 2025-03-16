package me.crazycranberry.minecrafttcg.carddefinitions.minions.couchpotato;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

public class CouchPotato extends Minion {
    public CouchPotato(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo);
    }

    @Override
    public void onTurnEnd() {
        super.onTurnEnd();
        this.onHeal(this.maxHealth());
    }

    @Override
    public String signDescription() {
        return "Heals to max\nat end of turn";
    }
}
