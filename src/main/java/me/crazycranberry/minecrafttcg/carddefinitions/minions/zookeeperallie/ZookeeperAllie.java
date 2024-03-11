package me.crazycranberry.minecrafttcg.carddefinitions.minions.zookeeperallie;

import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardUtils.ANIMAL_TYPES;

public class ZookeeperAllie extends Minion {
    public ZookeeperAllie(MinionInfo minionInfo) {
        super(CardEnum.ZOOKEEPER_ALLIE, minionInfo);
    }

    @Override
    public void onEnter() {
        super.onEnter();
        int numAnimals = 0;
        Stadium stadium = this.minionInfo().stadium();
        for (Spot spot : stadium.allyMinionSpots(this.minionInfo().master())) {
            Minion minion = spot.minionRef().apply(stadium);
            if (minion != null && ANIMAL_TYPES.contains(minion.minionInfo().entity().getType())) {
                numAnimals++;
            }
        }
        for (Spot spot : stadium.enemyMinionSpots(this.minionInfo().master())) {
            Minion minion = spot.minionRef().apply(stadium);
            if (minion != null && ANIMAL_TYPES.contains(minion.minionInfo().entity().getType())) {
                numAnimals++;
            }
        }
        this.addPermanentStrength(numAnimals);
        this.setMaxHealth(this.maxHealth() + numAnimals);
        this.setHealthNoHealTrigger(this.maxHealth());
    }
}
