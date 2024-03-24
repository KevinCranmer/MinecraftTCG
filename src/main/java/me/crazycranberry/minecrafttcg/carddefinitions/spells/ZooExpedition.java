package me.crazycranberry.minecrafttcg.carddefinitions.spells;

import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.ParticleBeamInfo;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.packleader.PackLeaderDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.zookeeperallie.ZookeeperAllieDef;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static me.crazycranberry.minecrafttcg.CommonFunctions.randomFromList;
import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.newAnimationStarted;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.oneAnimationFinished;
import static me.crazycranberry.minecrafttcg.carddefinitions.CardUtils.ANIMAL_TYPES;
import static me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition.summonMinion;
import static org.bukkit.Particle.SPELL_INSTANT;
import static org.bukkit.Sound.BLOCK_SPONGE_ABSORB;

public class ZooExpedition implements SpellCardDefinition {
    private int taskId;
    private int progressIndex = 0;
    private int tickProgress = 0;
    private List<Minion> minionsToTransform;
    private final int ticksBetweenTransformation = 4;
    private final List<Class<? extends MinionCardDefinition>> exclusions = List.of(
        PackLeaderDef.class, // I don't want to exclude this, but I need some way to manage static abilities that allows me to disable death/enter triggers.
        ZookeeperAllieDef.class
    );

    @Override
    public Integer cost() {
        return 5;
    }

    @Override
    public String cardName() {
        return "Zoo Expedition";
    }

    @Override
    public String cardDescription() {
        return String.format("Turn all Minions into %sAnimals%s.", ChatColor.BOLD, ChatColor.RESET);
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.RARE;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        newAnimationStarted(stadium, caster, 1);
        progressIndex = 0;
        tickProgress = 0;
        minionsToTransform = stadium.allMinions();
        Collections.shuffle(minionsToTransform);
        taskId = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
            if (progressIndex >= minionsToTransform.size()) {
                oneAnimationFinished(stadium, caster);
                Bukkit.getScheduler().cancelTask(taskId);
                return;
            }
            if (tickProgress % ticksBetweenTransformation == 0) {
                transformMinion(minionsToTransform.get(progressIndex));
                progressIndex++;
            }
            tickProgress++;
        }, 0 /*<-- the initial delay */, 1 /*<-- the interval */).getTaskId();
    }

    private void transformMinion(Minion minion) {
        minion.minionInfo().spot().minionSetRef().accept(minion.minionInfo().stadium(), null);
        LivingEntity entity = minion.minionInfo().entity();
        entity.remove();
        entity.getWorld().playSound(entity.getEyeLocation(), BLOCK_SPONGE_ABSORB, 1, 1);
        entity.getWorld().spawnParticle(SPELL_INSTANT, entity.getEyeLocation(), 5, 0.5, 0.25, 0.5);
        List<MinionCardDefinition> minionCardDefinitions = Arrays.stream(CardEnum.values())
            .map(CardEnum::card)
            .filter(c -> c instanceof MinionCardDefinition)
            .filter(c -> !exclusions.contains(c.getClass()))
            .map(c -> (MinionCardDefinition) c)
            .filter(c -> ANIMAL_TYPES.contains(c.minionType()))
            .toList();
        MinionCardDefinition minionCard = randomFromList(minionCardDefinitions).get();
        Minion newMinion = summonMinion(minion.minionInfo().spot(), minion.minionInfo().stadium(), minion.minionInfo().master(), minionCard.minionClass(), minionCard.minionType(), minionCard.equipment(), minionCard.entityAdjustment(),false);
        newMinion.loadTemporaryEffects(minion);
        newMinion.setupGoals();
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(false, false, false, false, false);
    }
}
