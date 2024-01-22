package me.crazycranberry.minecrafttcg.model;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;

public class Stadium {
    public static final Material RED_MATERIAL = Material.RED_TERRACOTTA;
    public static final Material BLUE_MATERIAL = Material.LIGHT_BLUE_TERRACOTTA;
    public static final Material GREEN_MATERIAL = Material.LIME_TERRACOTTA;
    private static final Material PLAYER_1_TARGET_MATERIAL = Material.LIME_CONCRETE;
    private static final Material PLAYER_2_TARGET_MATERIAL = Material.ORANGE_CONCRETE;
    private final Location startingCorner;
    private final Player player1;
    private final Player player2;
    private LivingEntity redAEntity;
    private LivingEntity redDEntity;
    private LivingEntity red1Entity;
    private LivingEntity red4Entity;
    private LivingEntity blueBEntity;
    private LivingEntity blueEEntity;
    private LivingEntity blue2Entity;
    private LivingEntity blue5Entity;
    private LivingEntity greenCEntity;
    private LivingEntity greenFEntity;
    private LivingEntity green3Entity;
    private LivingEntity green6Entity;
    private Spot player1Target;
    private Spot player2Target;

    public Stadium(Location startingCorner, Player player1, Player player2) {
        this.startingCorner = startingCorner;
        this.player1 = player1;
        this.player2 = player2;
    }

    public void playerTargeting(Player p, Spot target) {
        if (p.equals(player1)) {
            if (!target.equals(player1Target)) {
                if (player2Target != null && player2Target.equals(player1Target)) {
                    startingCorner.clone().add(player1Target.offset()).subtract(0, 1, 0).getBlock().setType(PLAYER_2_TARGET_MATERIAL);
                } else if (player1Target != null) {
                    startingCorner.clone().add(player1Target.offset()).subtract(0, 1, 0).getBlock().setType(player1Target.material());
                }
                startingCorner.clone().add(target.offset()).subtract(0, 1, 0).getBlock().setType(PLAYER_1_TARGET_MATERIAL);
                player1Target = target;
            }
        } else if (p.equals(player2)) {
            if (!target.equals(player2Target)) {
                if (player1Target != null && player1Target.equals(player2Target)) {
                    startingCorner.clone().add(player2Target.offset()).subtract(0, 1, 0).getBlock().setType(PLAYER_1_TARGET_MATERIAL);
                } else if (player2Target != null) {
                    startingCorner.clone().add(player2Target.offset()).subtract(0, 1, 0).getBlock().setType(player2Target.material());
                }
                startingCorner.clone().add(target.offset()).subtract(0, 1, 0).getBlock().setType(PLAYER_2_TARGET_MATERIAL);
                player2Target = target;
            }
        }
    }

    public void minionSummoned(Player p, LivingEntity minion) {
        if (p.equals(player1)) {
            player1Target.entitySetRef().accept(this, minion);
        } else {
            player2Target.entitySetRef().accept(this, minion);
        }
    }

    public Location locOfSpot(Spot spot) {
        return startingCorner.clone().add(spot.offset());
    }

    public Location playerTargetLoc(Player p) {
        if (p.equals(player1)) {
            return startingCorner.clone().add(player1Target.offset());
        } else {
            return startingCorner.clone().add(player2Target.offset());
        }
    }

    public boolean isPlayerParticipating(Player p) {
        return p.equals(player1) || p.equals(player2);
    }

    public boolean isPlayersTargetAvailable(Player p) {
        if (p.equals(player1)) {
            return player1Target.entityRef().apply(this) == null;
        } else {
            return player2Target.entityRef().apply(this) == null;
        }
    }

    public boolean isPlayerTargetingTheirOwnSpots(Player p) {
        if (p.equals(player1)) {
            return player1Target.isPlayer1Spot();
        } else {
            return !player2Target.isPlayer1Spot();
        }
    }

    public boolean isPlayerTargetingSummonableSpot(Player p) {
        if (p.equals(player1)) {
            return player1Target.isSummonableSpot();
        } else {
            return player2Target.isSummonableSpot();
        }
    }

    public LivingEntity redAEntity() {
        return redAEntity;
    }

    public LivingEntity redDEntity() {
        return redDEntity;
    }

    public LivingEntity red1Entity() {
        return red1Entity;
    }

    public LivingEntity red4Entity() {
        return red4Entity;
    }

    public LivingEntity blueBEntity() {
        return blueBEntity;
    }

    public LivingEntity blueEEntity() {
        return blueEEntity;
    }

    public LivingEntity blue2Entity() {
        return blue2Entity;
    }

    public LivingEntity blue5Entity() {
        return blue5Entity;
    }

    public LivingEntity greenCEntity() {
        return greenCEntity;
    }

