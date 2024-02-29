package me.crazycranberry.minecrafttcg.carddefinitions;

public class TargetRules {
    private final boolean targetsAllyMinions;
    private final boolean targetsEnemyMinions;
    private final boolean targetsPlayers;
    private final boolean targetsEmptyAllySpots;
    private final boolean targetsEmptyEnemySpots;

    public TargetRules(boolean targetsAllyMinions, boolean targetsEnemyMinions, boolean targetsPlayers, boolean targetsEmptyAllySpots, boolean targetsEmptyEnemySpots) {
        this.targetsAllyMinions = targetsAllyMinions;
        this.targetsEnemyMinions = targetsEnemyMinions;
        this.targetsPlayers = targetsPlayers;
        this.targetsEmptyAllySpots = targetsEmptyAllySpots;
        this.targetsEmptyEnemySpots = targetsEmptyEnemySpots;
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

    public boolean targetsEmptyAllySpots() {
        return targetsEmptyAllySpots;
    }

    public boolean targetsEmptyEnemySpots() {
        return targetsEmptyEnemySpots;
    }
}
