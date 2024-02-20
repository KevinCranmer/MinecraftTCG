package me.crazycranberry.minecrafttcg.carddefinitions;

public class TargetRules {
    private final boolean targetsAllyMinions;
    private final boolean targetsEnemyMinions;
    private final boolean targetsPlayers;
    private final boolean targetsEmptySpots;

    public TargetRules(boolean targetsAllyMinions, boolean targetsEnemyMinions, boolean targetsPlayers, boolean targetsEmptySpots) {
        this.targetsAllyMinions = targetsAllyMinions;
        this.targetsEnemyMinions = targetsEnemyMinions;
        this.targetsPlayers = targetsPlayers;
        this.targetsEmptySpots = targetsEmptySpots;
    }

    public boolean targetsAllyMinions() {
        return targetsAllyMinions;
    }

    public boolean targetsEnemyMinions() {
        return targetsEnemyMinions;
    }

    public boolean targetsPlayers() {
        return targetsPlayers;
    }

    public boolean targetsEmptySpots() {
        return targetsEmptySpots;
    }
}