    public LivingEntity greenFEntity() {
        return greenFEntity;
    }

    public LivingEntity green3Entity() {
        return green3Entity;
    }

    public LivingEntity green6Entity() {
        return green6Entity;
    }

    public LivingEntity player1() {
        return player1;
    }

    public LivingEntity player2() {
        return player2;
    }

    public void setRedAEntity(LivingEntity entity) {
        redAEntity = entity;
    }

    public void setRedDEntity(LivingEntity entity) {
        redDEntity = entity;
    }

    public void setRed1Entity(LivingEntity entity) {
        red1Entity = entity;
    }

    public void setRed4Entity(LivingEntity entity) {
        red4Entity = entity;
    }

    public void setBlueBEntity(LivingEntity entity) {
        blueBEntity = entity;
    }

    public void setBlueEEntity(LivingEntity entity) {
        blueEEntity = entity;
    }

    public void setBlue2Entity(LivingEntity entity) {
        blue2Entity = entity;
    }

    public void setBlue5Entity(LivingEntity entity) {
        blue5Entity = entity;
    }

    public void setGreenCEntity(LivingEntity entity) {
        greenCEntity = entity;
    }

    public void setGreenFEntity(LivingEntity entity) {
        greenFEntity = entity;
    }

    public void setGreen3Entity(LivingEntity entity) {
        green3Entity = entity;
    }

    public void setGreen6Entity(LivingEntity entity) {
        green6Entity = entity;
    }

    public static LivingEntity redAEntityStatic(Stadium stadium) {
        return stadium.redAEntity();
    }

    public static LivingEntity redDEntityStatic(Stadium stadium) {
        return stadium.redDEntity();
    }

    public static LivingEntity red1EntityStatic(Stadium stadium) {
        return stadium.red1Entity();
    }

    public static LivingEntity red4EntityStatic(Stadium stadium) {
        return stadium.red4Entity();
    }

    public static LivingEntity blueBEntityStatic(Stadium stadium) {
        return stadium.blueBEntity();
    }

    public static LivingEntity blueEEntityStatic(Stadium stadium) {
        return stadium.blueEEntity();
    }

    public static LivingEntity blue2EntityStatic(Stadium stadium) {
        return stadium.blue2Entity();
    }

    public static LivingEntity blue5EntityStatic(Stadium stadium) {
        return stadium.blue5Entity();
    }

    public static LivingEntity greenCEntityStatic(Stadium stadium) {
        return stadium.greenCEntity();
    }

    public static LivingEntity greenFEntityStatic(Stadium stadium) {
        return stadium.greenFEntity();
    }

    public static LivingEntity green3EntityStatic(Stadium stadium) {
        return stadium.green3Entity();
    }

    public static LivingEntity green6EntityStatic(Stadium stadium) {
        return stadium.green6Entity();
    }

    public static LivingEntity player1Static(Stadium stadium) {
        return stadium.player1();
    }

    public static LivingEntity player2Static(Stadium stadium) {
        return stadium.player2();
    }

    public static void setRedAEntityStatic(Stadium stadium, LivingEntity entity) {
        stadium.setRedAEntity(entity);
    }

    public static void setRedDEntityStatic(Stadium stadium, LivingEntity entity) {
        stadium.setRedDEntity(entity);
    }

    public static void setRed1EntityStatic(Stadium stadium, LivingEntity entity) {
        stadium.setRed1Entity(entity);
    }

    public static void setRed4EntityStatic(Stadium stadium, LivingEntity entity) {
        stadium.setRed4Entity(entity);
    }

    public static void setBlueBEntityStatic(Stadium stadium, LivingEntity entity) {
        stadium.setBlueBEntity(entity);
    }

    public static void setBlueEEntityStatic(Stadium stadium, LivingEntity entity) {
        stadium.setBlueEEntity(entity);
    }

    public static void setBlue2EntityStatic(Stadium stadium, LivingEntity entity) {
        stadium.setBlue2Entity(entity);
    }

    public static void setBlue5EntityStatic(Stadium stadium, LivingEntity entity) {
        stadium.setBlue5Entity(entity);
    }

    public static void setGreenCEntityStatic(Stadium stadium, LivingEntity entity) {
        stadium.setGreenCEntity(entity);
    }

    public static void setGreenFEntityStatic(Stadium stadium, LivingEntity entity) {
        stadium.setGreenFEntity(entity);
    }

    public static void setGreen3EntityStatic(Stadium stadium, LivingEntity entity) {
        stadium.setGreen3Entity(entity);
    }

    public static void setGreen6EntityStatic(Stadium stadium, LivingEntity entity) {
        stadium.setGreen6Entity(entity);
    }
}
