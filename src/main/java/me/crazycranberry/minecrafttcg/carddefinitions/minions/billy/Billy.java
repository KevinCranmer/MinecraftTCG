package me.crazycranberry.minecrafttcg.carddefinitions.minions.billy;

import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.model.Spot;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardUtils.FRONT_ROW_SPOTS;
import static me.crazycranberry.minecrafttcg.carddefinitions.CardUtils.swapTwoSpots;

public class Billy extends Minion {
    private final List<Minion> minionsIHit = new ArrayList<>();
    public Billy(MinionInfo minionInfo) {
        super(CardEnum.BILLY.card(), minionInfo);
    }

    @Override
    public void onDamageDealt(LivingEntity target, Integer damageDealt, Boolean wasCombatAttack, Boolean wasProtected) {
        super.onDamageDealt(target, damageDealt, wasCombatAttack, wasProtected);
        Optional<Minion> targetMinion = this.minionInfo().stadium().minionFromEntity(target);
        targetMinion.ifPresent(minionsIHit::add);
    }

    @Override
    public void onCombatEnd() {
        for (Minion m : minionsIHit) {
            if (FRONT_ROW_SPOTS.contains(m.minionInfo().spot())) {
                swapTwoSpots(this.minionInfo().stadium(), m.minionInfo().spot(), Spot.spotBehind(m.minionInfo().spot()));
            }
        }
        minionsIHit.clear();
        super.onCombatEnd();
    }

    @Override
    public String signDescription() {
        return "Can knock a\nfront row minion\ninto the back row";
    }
}
