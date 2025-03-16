package me.crazycranberry.minecrafttcg.carddefinitions.minions.reaper;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.model.TurnPhase;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Reaper extends Minion {
    private final List<Minion> minionsReaperDamaged = new ArrayList<>();

    public Reaper(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo);
    }

    @Override
    public void onCombatStart() {
        super.onCombatStart();
        minionsReaperDamaged.clear();
    }

    @Override
    public void onCombatEnd() {
        for (Minion m : minionsReaperDamaged) {
            if (m.health() <= 0) {
                this.minionInfo().stadium().draw(this.minionInfo().master());
            }
        }
        minionsReaperDamaged.clear();
        super.onCombatEnd();
    }

    @Override
    public void onDamageDealt(LivingEntity target, Integer damageDealt, Boolean wasCombatAttack, Boolean wasProtected) {
        super.onDamageDealt(target, damageDealt, wasCombatAttack, wasProtected);
        Optional<Minion> attackedMinion = this.minionInfo().stadium().minionFromEntity(target);
        if (attackedMinion.isEmpty() || wasProtected || damageDealt == 0) {
            return;
        }
        TurnPhase phase = this.minionInfo().stadium().phase();
        if (phase.equals(TurnPhase.COMBAT_PHASE)) {
            minionsReaperDamaged.add(attackedMinion.get());
        } else {
            if (attackedMinion.get().health() <= damageDealt) {
                this.minionInfo().stadium().draw(this.minionInfo().master());
            }
        }
    }

    @Override
    public String signDescription() {
        return "Draws when\nit kills";
    }
}
