package me.crazycranberry.minecrafttcg.managers.utils;

import me.crazycranberry.minecrafttcg.model.Spot;

import java.util.ArrayList;
import java.util.List;

public class CastInProgress {
    private final String cardUuid;
    private final List<Spot> targets;

    public CastInProgress(String cardUuid) {
        this.cardUuid = cardUuid;
        this.targets = new ArrayList<>();
    }

    public String cardUuid() {
        return cardUuid;
    }

    public List<Spot> targets() {
        return targets;
    }

    public void addTarget(Spot newTarget) {
        targets.add(newTarget);
    }
}
