package me.crazycranberry.minecrafttcg.carddefinitions.minions.lurkingthief;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.model.TurnPhase;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class LurkingThief extends Minion {
    public LurkingThief(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo);
    }

    @Override
    public void onDamageDealt(LivingEntity target, Integer damageDealt, Boolean wasCombatAttack, Boolean wasProtected) {
        super.onDamageDealt(target, damageDealt, wasCombatAttack, wasProtected);
        if (target instanceof Player) {
            if (this.minionInfo().stadium().phase().equals(TurnPhase.COMBAT_PHASE)) {
                this.minionInfo().stadium().pendingManaReplenishForPlayer(this.minionInfo().master(), damageDealt);
            } else {
                this.minionInfo().stadium().draw(this.minionInfo().master());
            }
        }
    }

    @Override
    public String signDescription() {
        return "Replenishes mana\nbased on damage\ndone to player";
    }
}
