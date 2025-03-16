package me.crazycranberry.minecrafttcg.carddefinitions.spells;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.MultiTargetCard;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.List;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.newAnimationStarted;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.oneAnimationFinished;

public class Fight implements SpellCardDefinition, MultiTargetCard {
    @Override
    public Integer cost() {
        return 1;
    }

    @Override
    public String cardName() {
        return "Fight";
    }

    @Override
    public String cardDescription() {
        return "Target ally minion fights target enemy minion.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.COMMON;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        if (targets.size() <= 1) {
            return;
        }
        Minion ally = stadium.minionFromSpot(targets.get(0));
        Minion enemy = stadium.minionFromSpot(targets.get(1));
        if (ally == null || enemy == null) {
            return;
        }
        newAnimationStarted(stadium, caster, 1);
        ally.removeGoals();
        Location home = stadium.locOfSpot(ally.minionInfo().spot());
        Location destination = getDestLoc(home, enemy, stadium);
        home.getWorld().spawnParticle(Particle.WITCH, ally.minionInfo().entity().getLocation().clone().add(0, 0.5, 0), 15, 0.5, 0.5, 0.5);
        home.getWorld().spawnParticle(Particle.WITCH, destination.clone().add(0, 0.5, 0), 15, 0.5, 0.5, 0.5);
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> teleportAlly(ally, destination), 15);
        int tickDelay = 25;
        for (int i = 0; i < Math.max(ally.getAttacksPerTurn(), enemy.getAttacksPerTurn()); i++) {
            if (i < ally.getAttacksPerTurn()) {
                Bukkit.getScheduler().runTaskLater(getPlugin(), () -> damage(ally, enemy), tickDelay);
            }
            if (i < enemy.getAttacksPerTurn()) {
                Bukkit.getScheduler().runTaskLater(getPlugin(), () -> damage(enemy, ally), tickDelay);
            }
            tickDelay += 10;
        }
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> teleportAlly(ally, home), tickDelay + 20);
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> allDone(ally), tickDelay + 25);
    }

    private void damage(Minion damager, Minion receiver) {
        receiver.onDamageReceived(damager.minionInfo().entity(), damager.strength(), receiver.isProtected());
    }

    private void teleportAlly(Minion ally, Location destination) {
        ally.minionInfo().entity().teleport(destination);
        destination.getWorld().spawnParticle(Particle.WITCH, destination.clone().add(0, 0.5, 0), 25, 0.8, 0.8, 0.8);
        destination.getWorld().spawnParticle(Particle.WITCH, ally.minionInfo().entity().getLocation().clone().add(0, 0.5, 0), 25, 0.8, 0.8, 0.8);
    }

    private Location getDestLoc(Location home, Minion enemy, Stadium stadium) {
        Location destination = stadium.locOfSpot(enemy.minionInfo().spot());
        int additionalX = 1;
        if (home.getX() > destination.getX()) {
            additionalX = -1;
        }
        destination.add(additionalX, 0, 0);
        return destination;
    }

    private void allDone(Minion ally) {
        ally.setupGoals();
        oneAnimationFinished(ally);
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(true, false, false, false, false);
    }

    @Override
    public List<TargetRules> targetRulesForExtraTargets() {
        return List.of(new TargetRules(false, true, false, false, false));
    }

    @Override
    public boolean isValidAdditionalTarget(Player p, Stadium stadium, Card card, List<Spot> targets, Spot newTarget) {
        return true;
    }
}
