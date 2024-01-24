package me.crazycranberry.minecrafttcg.model;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static me.crazycranberry.minecrafttcg.model.Stadium.BLUE_MATERIAL;
import static me.crazycranberry.minecrafttcg.model.Stadium.GREEN_MATERIAL;
import static me.crazycranberry.minecrafttcg.model.Stadium.RED_MATERIAL;

public enum Spot {
    RED_A(new Vector(20, 2, 1), RED_MATERIAL, false, true, Spot::redAMinionStatic, Spot::setRedAMinionStatic),
    RED_D(new Vector(17, 1, 1), RED_MATERIAL, false, true, Spot::redDMinionStatic, Spot::setRedDMinionStatic),
    RED_1(new Vector(9, 1, 1), RED_MATERIAL, true, true, Spot::red1MinionStatic, Spot::setRed1MinionStatic),
    RED_4(new Vector(6, 2 ,1), RED_MATERIAL, true, true, Spot::red4MinionStatic, Spot::setRed4MinionStatic),
    BLUE_B(new Vector(20, 2, 5), BLUE_MATERIAL, false, true, Spot::blueBMinionStatic, Spot::setBlueBMinionStatic),
    BLUE_E(new Vector(17, 1, 5), BLUE_MATERIAL, false, true, Spot::blueEMinionStatic, Spot::setBlueEMinionStatic),
    BLUE_2(new Vector(9, 1, 5), BLUE_MATERIAL, true, true, Spot::blue2MinionStatic, Spot::setBlue2MinionStatic),
    BLUE_5(new Vector(6, 2,5), BLUE_MATERIAL, true, true, Spot::blue5MinionStatic, Spot::setBlue5MinionStatic),
    GREEN_C(new Vector(20, 2, 9), GREEN_MATERIAL, false, true, Spot::greenCMinionStatic, Spot::setGreenCMinionStatic),
    GREEN_F(new Vector(17, 1, 9), GREEN_MATERIAL, false, true, Spot::greenFMinionStatic, Spot::setGreenFMinionStatic),
    GREEN_3(new Vector(9, 1, 9), GREEN_MATERIAL, true, true, Spot::green3MinionStatic, Spot::setGreen3MinionStatic),
    GREEN_6(new Vector(6, 2, 9), GREEN_MATERIAL, true, true, Spot::green6MinionStatic, Spot::setGreen6MinionStatic),
    PLAYER_1_OUTLOOK(new Vector(2, 8, 5), Material.BIRCH_PLANKS, true, false, null, null),
    PLAYER_2_OUTLOOK(new Vector(24, 8, 5), Material.COBBLESTONE, false, false, null, null),
    PLAYER_1_SMACK_ZONE(new Vector(2, 8, 5), Material.BIRCH_PLANKS, true, false, null, null),
    PLAYER_2_SMACK_ZONE(new Vector(24, 8, 5), Material.COBBLESTONE, false, false, null, null);

    private final Vector offset;
    private final Material material;
    private final Boolean isPlayer1Spot;
    private final Boolean isSummonableSpot;
    private final Function<Stadium, Minion> minionRef;
    private final BiConsumer<Stadium, Minion> minionSetRef;

    Spot(Vector offset, Material material, Boolean isPlayer1Spot, Boolean isSummonableSpot, Function<Stadium, Minion> minionRef, BiConsumer<Stadium, Minion> minionSetRef) {
        this.offset = offset;
        this.material = material;
        this.isPlayer1Spot = isPlayer1Spot;
        this.isSummonableSpot = isSummonableSpot;
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

    public Function<Stadium, Minion> minionRef() {
        return minionRef;
    }

    public BiConsumer<Stadium, Minion> minionSetRef() {
        return minionSetRef;
    }

    public static Spot opposingFrontRankSpot(Spot currentSpot) {
        return switch (currentSpot) {
            case RED_1, RED_4 -> RED_A;
            case RED_A, RED_D -> RED_1;
            case BLUE_2, BLUE_5 -> BLUE_B;
            case BLUE_B, BLUE_E -> BLUE_2;
            case GREEN_3, GREEN_6 -> GREEN_C;
            case GREEN_C, GREEN_F -> GREEN_3;
            default ->
                    throw new IllegalArgumentException("You cannot try to get the opposingFrontRankSpot from " + currentSpot);
        };
    }

    public static Spot opposingBackRankSpot(Spot currentSpot) {
        return switch (currentSpot) {
            case RED_1, RED_4 -> RED_D;
            case RED_A, RED_D -> RED_4;
            case BLUE_2, BLUE_5 -> BLUE_E;
            case BLUE_B, BLUE_E -> BLUE_5;
            case GREEN_3, GREEN_6 -> GREEN_F;
            case GREEN_C, GREEN_F -> GREEN_6;
            default ->
                    throw new IllegalArgumentException("You cannot try to get the opposingFrontRankSpot from " + currentSpot);
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
