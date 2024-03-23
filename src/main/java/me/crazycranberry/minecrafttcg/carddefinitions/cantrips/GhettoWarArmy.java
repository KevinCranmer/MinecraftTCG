package me.crazycranberry.minecrafttcg.carddefinitions.cantrips;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.newAnimationStarted;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.oneAnimationFinished;
import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.DINGY_SKELETON;
import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.SEWER_ZOMBIE;
import static me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition.summonMinion;
import static org.bukkit.Sound.ENTITY_WARDEN_DIG;

public class GhettoWarArmy implements CantripCardDefinition {
    private static final int ticksTillStartSpawning = 20;
    private static final int ticksBetweenSpawning = 5;
    private int tickProgress;
    private int spotProgress;
    private List<Spot> spotsToSpawn;
    private int taskId;

    @Override
    public Integer cost() {
        return 9;
    }

    @Override
    public String cardName() {
        return "Ghetto War Army";
    }

    @Override
    public String cardDescription() {
        return "Summon an army.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.RARE;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        newAnimationStarted(stadium, caster, 1);
        caster.getWorld().playSound(caster.getLocation(), ENTITY_WARDEN_DIG, 0.7f, 1);
        startSpawningTheseBoys(stadium, caster);
    }

    private void startSpawningTheseBoys(Stadium stadium, Player caster) {
        spotsToSpawn = stadium.allyMinionSpots(caster).stream().filter(spot -> spot.minionRef().apply(stadium) == null).toList();
        tickProgress = 0;
        spotProgress = 0;
        taskId = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
            if (spotProgress >= spotsToSpawn.size()) {
                oneAnimationFinished(stadium, caster);
                Bukkit.getScheduler().cancelTask(taskId);
                return;
            }
            if (tickProgress >= ticksTillStartSpawning && tickProgress % ticksBetweenSpawning == 0) {
                if (spotsToSpawn.get(spotProgress).name().endsWith("FRONT") && spotsToSpawn.get(spotProgress).minionRef().apply(stadium) == null) { // Don't feel great about this i'll be honest...
                    summonMinion(spotsToSpawn.get(spotProgress), stadium, caster, ((MinionCardDefinition)SEWER_ZOMBIE.card()).minionClass(), ((MinionCardDefinition)SEWER_ZOMBIE.card()).minionType(), null, ((MinionCardDefinition)SEWER_ZOMBIE.card()).entityAdjustment());
                } else if (spotsToSpawn.get(spotProgress).name().endsWith("BACK") && spotsToSpawn.get(spotProgress).minionRef().apply(stadium) == null) {
                    summonMinion(spotsToSpawn.get(spotProgress), stadium, caster, ((MinionCardDefinition)DINGY_SKELETON.card()).minionClass(), ((MinionCardDefinition)DINGY_SKELETON.card()).minionType(), null, ((MinionCardDefinition)DINGY_SKELETON.card()).entityAdjustment());
                }
                spotProgress++;
            }
            tickProgress++;
        }, 0 /*<-- the initial delay */, 1 /*<-- the interval */).getTaskId();
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(false, false, false, false, false);
    }

    @Override
    public Boolean canCastDuringCombat() {
        return false;
    }
}
