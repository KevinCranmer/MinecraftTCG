package me.crazycranberry.minecrafttcg.carddefinitions.minions.yousefssoulmender;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import org.bukkit.Particle;

import static me.crazycranberry.minecrafttcg.carddefinitions.minions.yousefssoulmender.YousefsSoulMenderDef.HEAL_AMOUNT;

public class YousefsSoulMender extends Minion {
    public YousefsSoulMender(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo);
    }

    @Override
    public void onTurnEnd() {
        super.onTurnEnd();
        double currentHealth = this.minionInfo().master().getHealth();
        if (currentHealth < 20) {
            this.minionInfo().stadium().healPlayer(this.minionInfo().master(), HEAL_AMOUNT);
            this.minionInfo().master().getWorld().spawnParticle(Particle.HEART, this.minionInfo().master().getEyeLocation(), 7, 0.5, 0.75, 0.5);
        }
    }

    @Override
    public String signDescription() {
        return "Heals its\ncontroller 2\nat end of turn";
    }
}
