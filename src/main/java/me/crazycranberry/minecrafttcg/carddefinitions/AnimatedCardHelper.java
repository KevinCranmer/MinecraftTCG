package me.crazycranberry.minecrafttcg.carddefinitions;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.events.CardAnimationFinishedEvent;
import me.crazycranberry.minecrafttcg.events.CardAnimationStartedEvent;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.MinecraftTCG.logger;

public class AnimatedCardHelper {
    private final static Map<UUID, AnimatedCardHelperInfo> animationsInProgress = new HashMap<>();
    private final static int animatedMaxDuration = 200;

    public static void newAnimationStarted(Stadium stadium, Player caster, Integer numberOfAnimations) {
        AnimatedCardHelperInfo info = animationsInProgress.get(caster.getUniqueId());
        int numAnimations = numberOfAnimations;
        if (info != null) {
            numAnimations += info.numAnimationsRemaining();
            Bukkit.getScheduler().cancelTask(info.timeoutTimerTaskId());
        }
        animationsInProgress.put(caster.getUniqueId(), new AnimatedCardHelperInfo(numAnimations, startNewTimer(stadium, caster)));
        Bukkit.getPluginManager().callEvent(new CardAnimationStartedEvent(stadium, caster));
    }

    public static void newAnimationStarted(Minion minion, Integer numberOfAnimations) {
        newAnimationStarted(minion.minionInfo().stadium(), minion.minionInfo().master(), numberOfAnimations);
    }

    private static int startNewTimer(Stadium stadium, Player caster) {
        return Bukkit.getScheduler().runTaskLater(getPlugin(), () -> timerExpired(stadium, caster), animatedMaxDuration).getTaskId();
    }

    public static boolean oneAnimationFinished(Minion minion) {
        return oneAnimationFinished(minion.minionInfo().stadium(), minion.minionInfo().master());
    }

    /** Returns true if that was the final animation. */
    public static boolean oneAnimationFinished(Stadium stadium, Player caster) {
        AnimatedCardHelperInfo info = animationsInProgress.get(caster.getUniqueId());
        if (info == null || info.numAnimationsRemaining() <= 1) {
            allAnimationsFinished(stadium, caster);
            return true;
        }
        info.oneAnimationFinished();
        info.newTimeoutTimer(startNewTimer(stadium, caster));
        return false;
    }

    public static void allAnimationsFinished(Stadium stadium, Player caster) {
        AnimatedCardHelperInfo animationRemoved = animationsInProgress.remove(caster.getUniqueId());
        if (animationRemoved == null) {
            logger().severe("Attempted to finish an animation that didn't exist. This was an animation for " + caster.getName());
        }
        Bukkit.getScheduler().cancelTask(animationRemoved.timeoutTimerTaskId());
        Bukkit.getPluginManager().callEvent(new CardAnimationFinishedEvent(stadium, caster));
    }

    public static void timerExpired(Stadium stadium, Player caster) {
        AnimatedCardHelperInfo info = animationsInProgress.get(caster.getUniqueId());
        if (info != null) {
            allAnimationsFinished(stadium, caster);
        }
    }
}
