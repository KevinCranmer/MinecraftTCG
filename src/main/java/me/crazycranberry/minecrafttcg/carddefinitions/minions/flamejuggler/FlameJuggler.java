package me.crazycranberry.minecrafttcg.carddefinitions.minions.flamejuggler;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.Objects;

import static me.crazycranberry.minecrafttcg.CommonFunctions.randomFromList;
import static me.crazycranberry.minecrafttcg.carddefinitions.minions.flamejuggler.FlameJugglerDef.DAMAGE;

public class FlameJuggler extends Minion {
    public FlameJuggler(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo);
    }

    @Override
    public String signDescription() {
        return String.format("When ally minion\nenters, deal %s\nto random enemy\nminion", DAMAGE);
    }

    @Override
    public void onAllyMinionEntered(Minion otherMinion) {
        Stadium stadium = this.minionInfo().stadium();
        Player master = this.minionInfo().master();
        randomFromList(
            stadium.enemyMinionSpots(master).stream()
                .map(stadium::minionFromSpot)
                .filter(Objects::nonNull)
                .toList()
        ).ifPresent(m -> {
            m.onDamageReceived(master, DAMAGE, m.isProtected());
            m.minionInfo().entity().getWorld().spawnParticle(Particle.LAVA, m.minionInfo().entity().getEyeLocation(), 7, 0.5, 0.75, 0.5);
        });
    }
}
