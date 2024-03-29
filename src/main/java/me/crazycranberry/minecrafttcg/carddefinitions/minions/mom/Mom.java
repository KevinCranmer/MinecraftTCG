package me.crazycranberry.minecrafttcg.carddefinitions.minions.mom;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.baby.Baby;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.baby.BabyDef;
import me.crazycranberry.minecrafttcg.model.Spot;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.MOM;
import static me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition.summonMinion;

public class Mom extends Minion {
    public Mom(MinionInfo minionInfo) {
        super(MOM.card(), minionInfo);
    }

    @Override
    public void onTurnStart() {
        super.onTurnStart();
        Spot spotInFront = this.minionInfo().stadium().getSpotInFront(this.minionInfo().spot());
        if (spotInFront != null && spotInFront.minionRef().apply(this.minionInfo().stadium()) == null) {
            BabyDef babyDef = new BabyDef();
            summonMinion(spotInFront, this.minionInfo().stadium(), this.minionInfo().master(), Baby.class, babyDef.minionType(), babyDef.equipment(), babyDef.entityAdjustment());
        }
    }
}
