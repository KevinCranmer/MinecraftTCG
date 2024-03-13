package me.crazycranberry.minecrafttcg.model;

import me.crazycranberry.minecrafttcg.events.CombatEndEvent;
import me.crazycranberry.minecrafttcg.events.CombatStartEvent;
import me.crazycranberry.minecrafttcg.events.EndOfTurnPhaseStartedEvent;
import me.crazycranberry.minecrafttcg.events.FirstPostCombatPhaseStartedEvent;
import me.crazycranberry.minecrafttcg.events.SecondPostCombatPhaseStartedEvent;
import me.crazycranberry.minecrafttcg.events.SecondPreCombatPhaseStartedEvent;
import me.crazycranberry.minecrafttcg.events.TurnEndEvent;
import org.bukkit.event.Event;

public enum TurnPhase {
    FIRST_PRECOMBAT_PHASE(SecondPreCombatPhaseStartedEvent.class),
    SECOND_PRECOMBAT_PHASE(CombatStartEvent.class),
    COMBAT_PHASE(CombatEndEvent.class),
    POST_COMBAT_CLEANUP(FirstPostCombatPhaseStartedEvent.class),
    FIRST_POSTCOMBAT_PHASE(SecondPostCombatPhaseStartedEvent.class),
    SECOND_POSTCOMBAT_PHASE(EndOfTurnPhaseStartedEvent.class),
    END_OF_TURN(TurnEndEvent.class);

    private Class<? extends Event> nextPhaseRequestEventClass;

    TurnPhase(Class<? extends Event> nextPhaseRequestEventClass) {
        this.nextPhaseRequestEventClass = nextPhaseRequestEventClass;
    }

    public Class<? extends Event> nextPhaseRequestEventClass() {
        return nextPhaseRequestEventClass;
    }
}
