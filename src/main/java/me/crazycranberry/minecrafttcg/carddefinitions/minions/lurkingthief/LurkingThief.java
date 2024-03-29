package me.crazycranberry.minecrafttcg.carddefinitions.minions.lurkingthief;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.model.TurnPhase;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.LURKING_THIEF;

public class LurkingThief extends Minion {
    public LurkingThief(MinionInfo minionInfo) {
        super(LURKING_THIEF.card(), minionInfo);
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
