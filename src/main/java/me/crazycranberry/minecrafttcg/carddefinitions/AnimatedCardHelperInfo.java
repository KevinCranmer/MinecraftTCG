package me.crazycranberry.minecrafttcg.carddefinitions;

import org.bukkit.Bukkit;

public class AnimatedCardHelperInfo {
    private int numAnimationsRemaining;
    private int timeoutTimerTaskId;

    public AnimatedCardHelperInfo(int numAnimationsRemaining, int timeoutTimerTaskId) {
        this.numAnimationsRemaining = numAnimationsRemaining;
        this.timeoutTimerTaskId = timeoutTimerTaskId;
    }

    public int numAnimationsRemaining() {
        return numAnimationsRemaining;
    }

    public int timeoutTimerTaskId() {
        return timeoutTimerTaskId;
    }

    public void oneAnimationFinished() {
        numAnimationsRemaining--;
    }

    public void newTimeoutTimer(int timeoutTimerTaskId) {
        Bukkit.getScheduler().cancelTask(this.timeoutTimerTaskId);
        this.timeoutTimerTaskId = timeoutTimerTaskId;
    }
}
