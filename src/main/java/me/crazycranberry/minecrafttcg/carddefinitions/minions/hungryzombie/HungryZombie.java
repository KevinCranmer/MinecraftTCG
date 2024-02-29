package me.crazycranberry.minecrafttcg.carddefinitions.minions.hungryzombie;

import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import org.bukkit.entity.Zombie;

public class HungryZombie extends Minion {
    public HungryZombie(MinionInfo minionInfo) {
        super(CardEnum.HUNGRY_ZOMBIE, minionInfo);
    }
}
