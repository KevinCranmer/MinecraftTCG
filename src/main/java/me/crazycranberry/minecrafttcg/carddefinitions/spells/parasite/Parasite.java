package me.crazycranberry.minecrafttcg.carddefinitions.spells.parasite;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.World;

public class Parasite extends Minion {
    public Parasite(MinionInfo minionInfo) {
        super(new ParasiteDef(), minionInfo);
    }

    @Override
    public String signDescription() {
        return "Deals 1 damage\nto it's controller\nwhen an ally\nenters or dies";
    }

    @Override
    public void onAllyMinionEntered(Minion otherMinion) {
        super.onAllyMinionEntered(otherMinion);
        leech(otherMinion);
    }

    @Override
    public void onAllyMinionDied(Minion otherMinion) {
        super.onAllyMinionDied(otherMinion);
        leech(otherMinion);
    }

    private void leech(Minion otherMinion) {
        World world = otherMinion.minionInfo().entity().getWorld();
        world.spawnParticle(Particle.DUST, otherMinion.minionInfo().entity().getLocation().add(0, 0.25, 0), 10, 0.3, 0.25, 0.3, new Particle.DustOptions(Color.BLACK, 1));
        world.spawnParticle(Particle.DUST, this.minionInfo().entity().getLocation().add(0, 0.25, 0), 10, 0.3, 0.25, 0.3, new Particle.DustOptions(Color.BLACK, 1));
        world.spawnParticle(Particle.DUST, this.minionInfo().master().getLocation().add(0, 0.25, 0), 10, 0.3, 0.25, 0.3, new Particle.DustOptions(Color.BLACK, 1));
        this.minionInfo().master().damage(1);
    }
}
