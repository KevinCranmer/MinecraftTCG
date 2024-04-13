package me.crazycranberry.minecrafttcg.model;

public enum Column {
    RED(0),
    BLUE(4),
    GREEN(8);

    private final int zOffset;

    Column(int zOffset) {
        this.zOffset = zOffset;
    }

    public int zOffset() {
        return zOffset;
    }
}
