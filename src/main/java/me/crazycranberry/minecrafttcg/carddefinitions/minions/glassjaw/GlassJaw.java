package me.crazycranberry.minecrafttcg.carddefinitions.minions.glassjaw;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import org.bukkit.Bukkit;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.GLASS_JAW;

public class GlassJaw extends Minion {
    public GlassJaw(MinionInfo minionInfo) {
        super(GLASS_JAW.card(), minionInfo);
    }

    @Override
    public void onEnter() {
        super.onEnter();
        Minion thisMinion = this;
        thisMinion.setPermanentOverkill(true);
        Bukkit.getScheduler().runTaskLater(getPlugin(), thisMinion::shouldIBeDead, 2);
    }

    @Override
    public String signDescription() {
        return "";
    }
}
