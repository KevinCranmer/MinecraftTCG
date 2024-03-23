package me.crazycranberry.minecrafttcg.carddefinitions.minions;

import me.crazycranberry.minecrafttcg.events.CombatEndEvent;
import me.crazycranberry.minecrafttcg.events.CombatStartAttackingEvent;
import me.crazycranberry.minecrafttcg.events.CombatStartEvent;
import me.crazycranberry.minecrafttcg.events.EndOfTurnPhaseStartedEvent;
import me.crazycranberry.minecrafttcg.events.FirstPreCombatPhaseStartedEvent;
import me.crazycranberry.minecrafttcg.events.MinionEnteredEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ListenerForIndividualMinion implements Listener {
    private final Minion minion;
    public ListenerForIndividualMinion(Minion minion) {
        this.minion = minion;
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onTurnStartEvent(FirstPreCombatPhaseStartedEvent event) {
        if (event.getStadium().equals(minion.minionInfo().stadium())) {
            minion.onTurnStart();
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    private void onCombatStartEvent(CombatStartEvent event) {
        if (event.getStadium().equals(minion.minionInfo().stadium())) {
            minion.onCombatStart();
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    private void onCombatStartAttackingEvent(CombatStartAttackingEvent event) {
        if (event.getStadium().equals(minion.minionInfo().stadium())) {
            minion.attackInFront();
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    private void onCombatEndEvent(CombatEndEvent event) {
        if (event.getStadium().equals(minion.minionInfo().stadium())) {
            minion.onCombatEnd();
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    private void onTurnEndEvent(EndOfTurnPhaseStartedEvent event) {
        if (event.getStadium().equals(minion.minionInfo().stadium())) {
            minion.onTurnEnd();
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    private void onMinionEnteredEvent(MinionEnteredEvent event) {
        if (event.stadium().equals(minion.minionInfo().stadium()) && !event.minion().equals(this)) {
            if (event.master().equals(minion.minionInfo().master())) {
                minion.onAllyMinionEntered(event.minion());
            } else {
                minion.onEnemyMinionEntered(event.minion());
            }
        }
    }
}
