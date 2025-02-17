package me.crazycranberry.minecrafttcg.carddefinitions.spells;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.List;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.newAnimationStarted;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.oneAnimationFinished;
import static org.bukkit.Sound.BLOCK_PORTAL_TRAVEL;
import static org.bukkit.Sound.BLOCK_VAULT_DEACTIVATE;

public class FellTheMighty implements SpellCardDefinition {
    public static final int FELL_THE_MIGHTY_POWER_THRESHOLD = 4;
    private final static Integer numTicks = 60;
    private final static Integer maxParticles = 120;

    @Override
    public Integer cost() {
        return 5;
    }

    @Override
    public String cardName() {
        return "Fell the Mighty";
    }

    @Override
    public String cardDescription() {
        return String.format("Destroy all minions with power %s or greater.", FELL_THE_MIGHTY_POWER_THRESHOLD);
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.UNCOMMON;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        newAnimationStarted(stadium, caster, 1);
        new FellTheMightyTracker(stadium, caster);
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(false, false, false, false, false);
    }

    private static class FellTheMightyTracker {
        private Integer progressInTicks = 0;
        private static final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.YELLOW, 1);
        private Integer taskId;

        private FellTheMightyTracker(Stadium stadium, Player caster) {
            startLoop(stadium, caster);
            caster.playSound(caster, BLOCK_PORTAL_TRAVEL, 1, 1);
            stadium.opponent(caster).playSound(stadium.opponent(caster), BLOCK_VAULT_DEACTIVATE, 1, 1);
        }

        public void startLoop(Stadium stadium, Player caster) {
            taskId = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
                if (progressInTicks >= numTicks) {
                    oneAnimationFinished(stadium, caster);
                    Bukkit.getScheduler().cancelTask(taskId);
                    return;
                } else if (progressInTicks == (numTicks / 2)) {
                    stadium.killAllMinions(m -> m.strength() >= FELL_THE_MIGHTY_POWER_THRESHOLD);
                }
                stadium.allyMinionSpots(caster).stream()
                    .filter(s -> stadium.minionFromSpot(s) != null)
                    .filter(s -> stadium.minionFromSpot(s).strength() >= FELL_THE_MIGHTY_POWER_THRESHOLD)
                    .forEach(s -> spawnParticles(stadium, s, progressInTicks));
                progressInTicks++;
            }, 0 /*<-- the initial delay */, 1 /*<-- the interval */).getTaskId();
        }

        private static void spawnParticles(Stadium stadium, Spot spot, int progressInTicks) {
            Location particleLoc = stadium.locOfSpot(spot).add(0, 0.5, 0);
            int halfway = numTicks / 2;
            double ratio = progressInTicks <= halfway ? (double) progressInTicks / halfway : numTicks - (double) progressInTicks / halfway;
            int numParticles = (int) (ratio * maxParticles);
            double radius = ratio * 1;
            particleLoc.getWorld().spawnParticle(Particle.DUST, particleLoc, numParticles, radius,radius, radius, dustOptions);
        }
    }
}
