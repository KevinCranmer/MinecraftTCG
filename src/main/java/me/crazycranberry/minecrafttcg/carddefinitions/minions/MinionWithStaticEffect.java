package me.crazycranberry.minecrafttcg.carddefinitions.minions;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;

public abstract class MinionWithStaticEffect extends Minion {
    private List<Minion> currentTargets;
    private final BiConsumer<Minion, Minion> removeEffect;
    private final int taskId;

    public MinionWithStaticEffect(Card card, MinionInfo minionInfo, Function<Minion, List<Minion>> getTargets, BiConsumer<Minion, Minion> effectForTargets, BiConsumer<Minion, Minion> removeEffect) {
        super(card, minionInfo);
        this.currentTargets = getTargets.apply(this);
        this.removeEffect = removeEffect;
        this.currentTargets.forEach(t -> effectForTargets.accept(this, t));
        this.taskId = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
            List<Minion> newTargets = getTargets.apply(this);
            currentTargets.forEach(t -> removeEffect.accept(this, t));
            newTargets.forEach(t -> effectForTargets.accept(this, t));
            currentTargets = newTargets;
        }, 1, 1).getTaskId();
    }

    public void cancelTask() {
        currentTargets.forEach(t -> removeEffect.accept(this, t));
        currentTargets = List.of();
        Bukkit.getScheduler().cancelTask(taskId);
    }

    @Override
    public void onDeath() {
        super.onDeath();
        cancelTask();
    }
}
