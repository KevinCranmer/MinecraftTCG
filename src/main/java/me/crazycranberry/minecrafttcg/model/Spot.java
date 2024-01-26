package me.crazycranberry.minecrafttcg.model;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static me.crazycranberry.minecrafttcg.model.Stadium.BLUE_MATERIAL;
import static me.crazycranberry.minecrafttcg.model.Stadium.GREEN_MATERIAL;
import static me.crazycranberry.minecrafttcg.model.Stadium.RED_MATERIAL;

public enum Spot {
    RED_2_BACK(new Vector(20, 2, 1), RED_MATERIAL, false, true, true, Spot::redAMinionStatic, Spot::setRedAMinionStatic),
    RED_2_FRONT(new Vector(17, 1, 1), RED_MATERIAL, false, true, true, Spot::redDMinionStatic, Spot::setRedDMinionStatic),
    RED_1_FRONT(new Vector(9, 1, 1), RED_MATERIAL, true, true, true, Spot::red1MinionStatic, Spot::setRed1MinionStatic),
    RED_1_BACK(new Vector(6, 2 ,1), RED_MATERIAL, true, true, true, Spot::red4MinionStatic, Spot::setRed4MinionStatic),
    BLUE_2_BACK(new Vector(20, 2, 5), BLUE_MATERIAL, false, true, true, Spot::blueBMinionStatic, Spot::setBlueBMinionStatic),
    BLUE_2_FRONT(new Vector(17, 1, 5), BLUE_MATERIAL, false, true, true, Spot::blueEMinionStatic, Spot::setBlueEMinionStatic),
    BLUE_1_FRONT(new Vector(9, 1, 5), BLUE_MATERIAL, true, true, true, Spot::blue2MinionStatic, Spot::setBlue2MinionStatic),
    BLUE_1_BACK(new Vector(6, 2,5), BLUE_MATERIAL, true, true, true, Spot::blue5MinionStatic, Spot::setBlue5MinionStatic),
    GREEN_2_BACK(new Vector(20, 2, 9), GREEN_MATERIAL, false, true, true, Spot::greenCMinionStatic, Spot::setGreenCMinionStatic),
    GREEN_2_FRONT(new Vector(17, 1, 9), GREEN_MATERIAL, false, true, true, Spot::greenFMinionStatic, Spot::setGreenFMinionStatic),
    GREEN_1_FRONT(new Vector(9, 1, 9), GREEN_MATERIAL, true, true, true, Spot::green3MinionStatic, Spot::setGreen3MinionStatic),
    GREEN_1_BACK(new Vector(6, 2, 9), GREEN_MATERIAL, true, true, true, Spot::green6MinionStatic, Spot::setGreen6MinionStatic),
    PLAYER_1_OUTLOOK(new Vector(2, 8, 5), Material.BIRCH_PLANKS, true, false, true, null, null),
    PLAYER_2_OUTLOOK(new Vector(24, 8, 5), Material.COBBLESTONE, false, false, true, null, null),
    PLAYER_1_RED_CHICKEN(new Vector(3, 2, 1), RED_MATERIAL, true, false, false, null, null),
    PLAYER_2_RED_CHICKEN(new Vector(23, 2, 1), RED_MATERIAL, true, false, false, null, null),
    PLAYER_1_BLUE_CHICKEN(new Vector(3, 2, 5), BLUE_MATERIAL, true, false, false, null, null),
    PLAYER_2_BLUE_CHICKEN(new Vector(23, 2, 5), BLUE_MATERIAL, true, false, false, null, null),
    PLAYER_1_GREEN_CHICKEN(new Vector(3, 2, 9), GREEN_MATERIAL, true, false, false, null, null),
    PLAYER_2_GREEN_CHICKEN(new Vector(23, 2, 9), GREEN_MATERIAL, true, false, false, null, null);

    private final Vector offset;
    private final Material material;
    private final Boolean isPlayer1Spot;
    private final Boolean isSummonableSpot;
    private final Boolean isTargetable;
    private final Function<Stadium, Minion> minionRef;
    private final BiConsumer<Stadium, Minion> minionSetRef;

    Spot(Vector offset, Material material, Boolean isPlayer1Spot, Boolean isSummonableSpot, Boolean isTargetable, Function<Stadium, Minion> minionRef, BiConsumer<Stadium, Minion> minionSetRef) {
        this.offset = offset;
        this.material = material;
        this.isPlayer1Spot = isPlayer1Spot;
        this.isSummonableSpot = isSummonableSpot;
        this.isTargetable = isTargetable;
        this.minionRef = minionRef;
        this.minionSetRef = minionSetRef;
    }

