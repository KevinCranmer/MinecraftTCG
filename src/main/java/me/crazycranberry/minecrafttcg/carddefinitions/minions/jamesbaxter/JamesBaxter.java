package me.crazycranberry.minecrafttcg.carddefinitions.minions.jamesbaxter;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.Player;

import java.util.Objects;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.JAMES_BAXTER;
import static me.crazycranberry.minecrafttcg.carddefinitions.minions.jamesbaxter.JamesBaxterDef.HEAL_AMOUNT;

public class JamesBaxter extends Minion {
    public JamesBaxter(MinionInfo minionInfo) {
        super(JAMES_BAXTER.card(), minionInfo);
    }

    @Override
    public String signDescription() {
        return "";
    }

    @Override
    public void onEnter() {
        super.onEnter();
        Stadium stadium = this.minionInfo().stadium();
        Player master = this.minionInfo().master();
        stadium.allyMinionSpots(master).stream()
            .map(stadium::minionFromSpot)
            .filter(Objects::nonNull)
            .forEach(m -> m.onHeal(HEAL_AMOUNT));
        stadium.healPlayer(master, HEAL_AMOUNT);
    }
}
