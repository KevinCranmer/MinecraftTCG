package me.crazycranberry.minecrafttcg.carddefinitions.minions.sewerzombie;


import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.events.CombatStartEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class SewerZombie extends Minion {
    public SewerZombie(MinionInfo minionInfo) {
        super(CardEnum.SEWER_ZOMBIE.card(), minionInfo);
    }
}
