package me.crazycranberry.minecrafttcg.carddefinitions.minions.slenderman;

import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;

import java.util.Objects;
import java.util.Optional;

import static me.crazycranberry.minecrafttcg.CommonFunctions.randomFromList;
import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.newAnimationStarted;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.oneAnimationFinished;

public class Slenderman extends Minion {
    Location targetLoc;
    Location slendermanLoc;

    public Slenderman(MinionInfo minionInfo) {
        super(CardEnum.SLENDERMAN.card(), minionInfo);
        this.setAttacksPerTurn(0);
    }

    @Override
    public void onTurnStart() {
        super.onTurnStart();
        newAnimationStarted(this, 1);
        Optional<Minion> target = getTeleportTarget();
        if (target.isEmpty()) {
            return;
        }
        removeGoals();
        setLocations(target.get());
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> teleportSlenderman(targetLoc), 15);
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> damageTarget(target.get()), 25);
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> teleportSlenderman(slendermanLoc), 45);
        Bukkit.getScheduler().runTaskLater(getPlugin(), this::allDone, 50);
    }

    private void allDone() {
        setupGoals();
        oneAnimationFinished(this);
    }

    private void damageTarget(Minion target) {
        target.onDamageReceived(this.minionInfo().entity(), this.strength(), target.isProtected());
    }

    private void teleportSlenderman(Location location) {
        this.minionInfo().entity().teleport(location);
        this.slendermanLoc.getWorld().spawnParticle(Particle.WITCH, this.slendermanLoc.clone().add(0, 0.5, 0), 25, 0.8, 0.8, 0.8);
        this.slendermanLoc.getWorld().spawnParticle(Particle.WITCH, this.targetLoc.clone().add(0, 0.5, 0), 25, 0.8, 0.8, 0.8);
    }

    private void setLocations(Minion target) {
        Stadium stadium = this.minionInfo().stadium();
        this.slendermanLoc = stadium.locOfSpot(this.minionInfo().spot());
        this.targetLoc = stadium.locOfSpot(target.minionInfo().spot());
        int additionalX = 1;
        if (slendermanLoc.getX() > targetLoc.getX()) {
            additionalX = -1;
        }
        this.targetLoc.add(additionalX, 0, 0);
        this.slendermanLoc.getWorld().spawnParticle(Particle.WITCH, this.slendermanLoc.clone().add(0, 0.5, 0), 15, 0.5, 0.5, 0.5);
        this.slendermanLoc.getWorld().spawnParticle(Particle.WITCH, this.targetLoc.clone().add(0, 0.5, 0), 15, 0.5, 0.5, 0.5);
    }

    private Optional<Minion> getTeleportTarget() {
        var entity = this.minionInfo().stadium().getTargetInFront(this);
        var minion = this.minionInfo().stadium().minionFromEntity(entity);
        if (minion.isPresent()) {
            return minion;
        }
        minion = randomFromList(this.minionInfo().stadium().enemyMinionSpots(this.minionInfo().master())
            .stream()
            .map(s -> this.minionInfo().stadium().minionFromSpot(s))
            .filter(Objects::nonNull)
            .toList());
        return minion;
    }

    @Override
    public String signDescription() {
        return String.format("Pacifist. Hits\nan enemy minion\nat start of\neach turn.");
    }
}
