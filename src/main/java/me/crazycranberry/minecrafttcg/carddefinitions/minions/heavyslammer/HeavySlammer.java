package me.crazycranberry.minecrafttcg.carddefinitions.minions.heavyslammer;

import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import org.bukkit.entity.LivingEntity;

import java.util.Optional;

public class HeavySlammer extends Minion {
    public HeavySlammer(MinionInfo minionInfo) {
        super(CardEnum.HEAVY_SLAMMER, minionInfo);
    }

    @Override
    public void onDamageDealt(LivingEntity target, Integer damageDealt, Boolean wasCombatAttack, Boolean wasProtected) {
        super.onDamageDealt(target, damageDealt, wasCombatAttack, wasProtected);
        Optional<Minion> maybeMinion = minionInfo().stadium().minionFromEntity(target);
        if (maybeMinion.isEmpty()) {
            return;
        }
        Minion minionBehind = minionInfo().stadium().getAllyMinionBehind(maybeMinion.get().minionInfo().spot());
        if (minionBehind == null) {
            return;
        }
        minionBehind.onDamageReceived(minionInfo().entity(), this.strength(), minionBehind.isProtected());
        this.onDamageDealt(minionBehind.minionInfo().entity(), this.strength(), wasCombatAttack, minionBehind.isProtected());
    }
}
