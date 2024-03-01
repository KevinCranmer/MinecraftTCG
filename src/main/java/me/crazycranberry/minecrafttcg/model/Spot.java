package me.crazycranberry.minecrafttcg.model;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import org.apache.commons.lang3.function.TriConsumer;
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
    RED_2_BACK(new Vector(20, 2, 1), RED_MATERIAL, false, true, true, Spot::red2BackMinionStatic, Spot::setRed2BackMinionStatic),
    RED_2_FRONT(new Vector(17, 1, 1), RED_MATERIAL, false, true, true, Spot::red2FrontMinionStatic, Spot::setRed2FrontMinionStatic),
    RED_1_FRONT(new Vector(9, 1, 1), RED_MATERIAL, true, true, true, Spot::red1FrontMinionStatic, Spot::setRed1FrontMinionStatic),
    RED_1_BACK(new Vector(6, 2 ,1), RED_MATERIAL, true, true, true, Spot::red1BackMinionStatic, Spot::setRed1BackMinionStatic),
    BLUE_2_BACK(new Vector(20, 2, 5), BLUE_MATERIAL, false, true, true, Spot::blue2BackMinionStatic, Spot::setBlue2BackMinionStatic),
    BLUE_2_FRONT(new Vector(17, 1, 5), BLUE_MATERIAL, false, true, true, Spot::blue2FrontMinionStatic, Spot::setBlue2FrontMinionStatic),
    BLUE_1_FRONT(new Vector(9, 1, 5), BLUE_MATERIAL, true, true, true, Spot::blue1FrontMinionStatic, Spot::setBlue1FrontMinionStatic),
    BLUE_1_BACK(new Vector(6, 2,5), BLUE_MATERIAL, true, true, true, Spot::blue1BackMinionStatic, Spot::setBlue1BackMinionStatic),
    GREEN_2_BACK(new Vector(20, 2, 9), GREEN_MATERIAL, false, true, true, Spot::green2BackMinionStatic, Spot::setGreen2BackMinionStatic),
    GREEN_2_FRONT(new Vector(17, 1, 9), GREEN_MATERIAL, false, true, true, Spot::green2FrontMinionStatic, Spot::setGreen2FrontMinionStatic),
    GREEN_1_FRONT(new Vector(9, 1, 9), GREEN_MATERIAL, true, true, true, Spot::green1FrontMinionStatic, Spot::setGreen1FrontMinionStatic),
    GREEN_1_BACK(new Vector(6, 2, 9), GREEN_MATERIAL, true, true, true, Spot::green1BackMinionStatic, Spot::setGreen1BackMinionStatic),
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
    private final TriConsumer<Stadium, Minion, Boolean> minionSetRef;

    Spot(Vector offset, Material material, Boolean isPlayer1Spot, Boolean isSummonableSpot, Boolean isTargetable, Function<Stadium, Minion> minionRef, TriConsumer<Stadium, Minion, Boolean> minionSetRef) {
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

    public TriConsumer<Stadium, Minion, Boolean> minionSetRef() {
        return minionSetRef;
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

    public static Minion red2BackMinionStatic(Stadium stadium) {
        return stadium.red2BackMinion();
    }

    public static Minion red2FrontMinionStatic(Stadium stadium) {
        return stadium.red2FrontMinion();
    }

    public static Minion red1FrontMinionStatic(Stadium stadium) {
        return stadium.red1FrontMinion();
    }

    public static Minion red1BackMinionStatic(Stadium stadium) {
        return stadium.red1BackMinion();
    }

    public static Minion blue2BackMinionStatic(Stadium stadium) {
        return stadium.blue2BackMinion();
    }

    public static Minion blue2FrontMinionStatic(Stadium stadium) {
        return stadium.blue2FrontMinion();
    }

    public static Minion blue1FrontMinionStatic(Stadium stadium) {
        return stadium.blue1FrontMinion();
    }

    public static Minion blue1BackMinionStatic(Stadium stadium) {
        return stadium.blue1BackMinion();
    }

    public static Minion green2BackMinionStatic(Stadium stadium) {
        return stadium.green2BackMinion();
    }

    public static Minion green2FrontMinionStatic(Stadium stadium) {
        return stadium.green2FrontMinion();
    }

    public static Minion green1FrontMinionStatic(Stadium stadium) {
        return stadium.green1FrontMinion();
    }

    public static Minion green1BackMinionStatic(Stadium stadium) {
        return stadium.green1BackMinion();
    }

    public static Player player1Static(Stadium stadium) {
        return stadium.player1();
    }

    public static Player player2Static(Stadium stadium) {
        return stadium.player2();
    }

    public static void setRed2BackMinionStatic(Stadium stadium, Minion minion, boolean triggerOnEnter) {
        stadium.setRed2BackMinion(minion, triggerOnEnter);
    }

    public static void setRed2FrontMinionStatic(Stadium stadium, Minion minion, boolean triggerOnEnter) {
        stadium.setRed2FrontMinion(minion, triggerOnEnter);
    }

    public static void setRed1FrontMinionStatic(Stadium stadium, Minion minion, boolean triggerOnEnter) {
        stadium.setRed1FrontMinion(minion, triggerOnEnter);
    }

    public static void setRed1BackMinionStatic(Stadium stadium, Minion minion, boolean triggerOnEnter) {
        stadium.setRed1BackMinion(minion, triggerOnEnter);
    }

    public static void setBlue2BackMinionStatic(Stadium stadium, Minion minion, boolean triggerOnEnter) {
        stadium.setBlue2BackMinion(minion, triggerOnEnter);
    }

    public static void setBlue2FrontMinionStatic(Stadium stadium, Minion minion, boolean triggerOnEnter) {
        stadium.setBlue2FrontMinion(minion, triggerOnEnter);
    }

    public static void setBlue1FrontMinionStatic(Stadium stadium, Minion minion, boolean triggerOnEnter) {
        stadium.setBlue1FrontMinion(minion, triggerOnEnter);
    }

    public static void setBlue1BackMinionStatic(Stadium stadium, Minion minion, boolean triggerOnEnter) {
        stadium.setBlue1BackMinion(minion, triggerOnEnter);
    }

    public static void setGreen2BackMinionStatic(Stadium stadium, Minion minion, boolean triggerOnEnter) {
        stadium.setGreen2BackMinion(minion, triggerOnEnter);
    }

    public static void setGreen2FrontMinionStatic(Stadium stadium, Minion minion, boolean triggerOnEnter) {
        stadium.setGreen2FrontMinion(minion, triggerOnEnter);
    }

    public static void setGreen1FrontMinionStatic(Stadium stadium, Minion minion, boolean triggerOnEnter) {
        stadium.setGreen1FrontMinion(minion, triggerOnEnter);
    }

    public static void setGreen1BackMinionStatic(Stadium stadium, Minion minion, boolean triggerOnEnter) {
        stadium.setGreen1BackMinion(minion, triggerOnEnter);
    }
}
