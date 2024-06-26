package me.crazycranberry.minecrafttcg.carddefinitions.minions.yousefssoulmender;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.events.PlayerHealedEvent;
import org.bukkit.Bukkit;
import org.bukkit.Particle;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.YOUSEFS_SOUL_MENDER;
import static me.crazycranberry.minecrafttcg.carddefinitions.minions.yousefssoulmender.YousefsSoulMenderDef.HEAL_AMOUNT;

public class YousefsSoulMender extends Minion {
    public YousefsSoulMender(MinionInfo minionInfo) {
        super(YOUSEFS_SOUL_MENDER.card(), minionInfo);
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
