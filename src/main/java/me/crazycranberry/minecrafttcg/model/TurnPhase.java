package me.crazycranberry.minecrafttcg.model;

import me.crazycranberry.minecrafttcg.events.CombatEndEvent;
import me.crazycranberry.minecrafttcg.events.CombatStartEvent;
import me.crazycranberry.minecrafttcg.events.EndOfTurnPhaseStartedEvent;
import me.crazycranberry.minecrafttcg.events.FirstSummoningPhaseStartedEvent;
import me.crazycranberry.minecrafttcg.events.FirstPreCombatPhaseStartedEvent;
import me.crazycranberry.minecrafttcg.events.SecondSummoningPhaseStartedEvent;
import me.crazycranberry.minecrafttcg.events.SecondPreCombatPhaseStartedEvent;
import me.crazycranberry.minecrafttcg.events.TurnEndEvent;
import org.bukkit.event.Event;

public enum TurnPhase {
    MULLIGAN_PHASE(FirstPreCombatPhaseStartedEvent.class),
    FIRST_PRECOMBAT_PHASE(SecondPreCombatPhaseStartedEvent.class),
    SECOND_PRECOMBAT_PHASE(CombatStartEvent.class),
    COMBAT_PHASE(CombatEndEvent.class),
    POST_COMBAT_CLEANUP(FirstSummoningPhaseStartedEvent.class),
    FIRST_SUMMONING_PHASE(SecondSummoningPhaseStartedEvent.class),
    SECOND_SUMMONING_PHASE(EndOfTurnPhaseStartedEvent.class),
    END_OF_TURN(TurnEndEvent.class);

    private final Class<? extends Event> nextPhaseRequestEventClass;

    TurnPhase(Class<? extends Event> nextPhaseRequestEventClass) {
        this.nextPhaseRequestEventClass = nextPhaseRequestEventClass;
    }

    public Class<? extends Event> nextPhaseRequestEventClass() {
        return nextPhaseRequestEventClass;
    }
}
