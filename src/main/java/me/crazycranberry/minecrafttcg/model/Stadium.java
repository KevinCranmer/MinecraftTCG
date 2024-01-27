package me.crazycranberry.minecrafttcg.model;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.events.CombatEndEvent;
import me.crazycranberry.minecrafttcg.managers.StadiumManager;
import org.bukkit.Bukkit;
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
import static me.crazycranberry.minecrafttcg.model.Spot.BLUE_1_BACK;
import static me.crazycranberry.minecrafttcg.model.Spot.BLUE_1_FRONT;
import static me.crazycranberry.minecrafttcg.model.Spot.BLUE_2_BACK;
import static me.crazycranberry.minecrafttcg.model.Spot.BLUE_2_FRONT;
import static me.crazycranberry.minecrafttcg.model.Spot.GREEN_1_BACK;
import static me.crazycranberry.minecrafttcg.model.Spot.GREEN_1_FRONT;
import static me.crazycranberry.minecrafttcg.model.Spot.GREEN_2_BACK;
import static me.crazycranberry.minecrafttcg.model.Spot.GREEN_2_FRONT;
import static me.crazycranberry.minecrafttcg.model.Spot.PLAYER_1_OUTLOOK;
import static me.crazycranberry.minecrafttcg.model.Spot.PLAYER_2_OUTLOOK;
import static me.crazycranberry.minecrafttcg.model.Spot.RED_1_BACK;
import static me.crazycranberry.minecrafttcg.model.Spot.RED_1_FRONT;
import static me.crazycranberry.minecrafttcg.model.Spot.RED_2_BACK;
import static me.crazycranberry.minecrafttcg.model.Spot.RED_2_FRONT;
import static me.crazycranberry.minecrafttcg.model.TurnPhase.COMBAT_PHASE;
import static me.crazycranberry.minecrafttcg.model.TurnPhase.FIRST_PRECOMBAT_PHASE;
import static me.crazycranberry.minecrafttcg.model.TurnPhase.POST_COMBAT_CLEANUP;
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
    private int player1Mana = 0;
    private int player2Mana = 0;
    private int player1PendingDamage = 0;
    private int player2PendingDamage = 0;
    private int turn = 0;
    private TurnPhase phase;
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
    private final LivingEntity player1RedChicken;
    private final LivingEntity player1BlueChicken;
    private final LivingEntity player1GreenChicken;
    private final LivingEntity player2RedChicken;
    private final LivingEntity player2BlueChicken;
    private final LivingEntity player2GreenChicken;
    private Spot player1Target;
    private Spot player2Target;

    public Stadium(Location startingCorner, Player player1, Player player2, LivingEntity player1RedChicken, LivingEntity player1BlueChicken, LivingEntity player1GreenChicken, LivingEntity player2RedChicken, LivingEntity player2BlueChicken, LivingEntity player2GreenChicken) {
        this.startingCorner = startingCorner;
        this.player1 = player1;
        this.player2 = player2;
        this.player1RedChicken = player1RedChicken;
        this.player1BlueChicken = player1BlueChicken;
        this.player1GreenChicken = player1GreenChicken;
        this.player2RedChicken = player2RedChicken;
        this.player2BlueChicken = player2BlueChicken;
        this.player2GreenChicken = player2GreenChicken;
    }

    public void updatePhase(TurnPhase turnPhase) {
        if (turnPhase == FIRST_PRECOMBAT_PHASE) {
            turn++;
            player1Mana = Math.min(10, turn);
            player2Mana = Math.min(10, turn);
            StadiumManager.updateManaForANewTurn(this, turn);
        } else if (phase.ordinal() + 1 != turnPhase.ordinal()) {
            System.out.println("What the hell, the turn phases are out of order!!!");
        }
        phase = turnPhase;

        // In the event that nothing is on the board able to attack, we should just skip combat
        if (phase.equals(COMBAT_PHASE)) {
            doneAttacking();
        } else if (phase.equals(POST_COMBAT_CLEANUP)) {
            if (player1.getHealth() < player1PendingDamage && player2.getHealth() < player2PendingDamage) {
                System.out.println("It's a tie!!");
            } else {
                if (player1PendingDamage > 0) {
                    player1.damage(player1PendingDamage);
                }
                player1PendingDamage = 0;
                if (player2PendingDamage > 0) {
                    player2.damage(player2PendingDamage);
                }
                player2PendingDamage = 0;
            }
        }
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

    public void updateCustomName(Minion minion) {
        minion.minionInfo().entity().setCustomName(String.format("%s%s %s%s%s:%s %s❤%s:%s/%s",
            minion.minionInfo().spot().isPlayer1Spot() ? GREEN : GOLD, minion.cardDef().cardName(),
            DARK_GREEN, minion.cardDef().isRanged() ? "\uD83C\uDFF9" : "🗡", RESET, minion.strength(), RED, RESET, minion.health(), minion.maxHealth()
        ));
    }

    public void showName(Spot spot) {
        if (spot.minionRef() != null && spot.minionRef().apply(this) != null) {
            Minion minion = spot.minionRef().apply(this);
            updateCustomName(minion);
            minion.minionInfo().entity().setCustomNameVisible(true);
        }
    }

    private void hideName(Spot spot) {
        if (spot.minionRef() != null && spot.minionRef().apply(this) != null) {
            spot.minionRef().apply(this).minionInfo().entity().setCustomNameVisible(false);
        }
    }

    public void pendingDamageForPlayer(Player p, Integer damage) {
        if (p.equals(player1)) {
            player1PendingDamage += damage;
        } else {
            player2PendingDamage += damage;
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

    public Minion targetedMinion(Player p) {
        if (p.equals(player1)) {
            return player1Target.equals(PLAYER_2_OUTLOOK) ? null : player1Target.minionRef().apply(this);
        }
        else {
            return player2Target.equals(PLAYER_1_OUTLOOK) ? null : player2Target.minionRef().apply(this);
        }
    }

    public List<Spot> targetedRow(Player p) {
        Spot target = p.equals(player1) ? player1Target : player2Target;
        return switch (target) {
            case RED_1_FRONT, BLUE_1_FRONT, GREEN_1_FRONT -> List.of(RED_1_FRONT, BLUE_1_FRONT, GREEN_1_FRONT);
            case RED_1_BACK, BLUE_1_BACK, GREEN_1_BACK -> List.of(RED_1_BACK, BLUE_1_BACK, GREEN_1_BACK);
            case RED_2_FRONT, BLUE_2_FRONT, GREEN_2_FRONT -> List.of(RED_2_FRONT, BLUE_2_FRONT, GREEN_2_FRONT);
            case RED_2_BACK, BLUE_2_BACK, GREEN_2_BACK -> List.of(RED_2_BACK, BLUE_2_BACK, GREEN_2_BACK);
            default -> null;
        };
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

    public Spot playerTargetSpot(Player p) {
        return p.equals(player1) ? player1Target : player2Target;
    }

    public LivingEntity getTargetInFront(Minion minion) {
        Minion opposingMinion = Spot.opposingFrontRankSpot(minion.minionInfo().spot()).minionRef().apply(this);
        if (opposingMinion == null) {
            opposingMinion = Spot.opposingBackRankSpot(minion.minionInfo().spot()).minionRef().apply(this);
            if (opposingMinion == null) {
                return Spot.opposingChicken(minion.minionInfo().spot(), this);
            }
        }
        return opposingMinion.minionInfo().entity();
    }

    public Optional<Minion> minionFromEntity(LivingEntity entity) {
        for (Spot spot : Spot.values()) {
            if (spot.minionRef() != null) {
                Minion minion = spot.minionRef().apply(this);
                if (minion != null && entity.equals(minion.minionInfo().entity())) {
                    return Optional.of(minion);
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Player> getPlayerFromChicken(LivingEntity entity) {
        if (entity.equals(player1RedChicken) || entity.equals(player1BlueChicken) || entity.equals(player1GreenChicken)) {
            return Optional.of(player1);
        } else if (entity.equals(player2RedChicken) || entity.equals(player2BlueChicken) || entity.equals(player2GreenChicken)) {
            return Optional.of(player2);
        }
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
            if (targetedSpot.equals(PLAYER_1_OUTLOOK) || targetedSpot.equals(Spot.PLAYER_2_OUTLOOK)) {
                Sign sign = (Sign) startingCorner.getBlock().getRelative((int) offset.getX(), (int) offset.getY()-1, (int) offset.getZ()).getState();
                Player targetedPlayer = player.equals(player1) ? player2 : player1;
                sign.getSide(Side.FRONT).setLine(0, targetedPlayer.getName());
                sign.getSide(Side.FRONT).setLine(1, "❤: " + targetedPlayer.getHealth());
                sign.getSide(Side.FRONT).setLine(2, "");
                sign.getSide(Side.FRONT).setLine(3, "");
                sign.update();
            }
            return;
        }
        Minion minion = targetedSpot.minionRef().apply(this);
        Sign sign1 = (Sign) startingCorner.getBlock().getRelative((int) offset.getX(), (int) offset.getY()-1, (int) offset.getZ()).getState();
        Sign sign2 = (Sign) startingCorner.getBlock().getRelative((int) offset.getX(), (int) offset.getY()-2, (int) offset.getZ()).getState();
        sign1.getSide(Side.FRONT).setLine(0, String.format("%s%s%s", minionNameColor, minion.cardDef().cardName(), RESET));
        sign1.getSide(Side.FRONT).setLine(1, String.format("%s%s%s:%s %s❤%s:%s/%s\n", DARK_GREEN, minion.cardDef().isRanged() ? "\uD83C\uDFF9" : "🗡", RESET, minion.cardDef().strength(), RED, RESET, minion.cardDef().maxHealth(), minion.cardDef().maxHealth()));
        List<String> lines = List.of(minion.cardDef().signDescription().split("\n"));
        for (int i = 0; i < 6; i++) {
            int lineIndex = i + 2;
            Sign sign = lineIndex > 3 ? sign2 : sign1;
            if (i > lines.size()) {
                sign.getSide(Side.FRONT).setLine(lineIndex % 4, "");
            } else {
                sign.getSide(Side.FRONT).setLine(lineIndex % 4, lines.get(i));
            }
        }
        sign1.update();
        sign2.update();
    }

    public void doneAttacking() {
        if (phase != COMBAT_PHASE) {
            System.out.println("Who the hell is calling doneAttacking outside of combat >:( ");
        }
        boolean everyoneDone = true;
        for (Spot spot : Spot.values()) {
            if (spot.isSummonableSpot()) {
                Minion minion = spot.minionRef().apply(this);
                if (minion != null && minion.attacksLeft() > 0 && !(!minion.cardDef().isRanged() && hasAllyMinionInFront(spot))) {
                    everyoneDone = false;
                }
            }
        }
        if (everyoneDone) {
            Bukkit.getPluginManager().callEvent(new CombatEndEvent(this));
        }
    }

    public boolean hasAllyMinionInFront(Spot spot) {
        return switch (spot) {
            case RED_2_BACK -> RED_2_FRONT.minionRef().apply(this) != null;
            case RED_1_BACK -> RED_1_FRONT.minionRef().apply(this) != null;
            case BLUE_2_BACK -> BLUE_2_FRONT.minionRef().apply(this) != null;
            case BLUE_1_BACK -> BLUE_1_FRONT.minionRef().apply(this) != null;
            case GREEN_2_BACK -> GREEN_2_FRONT.minionRef().apply(this) != null;
            case GREEN_1_BACK -> GREEN_1_FRONT.minionRef().apply(this) != null;
            default -> false;
        };
    }

    public void minionDied(Spot spot) {
        spot.minionSetRef().accept(this, null);
    }

    public int turn() {
        return turn;
    }

    public TurnPhase phase() {
        return phase;
    }

    public int playerMana(Player p) {
        return p.equals(player1) ? player1Mana : player2Mana;
    }

    public void reduceMana(Player caster, Integer cost) {
        if (caster.equals(player1)) {
            player1Mana = Math.max(0, player1Mana - cost);
            StadiumManager.reduceMana(this, player1Mana, turn, true);
        } else {
            player2Mana = Math.max(0, player2Mana - cost);
            StadiumManager.reduceMana(this, player2Mana, turn, false);
        }
    }

    public Location startingCorner() {
        return startingCorner;
    }

    public boolean isPlayersTurn(Player p) {
        if (p.equals(player1)) {
            switch (phase) {
                case FIRST_PRECOMBAT_PHASE:
                case SECOND_POSTCOMBAT_PHASE:
                    return turn % 2 == 1;
                case SECOND_PRECOMBAT_PHASE:
                case FIRST_POSTCOMBAT_PHASE:
                    return turn % 2 == 0;
                default:
                    return false;
            }
        } else {
            switch (phase) {
                case FIRST_PRECOMBAT_PHASE:
                case SECOND_POSTCOMBAT_PHASE:
                    return turn % 2 == 0;
                case SECOND_PRECOMBAT_PHASE:
                case FIRST_POSTCOMBAT_PHASE:
                    return turn % 2 == 1;
                default:
                    return false;
            }
        }
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

    public LivingEntity player1RedChicken() {
        return player1RedChicken;
    }

    public LivingEntity player1BlueChicken() {
        return player1BlueChicken;
    }

    public LivingEntity player1GreenChicken() {
        return player1GreenChicken;
    }

    public LivingEntity player2RedChicken() {
        return player2RedChicken;
    }

    public LivingEntity player2BlueChicken() {
        return player2BlueChicken;
    }

    public LivingEntity player2GreenChicken() {
        return player2GreenChicken;
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