    public Vector offset() {
        return offset;
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

    public Function<Stadium, Minion> minionRef() {
        return minionRef;
    }

    public BiConsumer<Stadium, Minion> minionSetRef() {
        return minionSetRef;
    }

    public static Spot opposingBackRankSpot(Spot currentSpot) {
        return switch (currentSpot) {
            case RED_1_FRONT, RED_1_BACK -> RED_2_BACK;
            case RED_2_BACK, RED_2_FRONT -> RED_1_FRONT;
            case BLUE_1_FRONT, BLUE_1_BACK -> BLUE_2_BACK;
            case BLUE_2_BACK, BLUE_2_FRONT -> BLUE_1_FRONT;
            case GREEN_1_FRONT, GREEN_1_BACK -> GREEN_2_BACK;
            case GREEN_2_BACK, GREEN_2_FRONT -> GREEN_1_FRONT;
            default ->
                    throw new IllegalArgumentException("You cannot try to get the opposingBackRankSpot from " + currentSpot);
        };
    }

    public static Spot opposingFrontRankSpot(Spot currentSpot) {
        return switch (currentSpot) {
            case RED_1_FRONT, RED_1_BACK -> RED_2_FRONT;
            case RED_2_BACK, RED_2_FRONT -> RED_1_BACK;
            case BLUE_1_FRONT, BLUE_1_BACK -> BLUE_2_FRONT;
            case BLUE_2_BACK, BLUE_2_FRONT -> BLUE_1_BACK;
            case GREEN_1_FRONT, GREEN_1_BACK -> GREEN_2_FRONT;
            case GREEN_2_BACK, GREEN_2_FRONT -> GREEN_1_BACK;
            default ->
                    throw new IllegalArgumentException("You cannot try to get the opposingFrontRankSpot from " + currentSpot);
        };
    }

    public static LivingEntity opposingChicken(Spot currentSpot, Stadium stadium) {
        return switch (currentSpot) {
            case RED_1_FRONT, RED_1_BACK -> stadium.player2RedChicken();
            case RED_2_BACK, RED_2_FRONT -> stadium.player1RedChicken();
            case BLUE_1_FRONT, BLUE_1_BACK -> stadium.player2BlueChicken();
            case BLUE_2_BACK, BLUE_2_FRONT -> stadium.player1BlueChicken();
            case GREEN_1_FRONT, GREEN_1_BACK -> stadium.player2GreenChicken();
            case GREEN_2_BACK, GREEN_2_FRONT -> stadium.player1GreenChicken();
            default ->
                    throw new IllegalArgumentException("You cannot try to get the opposingChicken from " + currentSpot);
        };
    }

    public static Minion redAMinionStatic(Stadium stadium) {
        return stadium.redAMinion();
    }

    public static Minion redDMinionStatic(Stadium stadium) {
        return stadium.redDMinion();
    }

    public static Minion red1MinionStatic(Stadium stadium) {
        return stadium.red1Minion();
    }

    public static Minion red4MinionStatic(Stadium stadium) {
        return stadium.red4Minion();
    }

    public static Minion blueBMinionStatic(Stadium stadium) {
        return stadium.blueBMinion();
    }

    public static Minion blueEMinionStatic(Stadium stadium) {
        return stadium.blueEMinion();
    }

    public static Minion blue2MinionStatic(Stadium stadium) {
        return stadium.blue2Minion();
    }

    public static Minion blue5MinionStatic(Stadium stadium) {
        return stadium.blue5Minion();
    }

    public static Minion greenCMinionStatic(Stadium stadium) {
        return stadium.greenCMinion();
    }

    public static Minion greenFMinionStatic(Stadium stadium) {
        return stadium.greenFMinion();
    }

    public static Minion green3MinionStatic(Stadium stadium) {
        return stadium.green3Minion();
    }

    public static Minion green6MinionStatic(Stadium stadium) {
        return stadium.green6Minion();
    }

    public static Player player1Static(Stadium stadium) {
        return stadium.player1();
    }

    public static Player player2Static(Stadium stadium) {
        return stadium.player2();
    }

    public static void setRedAMinionStatic(Stadium stadium, Minion minion) {
        stadium.setRedAMinion(minion);
    }

    public static void setRedDMinionStatic(Stadium stadium, Minion minion) {
        stadium.setRedDMinion(minion);
    }

    public static void setRed1MinionStatic(Stadium stadium, Minion minion) {
        stadium.setRed1Minion(minion);
    }

    public static void setRed4MinionStatic(Stadium stadium, Minion minion) {
        stadium.setRed4Minion(minion);
    }

    public static void setBlueBMinionStatic(Stadium stadium, Minion minion) {
        stadium.setBlueBMinion(minion);
    }

    public static void setBlueEMinionStatic(Stadium stadium, Minion minion) {
        stadium.setBlueEMinion(minion);
    }

    public static void setBlue2MinionStatic(Stadium stadium, Minion minion) {
        stadium.setBlue2Minion(minion);
    }

    public static void setBlue5MinionStatic(Stadium stadium, Minion minion) {
        stadium.setBlue5Minion(minion);
    }

    public static void setGreenCMinionStatic(Stadium stadium, Minion minion) {
        stadium.setGreenCMinion(minion);
    }

    public static void setGreenFMinionStatic(Stadium stadium, Minion minion) {
        stadium.setGreenFMinion(minion);
    }

    public static void setGreen3MinionStatic(Stadium stadium, Minion minion) {
        stadium.setGreen3Minion(minion);
    }

    public static void setGreen6MinionStatic(Stadium stadium, Minion minion) {
        stadium.setGreen6Minion(minion);
    }
}
