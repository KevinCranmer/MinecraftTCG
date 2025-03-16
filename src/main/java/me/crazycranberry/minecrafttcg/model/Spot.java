package me.crazycranberry.minecrafttcg.model;

import org.bukkit.Material;
import org.bukkit.util.Vector;

import static me.crazycranberry.minecrafttcg.model.Column.BLUE;
import static me.crazycranberry.minecrafttcg.model.Column.GREEN;
import static me.crazycranberry.minecrafttcg.model.Column.RED;
import static me.crazycranberry.minecrafttcg.model.Stadium.BLUE_MATERIAL;
import static me.crazycranberry.minecrafttcg.model.Stadium.GREEN_MATERIAL;
import static me.crazycranberry.minecrafttcg.model.Stadium.RED_MATERIAL;

public enum Spot {
    RED_2_BACK(new Vector(20, 2, 1), RED, RED_MATERIAL, false, true, true),
    RED_2_FRONT(new Vector(17, 1, 1), RED, RED_MATERIAL, false, true, true),
    RED_1_FRONT(new Vector(9, 1, 1), RED, RED_MATERIAL, true, true, true),
    RED_1_BACK(new Vector(6, 2 ,1), RED, RED_MATERIAL, true, true, true),
    BLUE_2_BACK(new Vector(20, 2, 5), BLUE, BLUE_MATERIAL, false, true, true),
    BLUE_2_FRONT(new Vector(17, 1, 5), BLUE, BLUE_MATERIAL, false, true, true),
    BLUE_1_FRONT(new Vector(9, 1, 5), BLUE, BLUE_MATERIAL, true, true, true),
    BLUE_1_BACK(new Vector(6, 2,5), BLUE, BLUE_MATERIAL, true, true, true),
    GREEN_2_BACK(new Vector(20, 2, 9), GREEN, GREEN_MATERIAL, false, true, true),
    GREEN_2_FRONT(new Vector(17, 1, 9), GREEN, GREEN_MATERIAL, false, true, true),
    GREEN_1_FRONT(new Vector(9, 1, 9), GREEN, GREEN_MATERIAL, true, true, true),
    GREEN_1_BACK(new Vector(6, 2, 9), GREEN, GREEN_MATERIAL, true, true, true),
    PLAYER_1_OUTLOOK(new Vector(2, 8, 5), null, Material.BIRCH_PLANKS, true, false, true),
    PLAYER_2_OUTLOOK(new Vector(24, 8, 5), null, Material.COBBLESTONE, false, false, true),
    PLAYER_1_RED_CHICKEN(new Vector(3, 2, 1), null, RED_MATERIAL, true, false, false),
    PLAYER_2_RED_CHICKEN(new Vector(23, 2, 1), null, RED_MATERIAL, true, false, false),
    PLAYER_1_BLUE_CHICKEN(new Vector(3, 2, 5), null, BLUE_MATERIAL, true, false, false),
    PLAYER_2_BLUE_CHICKEN(new Vector(23, 2, 5), null, BLUE_MATERIAL, true, false, false),
    PLAYER_1_GREEN_CHICKEN(new Vector(3, 2, 9), null, GREEN_MATERIAL, true, false, false),
    PLAYER_2_GREEN_CHICKEN(new Vector(23, 2, 9), null, GREEN_MATERIAL, true, false, false);

    private final Vector offset;
    private final Column column;
    private final Material material;
    private final Boolean isPlayer1Spot;
    private final Boolean isSummonableSpot;
    private final Boolean isTargetable;
    public static final int MIDDLE_X = (int) ((RED_1_FRONT.offset().getX() + RED_2_FRONT.offset().getX()) / 2);
    public static final int RADIUS_X = (int) ((PLAYER_1_RED_CHICKEN.offset().getX() + PLAYER_2_GREEN_CHICKEN.offset().getX()) / 2);
    public static final int RADIUS_Z = (int) ((PLAYER_1_RED_CHICKEN.offset().getZ() + PLAYER_2_GREEN_CHICKEN.offset().getZ()) / 2);
    public static final double LENGTH_OF_BATTLEFIELD = GREEN_1_FRONT.offset().getZ() - RED_1_FRONT.offset().getZ();

    Spot(Vector offset, Column column, Material material, Boolean isPlayer1Spot, Boolean isSummonableSpot, Boolean isTargetable) {
        this.offset = offset;
        this.column = column;
        this.material = material;
        this.isPlayer1Spot = isPlayer1Spot;
        this.isSummonableSpot = isSummonableSpot;
        this.isTargetable = isTargetable;
    }

    public Vector offset() {
        return offset;
    }

    public Column column() {
        return column;
    }

    public Material material() {
        return material;
    }

    public Boolean isPlayer1Spot() {
        return isPlayer1Spot;
    }

    public Boolean isSummonableSpot() {
        return isSummonableSpot;
    }

    public Boolean isTargetable() {
        return isTargetable;
    }

    public static Spot spotBehind(Spot currentSpot) {
        return switch (currentSpot) {
            case RED_1_FRONT -> RED_1_BACK;
            case RED_2_FRONT -> RED_2_BACK;
            case BLUE_1_FRONT -> BLUE_1_BACK;
            case BLUE_2_FRONT -> BLUE_2_BACK;
            case GREEN_1_FRONT -> GREEN_1_BACK;
            case GREEN_2_FRONT -> GREEN_2_BACK;
            default ->
                throw new IllegalArgumentException("You cannot try to get the spotBehind from " + currentSpot);
        };
    }

    public static Spot opposingBackRankSpot(Spot currentSpot) {
        return switch (currentSpot) {
            case RED_1_FRONT, RED_1_BACK -> RED_2_BACK;
            case RED_2_BACK, RED_2_FRONT -> RED_1_BACK;
            case BLUE_1_FRONT, BLUE_1_BACK -> BLUE_2_BACK;
            case BLUE_2_BACK, BLUE_2_FRONT -> BLUE_1_BACK;
            case GREEN_1_FRONT, GREEN_1_BACK -> GREEN_2_BACK;
            case GREEN_2_BACK, GREEN_2_FRONT -> GREEN_1_BACK;
            default ->
                    throw new IllegalArgumentException("You cannot try to get the opposingBackRankSpot from " + currentSpot);
        };
    }

    public static Spot opposingFrontRankSpot(Spot currentSpot) {
        return switch (currentSpot) {
            case RED_1_FRONT, RED_1_BACK -> RED_2_FRONT;
            case RED_2_BACK, RED_2_FRONT -> RED_1_FRONT;
            case BLUE_1_FRONT, BLUE_1_BACK -> BLUE_2_FRONT;
            case BLUE_2_BACK, BLUE_2_FRONT -> BLUE_1_FRONT;
            case GREEN_1_FRONT, GREEN_1_BACK -> GREEN_2_FRONT;
            case GREEN_2_BACK, GREEN_2_FRONT -> GREEN_1_FRONT;
            default ->
                    throw new IllegalArgumentException("You cannot try to get the opposingFrontRankSpot from " + currentSpot);
        };
    }
}
