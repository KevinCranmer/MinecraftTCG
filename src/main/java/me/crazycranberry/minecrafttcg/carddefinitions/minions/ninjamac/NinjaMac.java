package me.crazycranberry.minecrafttcg.carddefinitions.minions.ninjamac;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.model.TurnPhase;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class NinjaMac extends Minion {
    public NinjaMac(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo);
        this.setPermanentFlying(true);
    }

    @Override
    public void onDamageDealt(LivingEntity target, Integer damageDealt, Boolean wasCombatAttack, Boolean wasProtected) {
        super.onDamageDealt(target, damageDealt, wasCombatAttack, wasProtected);
        if (target instanceof Player) {
            if (this.minionInfo().stadium().phase().equals(TurnPhase.COMBAT_PHASE)) {
                this.minionInfo().stadium().pendingDrawsForPlayer(this.minionInfo().master(), 1);
            } else {
                this.minionInfo().stadium().draw(this.minionInfo().master());
            }
        }
    }

    @Override
    public String signDescription() {
        return "Draws if it\nhits you";
    }
}
