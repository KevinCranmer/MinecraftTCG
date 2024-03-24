package me.crazycranberry.minecrafttcg.carddefinitions.minions;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;

public abstract class MinionWithStaticEffect extends Minion {
    private List<Minion> currentTargets;
    private final Function<Minion, List<Minion>> getTargets;
    private final Consumer<Minion> effectForTargets;
    private final Consumer<Minion> removeEffect;
    private final int taskId;

    public MinionWithStaticEffect(Card card, MinionInfo minionInfo, Function<Minion, List<Minion>> getTargets, Consumer<Minion> effectForTargets, Consumer<Minion> removeEffect) {
        super(card, minionInfo);
        this.getTargets = getTargets;
        this.currentTargets = getTargets.apply(this);
        this.effectForTargets = effectForTargets;
        this.removeEffect = removeEffect;
        this.currentTargets.forEach(effectForTargets);
        this.taskId = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
            List<Minion> newTargets = getTargets.apply(this);
            boolean targetsAreTheSame = true;
            if (currentTargets.size() != newTargets.size()) {
                targetsAreTheSame = false;
            } else {
                for (int i = 0; i < currentTargets.size(); i++) {
                    if (!currentTargets.get(i).equals(newTargets.get(i))) {
                        targetsAreTheSame = false;
                        break;
                    }
                }
            }
            if (!targetsAreTheSame) {
                currentTargets.forEach(removeEffect);
                newTargets.forEach(effectForTargets);
                currentTargets = newTargets;
            }
        }, 1, 1).getTaskId();
    }

    @Override
    public void onDeath() {
        super.onDeath();
        currentTargets.forEach(removeEffect);
        Bukkit.getScheduler().cancelTask(taskId);
    }
}
