package me.crazycranberry.minecrafttcg.carddefinitions.minions.theknapper;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;

import java.util.Optional;

import static me.crazycranberry.minecrafttcg.managers.StadiumManager.PLAYER_PROXY_ENTITY_TYPE;

public class TheKnapper extends Minion {
    public TheKnapper(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo);
    }

    @Override
    public void onCombatStart() {
        super.onCombatStart();
        Stadium stadium = this.minionInfo().stadium();
        LivingEntity targetEntity = stadium.getTargetInFront(this);
        if (stadium.getAllyMinionInFront(this.minionInfo().spot()) != null || targetEntity.getType().equals(PLAYER_PROXY_ENTITY_TYPE)) {
            return;
        }
        Optional<Minion> targetMinion = stadium.minionFromEntity(targetEntity);
        if (targetMinion.isPresent() && targetMinion.get().strength() > this.strength()) {
            int currentStrength = this.strength();
            this.giveTemporaryStrength(targetMinion.get().strength() - currentStrength);
            targetMinion.get().giveTemporaryStrength(currentStrength - targetMinion.get().strength());
            this.minionInfo().entity().getWorld().spawnParticle(Particle.NOTE, this.minionInfo().entity().getEyeLocation(), 3, 0.75, 0.5, 0.75);
            this.minionInfo().entity().getWorld().spawnParticle(Particle.NOTE, targetEntity.getEyeLocation(), 3, 0.75, 0.5, 0.75);
        }
    }

    @Override
    public String signDescription() {
        return "Swaps strength\nwith stronger\nenemies in\nfront until turn end";
    }
}
