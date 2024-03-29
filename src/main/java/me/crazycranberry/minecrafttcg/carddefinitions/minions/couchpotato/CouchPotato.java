package me.crazycranberry.minecrafttcg.carddefinitions.minions.couchpotato;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.COUCH_POTATO;

public class CouchPotato extends Minion {
    public CouchPotato(MinionInfo minionInfo) {
        super(COUCH_POTATO.card(), minionInfo);
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
