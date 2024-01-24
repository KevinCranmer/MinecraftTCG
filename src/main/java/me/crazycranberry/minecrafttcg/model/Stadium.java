package me.crazycranberry.minecrafttcg.model;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.logger;
import static me.crazycranberry.minecrafttcg.managers.StadiumManager.PLAYER_1_SIGN_OFFSET;
import static me.crazycranberry.minecrafttcg.managers.StadiumManager.PLAYER_2_SIGN_OFFSET;
import static org.bukkit.ChatColor.DARK_GREEN;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.RESET;

public class Stadium {
    public static final Material RED_MATERIAL = Material.RED_TERRACOTTA;
    public static final Material BLUE_MATERIAL = Material.LIGHT_BLUE_TERRACOTTA;
    public static final Material GREEN_MATERIAL = Material.LIME_TERRACOTTA;
    private static final Material PLAYER_1_TARGET_MATERIAL = Material.LIME_CONCRETE;
    private static final Material PLAYER_2_TARGET_MATERIAL = Material.ORANGE_CONCRETE;
    private final Location startingCorner;
    private final Player player1;
    private final Player player2;
    private Minion redAMinion;
    private Minion redDMinion;
    private Minion red1Minion;
    private Minion red4Minion;
    private Minion blueBMinion;
    private Minion blueEMinion;
    private Minion blue2Minion;
    private Minion blue5Minion;
    private Minion greenCMinion;
    private Minion greenFMinion;
    private Minion green3Minion;
    private Minion green6Minion;
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
                    hideName(player1Target);
                }
                showName(target);
                startingCorner.clone().add(target.offset()).subtract(0, 1, 0).getBlock().setType(PLAYER_1_TARGET_MATERIAL);
                player1Target = target;
            }
        } else if (p.equals(player2)) {
            if (!target.equals(player2Target)) {
                if (player1Target != null && player1Target.equals(player2Target)) {
                    startingCorner.clone().add(player2Target.offset()).subtract(0, 1, 0).getBlock().setType(PLAYER_1_TARGET_MATERIAL);
                } else if (player2Target != null) {
                    startingCorner.clone().add(player2Target.offset()).subtract(0, 1, 0).getBlock().setType(player2Target.material());
                    hideName(player2Target);
                }
                showName(target);
                startingCorner.clone().add(target.offset()).subtract(0, 1, 0).getBlock().setType(PLAYER_2_TARGET_MATERIAL);
                player2Target = target;
            }
        }
    }

    private void showName(Spot spot) {
        if (spot.minionRef() != null && spot.minionRef().apply(this) != null) {
            Minion minion = spot.minionRef().apply(this);
            minion.minionInfo().entity().setCustomName(String.format("%s%s %s🗡%s:%s %s❤%s:%s/%s",
                spot.isPlayer1Spot() ? GREEN : GOLD, minion.cardDef().cardName(),
                DARK_GREEN, RESET, minion.strength(), RED, RESET, minion.health(), minion.maxHealth()
                ));
            minion.minionInfo().entity().setCustomNameVisible(true);
        }
    }

    private void hideName(Spot spot) {
        if (spot.minionRef() != null && spot.minionRef().apply(this) != null) {
            spot.minionRef().apply(this).minionInfo().entity().setCustomNameVisible(false);
        }
    }

    public void minionSummoned(Player p, MinionCardDefinition minionDef) {
        try {
            Constructor<? extends Minion> c = minionDef.minionClass().getConstructor(MinionInfo.class);
            c.setAccessible(true);
            Minion minion;
            if (p.equals(player1)) {
                LivingEntity entity = (LivingEntity) p.getWorld().spawnEntity(playerTargetLoc(player1), minionDef.minionType(), false);
                minion = c.newInstance(new MinionInfo(this, player1Target, entity, player1));
                player1Target.minionSetRef().accept(this, minion);
                showName(player1Target);
            } else {
                LivingEntity entity = (LivingEntity) p.getWorld().spawnEntity(playerTargetLoc(player2), minionDef.minionType(), false);
                minion = c.newInstance(new MinionInfo(this, player2Target, entity, player2));
                player2Target.minionSetRef().accept(this, minion);
                showName(player2Target);
            }
            minion.onEnter();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {
            logger().severe(String.format("Unable to create a %s\nException: %s\n%s", minionDef.minionClass(), ex.getClass().getSimpleName(), ex.getMessage()));
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
            return player1Target.minionRef().apply(this) == null;
        } else {
            return player2Target.minionRef().apply(this) == null;
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

    public Optional<Minion> minionFromEntity(LivingEntity entity) {
        for (Spot spot : Spot.values()) {
            if (spot.minionRef() != null) {
                Minion minion = spot.minionRef().apply(this);
                if (minion != null && entity.equals(minion.minionInfo().entity())) {
                    System.out.println("Found the minion!");
                    return Optional.of(minion);
                }
            }
        }
        System.out.println("Could not find the minion");
        return Optional.empty();
    }

    public void displayDescription(Player player) {
        Spot targetedSpot = player2Target;
        Vector offset = PLAYER_2_SIGN_OFFSET;
        if (player.equals(player1)) {
            targetedSpot = player1Target;
            offset = PLAYER_1_SIGN_OFFSET;
        }
        ChatColor minionNameColor = targetedSpot.isPlayer1Spot() ? GREEN : GOLD;
        if (targetedSpot.minionRef() == null || targetedSpot.minionRef().apply(this) == null) {
            return;
        }
        Minion minion = targetedSpot.minionRef().apply(this);
        Sign sign1 = (Sign) startingCorner.getBlock().getRelative((int) offset.getX(), (int) offset.getY()-1, (int) offset.getZ()).getState();
        Sign sign2 = (Sign) startingCorner.getBlock().getRelative((int) offset.getX(), (int) offset.getY()-2, (int) offset.getZ()).getState();
        sign1.getSide(Side.FRONT).setLine(0, String.format("%s%s%s", minionNameColor, minion.cardDef().cardName(), RESET));
        List<String> lines = List.of(minion.cardDef().cardDescription().replace("\n", "").split("\\$"));
        for (int i = 0; i < lines.size(); i++) {
            int lineIndex = i + 1;
            Sign sign = lineIndex > 3 ? sign2 : sign1;
            sign.getSide(Side.FRONT).setLine(lineIndex % 4, lines.get(i));
        }
        sign1.update();
        sign2.update();
    }

    public Minion redAMinion() {
        return redAMinion;
    }

    public Minion redDMinion() {
        return redDMinion;
    }

    public Minion red1Minion() {
        return red1Minion;
    }

    public Minion red4Minion() {
        return red4Minion;
    }

    public Minion blueBMinion() {
        return blueBMinion;
    }

    public Minion blueEMinion() {
        return blueEMinion;
    }

    public Minion blue2Minion() {
        return blue2Minion;
    }

    public Minion blue5Minion() {
        return blue5Minion;
    }

    public Minion greenCMinion() {
        return greenCMinion;
    }

    public Minion greenFMinion() {
        return greenFMinion;
    }

    public Minion green3Minion() {
        return green3Minion;
    }

    public Minion green6Minion() {
        return green6Minion;
    }

    public Player player1() {
        return player1;
    }

    public Player player2() {
        return player2;
    }

    public void setRedAMinion(Minion minion) {
        redAMinion = minion;
    }

    public void setRedDMinion(Minion minion) {
        redDMinion = minion;
    }

    public void setRed1Minion(Minion minion) {
        red1Minion = minion;
    }

    public void setRed4Minion(Minion minion) {
        red4Minion = minion;
    }

    public void setBlueBMinion(Minion minion) {
        blueBMinion = minion;
    }

    public void setBlueEMinion(Minion minion) {
        blueEMinion = minion;
    }

    public void setBlue2Minion(Minion minion) {
        blue2Minion = minion;
    }

    public void setBlue5Minion(Minion minion) {
        blue5Minion = minion;
    }

    public void setGreenCMinion(Minion minion) {
        greenCMinion = minion;
    }

    public void setGreenFMinion(Minion minion) {
        greenFMinion = minion;
    }

    public void setGreen3Minion(Minion minion) {
        green3Minion = minion;
    }

    public void setGreen6Minion(Minion minion) {
        green6Minion = minion;
    }
}
