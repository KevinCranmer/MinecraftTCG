package me.crazycranberry.minecrafttcg.carddefinitions.minions.borsharak;

import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.LivingEntity;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Borsharak extends Minion {
    public Borsharak(MinionInfo minionInfo) {
        super(CardEnum.BORSHARAK, minionInfo);
    }

    @Override
    public void onDamageDealt(LivingEntity target, Integer damageDealt, Boolean wasCombatAttack, Boolean wasProtected) {
        super.onDamageDealt(target, damageDealt, wasCombatAttack, wasProtected);
        Stadium stadium = this.minionInfo().stadium();
        Optional<Minion> targetMinion = stadium.minionFromEntity(target);
        if (!wasCombatAttack || targetMinion.isEmpty()) {
            return;
        }
        List<Spot> adjacentSpots = stadium.adjacentSpots(targetMinion.get().minionInfo().spot());
        adjacentSpots.stream()
            .map(Spot::minionRef)
            .map(mr -> mr.apply(stadium))
            .filter(Objects::nonNull)
            .forEach(m -> {
                this.onDamageDealt(m.minionInfo().entity(), damageDealt, false, m.isProtected());
                m.onDamageReceived(this.minionInfo().entity(), damageDealt, m.isProtected());
                m.minionInfo().entity().damage(0);
            });
    }
}
