package me.crazycranberry.minecrafttcg.carddefinitions.minions.persistence;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.model.TurnPhase;
import org.bukkit.Particle;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.PERSISTENCE;
import static me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition.summonMinion;
import static org.bukkit.Sound.BLOCK_TRIAL_SPAWNER_OMINOUS_ACTIVATE;

public class Persistence extends Minion {
    public Persistence(MinionInfo minionInfo) {
        super(PERSISTENCE.card(), minionInfo);
    }

    @Override
    public void onDeath() {
        super.onDeath();
        if (this.minionInfo().stadium().phase().equals(TurnPhase.POST_COMBAT_CLEANUP)) {
            resummon();
        }
    }

    private void resummon() {
        this.minionInfo().entity().getWorld().spawnParticle(Particle.SQUID_INK, this.minionInfo().entity().getEyeLocation(), 10, 0.25, 0.5, 0.25);
        this.minionInfo().entity().getWorld().playSound(this.minionInfo().entity(), BLOCK_TRIAL_SPAWNER_OMINOUS_ACTIVATE, 1, 1);
        summonMinion(this.minionInfo().spot(), this.minionInfo().stadium(), this.minionInfo().master(), Persistence.class, (MinionCardDefinition) PERSISTENCE.card());
    }

    @Override
    public String signDescription() {
        return "Resurrects when\nkilled during\ncombat";
    }
}
