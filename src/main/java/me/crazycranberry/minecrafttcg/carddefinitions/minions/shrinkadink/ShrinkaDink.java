package me.crazycranberry.minecrafttcg.carddefinitions.minions.shrinkadink;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import org.bukkit.Material;
import org.bukkit.Particle;
import java.util.Map;

import static me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition.summonMinion;
import static org.bukkit.Sound.BLOCK_TRIAL_SPAWNER_OMINOUS_ACTIVATE;

public class ShrinkaDink extends Minion {
    Map<Integer, MinionCardDefinition> shrinkies = Map.of(
        4, new ShrinkaDink3Def(),
        3, new ShrinkaDink2Def(),
        2, new ShrinkaDink1Def()
    );

    public ShrinkaDink(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo);
    }

    @Override
    public String signDescription() {
        return "Resummons with\n-1 str, if it\nhad >1 base\nstrength";
    }

    @Override
    public void onDeath() {
        super.onDeath();
        resummon();
    }

    private void resummon() {
        MinionCardDefinition nextCardDef = shrinkies.get(this.baseStrength());
        if (nextCardDef == null) {
            return;
        }
        this.minionInfo().entity().getWorld().spawnParticle(Particle.SQUID_INK, this.minionInfo().entity().getEyeLocation(), 10, 0.25, 0.5, 0.25);
        this.minionInfo().entity().getWorld().playSound(this.minionInfo().entity(), BLOCK_TRIAL_SPAWNER_OMINOUS_ACTIVATE, 1, 1);
        summonMinion(this.minionInfo().spot(), this.minionInfo().stadium(), this.minionInfo().master(), ShrinkaDink.class, nextCardDef);
    }
}
