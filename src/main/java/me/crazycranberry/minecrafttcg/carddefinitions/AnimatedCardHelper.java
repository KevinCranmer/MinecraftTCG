package me.crazycranberry.minecrafttcg.carddefinitions;

import me.crazycranberry.minecrafttcg.events.CardAnimationFinishedEvent;
import me.crazycranberry.minecrafttcg.events.CardAnimationStartedEvent;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;

public class AnimatedCardHelper {
    private final static Map<UUID, AnimatedCardHelperInfo> animationsInProgress = new HashMap<>();
    private final static int animatedMaxDuration = 200;

    public static void newAnimationStarted(Stadium stadium, Player caster, Integer numberOfAnimations) {
        animationsInProgress.put(caster.getUniqueId(), new AnimatedCardHelperInfo(numberOfAnimations == null ? 1 : numberOfAnimations, startNewTimer(stadium, caster)));
        Bukkit.getPluginManager().callEvent(new CardAnimationStartedEvent(stadium, caster));
    }

    private static int startNewTimer(Stadium stadium, Player caster) {
        return Bukkit.getScheduler().runTaskLater(getPlugin(), () -> timerExpired(stadium, caster), animatedMaxDuration).getTaskId();
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
        Bukkit.getScheduler().cancelTask(animationsInProgress.remove(caster.getUniqueId()).timeoutTimerTaskId());
        Bukkit.getPluginManager().callEvent(new CardAnimationFinishedEvent(stadium, caster));
    }

    public static void timerExpired(Stadium stadium, Player caster) {
        AnimatedCardHelperInfo info = animationsInProgress.get(caster.getUniqueId());
        if (info != null) {
            allAnimationsFinished(stadium, caster);
        }
    }
}
