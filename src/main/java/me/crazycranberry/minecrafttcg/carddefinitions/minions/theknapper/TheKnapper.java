package me.crazycranberry.minecrafttcg.carddefinitions.minions.theknapper;

import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;

import java.util.Optional;

import static me.crazycranberry.minecrafttcg.managers.StadiumManager.PLAYER_PROXY_ENTITY_TYPE;

public class TheKnapper extends Minion {
    public TheKnapper(MinionInfo minionInfo) {
        super(CardEnum.THE_KNAPPER, minionInfo);
    }

    @Override
    public void onCombatStart() {
        super.onCombatStart();
        Stadium stadium = this.minionInfo().stadium();
        LivingEntity targetEntity = stadium.getTargetInFront(this);
        if (stadium.hasAllyMinionInFront(this.minionInfo().spot()) || targetEntity.getType().equals(PLAYER_PROXY_ENTITY_TYPE)) {
            return;
        }
        Optional<Minion> targetMinion = stadium.minionFromEntity(targetEntity);
        if (targetMinion.isPresent()) {
            int currentStrength = this.strength();
            this.setStrength(targetMinion.get().strength());
            targetMinion.get().setStrength(currentStrength);
            this.minionInfo().entity().getWorld().spawnParticle(Particle.NOTE, this.minionInfo().entity().getEyeLocation(), 3, 0.75, 0.5, 0.75);
            this.minionInfo().entity().getWorld().spawnParticle(Particle.NOTE, targetEntity.getEyeLocation(), 3, 0.75, 0.5, 0.75);
        }
    }
}
