package me.crazycranberry.minecrafttcg.model;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.events.CombatEndEvent;
import me.crazycranberry.minecrafttcg.events.DuelEndEvent;
import me.crazycranberry.minecrafttcg.managers.StadiumManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.carddefinitions.Card.IS_CARD_KEY;
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
import static org.bukkit.Color.BLACK;

public class Stadium {
    public static final Material RED_MATERIAL = Material.RED_TERRACOTTA;
    public static final Material BLUE_MATERIAL = Material.LIGHT_BLUE_TERRACOTTA;
    public static final Material GREEN_MATERIAL = Material.LIME_TERRACOTTA;
    private static final Material PLAYER_1_TARGET_MATERIAL = Material.LIME_CONCRETE;
    private static final Material PLAYER_2_TARGET_MATERIAL = Material.ORANGE_CONCRETE;
    private static final int MAX_CARDS_IN_HAND = 9;
    private final Location startingCorner;
    private final Boolean isRanked;
    private final Player player1;
    private final Player player2;
    private final int currentPlayerTurnParticlesTaskId;
    private int player1Mana = 0;
    private int player2Mana = 0;
    private int player1PendingDamage = 0;
    private int player2PendingDamage = 0;
    private final Deck player1Deck;
    private final Deck player2Deck;
    private int player1MillDamage = 1;
    private int player2MillDamage = 1;
    public int turn = 0; //TODO: MAKE NOT PUBLIC
    private TurnPhase phase;
    private Minion red2BackMinion;
    private Minion red2FrontMinion;
    private Minion red1FrontMinion;
    private Minion red1BackMinion;
    private Minion blue2BackMinion;
    private Minion blue2FrontMinion;
    private Minion blue1FrontMinion;
    private Minion blue1BackMinion;
    private Minion green2BackMinion;
    private Minion green2FrontMinion;
    private Minion green1FrontMinion;
    private Minion green1BackMinion;
    private LivingEntity player1RedChicken;
    private LivingEntity player1BlueChicken;
    private LivingEntity player1GreenChicken;
    private LivingEntity player2RedChicken;
    private LivingEntity player2BlueChicken;
    private LivingEntity player2GreenChicken;
    private Spot player1Target;
    private Spot player2Target;
    private int fireworksLeft = 10;
    private int fireworkTaskId;
    private int fireworkInterval = 10;
    private int totalFireworkDuration = (fireworksLeft + 1) * fireworkInterval;
    private boolean duelDone = false;

    public Stadium(Location startingCorner, Player player1, Deck player1Deck, Player player2, Deck player2Deck, Boolean ranked) {
        this.startingCorner = startingCorner;
        this.player1 = player1;
        this.player1Deck = player1Deck;
        this.player2 = player2;
        this.player2Deck = player2Deck;
        this.isRanked = ranked;
        this.currentPlayerTurnParticlesTaskId = Bukkit.getScheduler().runTaskTimer(getPlugin(), this::generateParticlesForCurrentPlayersTurn, 0 /*<-- the initial delay */, 8 /*<-- the interval */).getTaskId();
    }

    public void setChickens(LivingEntity player1RedChicken, LivingEntity player1BlueChicken, LivingEntity player1GreenChicken, LivingEntity player2RedChicken, LivingEntity player2BlueChicken, LivingEntity player2GreenChicken) {
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
            // Is it a tie?
            if (player1.getHealth() <= player1PendingDamage && player2.getHealth() <= player2PendingDamage) {
                Bukkit.getPluginManager().callEvent(new DuelEndEvent(player1, true));
            }
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

    public void draw(Player p) {
        if (p.equals(player1)) {
            Optional<ItemStack> cardDrawn = player1Deck.draw();
            if (cardDrawn.isEmpty()) {
                sendMessageToBothPlayers(String.format("%s%s%s has attempted to draw from an empty deck. Taking %s%s%s damage.", GREEN, p.getName(), RESET, RED, player1MillDamage, RESET));
                player1.damage(player1MillDamage);
                player1MillDamage++;
            } else if(numCardsInHand(player1) >= MAX_CARDS_IN_HAND) {
                sendMessageToBothPlayers(String.format("%s%s%s has attempted to draw while already having %s cards. [%s%s] has been sent to the void.", GREEN, p.getName(), RESET, numCardsInHand(player1), cardDrawn.get().getItemMeta().getDisplayName(), RESET));
            } else {
                p.getInventory().addItem(cardDrawn.get());
            }
        } else {
            Optional<ItemStack> cardDrawn = player2Deck.draw();
            if (cardDrawn.isEmpty()) {
                sendMessageToBothPlayers(String.format("%s%s%s has attempted to draw from an empty deck. Taking %s%s%s damage.", GOLD, p.getName(), RESET, RED, player2MillDamage, RESET));
                player2.damage(player2MillDamage);
                player2MillDamage++;
            } else if(numCardsInHand(player2) >= MAX_CARDS_IN_HAND) {
                sendMessageToBothPlayers(String.format("%s%s%s has attempted to draw while already having %s cards. [%s%s] has been sent to the void.", GOLD, p.getName(), RESET, numCardsInHand(player2), cardDrawn.get().getItemMeta().getDisplayName(), RESET));
            } else {
                p.getInventory().addItem(cardDrawn.get());
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
            return player1Target.equals(PLAYER_2_OUTLOOK) || player1Target.equals(PLAYER_1_OUTLOOK) ? null : player1Target.minionRef().apply(this);
        }
        else {
            return player2Target.equals(PLAYER_1_OUTLOOK) || player2Target.equals(PLAYER_2_OUTLOOK) ? null : player2Target.minionRef().apply(this);
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

    public List<Spot> adjacentSpots(Spot spot) {
        return switch (spot) {
            case RED_1_FRONT, GREEN_1_FRONT -> List.of(BLUE_1_FRONT);
            case BLUE_1_FRONT -> List.of(RED_1_FRONT, GREEN_1_FRONT);
            case RED_2_FRONT, GREEN_2_FRONT -> List.of(BLUE_2_FRONT);
            case BLUE_2_FRONT -> List.of(RED_2_FRONT, GREEN_2_FRONT);
            case RED_1_BACK, GREEN_1_BACK -> List.of(BLUE_1_BACK);
            case BLUE_1_BACK -> List.of(RED_1_BACK, GREEN_1_BACK);
            case RED_2_BACK, GREEN_2_BACK -> List.of(BLUE_2_BACK);
            case BLUE_2_BACK -> List.of(RED_2_BACK, GREEN_2_BACK);
            default -> List.of();
        };
    }

    public List<Spot> allyMinionSpots(Player p) {
        return p.equals(player1) ? List.of(RED_1_FRONT, RED_1_BACK, BLUE_1_FRONT, BLUE_1_BACK, GREEN_1_FRONT, GREEN_1_BACK) : List.of(RED_2_FRONT, RED_2_BACK, BLUE_2_FRONT, BLUE_2_BACK, GREEN_2_FRONT, GREEN_2_BACK);
    }

    public List<Spot> enemyMinionSpots(Player p) {
        return p.equals(player2) ? List.of(RED_1_FRONT, RED_1_BACK, BLUE_1_FRONT, BLUE_1_BACK, GREEN_1_FRONT, GREEN_1_BACK) : List.of(RED_2_FRONT, RED_2_BACK, BLUE_2_FRONT, BLUE_2_BACK, GREEN_2_FRONT, GREEN_2_BACK);
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

    public boolean isDuelDone() {
        return duelDone;
    }

    public void duelEnded(Player winner, Boolean isTie) {
        Bukkit.getScheduler().cancelTask(currentPlayerTurnParticlesTaskId);
        duelDone = true;
        fireworkTaskId = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
            int numFireworksAtATime = (int) (Math.random() * 4);
            for (int i = 0; i < numFireworksAtATime; i++) {
                double xOffset = Math.random() * 26;
                double yOffset = 8 + (Math.random() * 5);
                double zOffset = Math.random() * 11;
                Firework fw = (Firework) startingCorner.getWorld().spawnEntity(startingCorner.clone().add(new Vector(xOffset, yOffset, zOffset)), EntityType.FIREWORK);
                FireworkMeta fwm = fw.getFireworkMeta();
                fwm.setPower(1);
                fwm.addEffect(FireworkEffect.builder().withColor(isTie ? BLACK : (winner.equals(player1) ? Color.GREEN : Color.ORANGE)).flicker(true).build());
                fw.setFireworkMeta(fwm);
                fw.setMaxLife(10);
            }
            fireworksLeft--;
            if (fireworksLeft <= 0) {
                Bukkit.getScheduler().cancelTask(fireworkTaskId);
            }
        }, 5 /*<-- the initial delay */, fireworkInterval /*<-- the interval */).getTaskId();
    }

    public int fireworkDuration() {
        return totalFireworkDuration;
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
                Sign sign1 = (Sign) startingCorner.getBlock().getRelative((int) offset.getX(), (int) offset.getY()-1, (int) offset.getZ()).getState();
                Sign sign2 = (Sign) startingCorner.getBlock().getRelative((int) offset.getX(), (int) offset.getY()-2, (int) offset.getZ()).getState();
                Player targetedPlayer = player.equals(player1) ? player2 : player1;
                System.out.println("Players health " + targetedPlayer.getName() + targetedPlayer.getHealth());
                sign1.getSide(Side.FRONT).setLine(0, targetedPlayer.getName());
                sign1.getSide(Side.FRONT).setLine(1, String.format("%s❤%s: %s", RED, RESET, targetedPlayer.getHealth()));
                sign1.getSide(Side.FRONT).setLine(2, String.format("Cards in Hand: %s", numCardsInHand(targetedPlayer)));
                sign1.getSide(Side.FRONT).setLine(3, "");
                sign2.getSide(Side.FRONT).setLine(0, "");
                sign2.getSide(Side.FRONT).setLine(1, "");
                sign2.getSide(Side.FRONT).setLine(2, "");
                sign2.getSide(Side.FRONT).setLine(3, "");
                sign1.update();
                sign2.update();
            }
            return;
        }
        Minion minion = targetedSpot.minionRef().apply(this);
        Sign sign1 = (Sign) startingCorner.getBlock().getRelative((int) offset.getX(), (int) offset.getY()-1, (int) offset.getZ()).getState();
        Sign sign2 = (Sign) startingCorner.getBlock().getRelative((int) offset.getX(), (int) offset.getY()-2, (int) offset.getZ()).getState();
        sign1.getSide(Side.FRONT).setLine(0, String.format("%s%s%s", minionNameColor, minion.cardDef().cardName(), RESET));
        sign1.getSide(Side.FRONT).setLine(1, String.format("%s%s%s:%s %s❤%s:%s/%s\n", DARK_GREEN, minion.cardDef().isRanged() ? "\uD83C\uDFF9" : "🗡", RESET, minion.strength(), RED, RESET, minion.health(), minion.maxHealth()));
        List<String> lines = List.of(minion.cardDef().signDescription().split("\n"));
        for (int i = 0; i < 6; i++) {
            int lineIndex = i + 2;
            Sign sign = lineIndex > 3 ? sign2 : sign1;
            if (i >= lines.size()) {
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
                    System.out.println(minion.cardDef().cardName() + " still has this many attacks left: " + minion.attacksLeft());
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

    public boolean isWalled(Minion minion) {
        // TODO: IMPLEMENT
        return false;
    }

    public boolean isRanked() {
        return isRanked;
    }

    public Integer numCardsInHand(Player p) {
        Inventory inv = p.equals(player1) ? player1.getInventory() : player2.getInventory();
        int cardCount = 0;
        for (ItemStack item : inv.getContents()) {
            if (item != null && Boolean.TRUE.equals(item.getItemMeta().getPersistentDataContainer().get(IS_CARD_KEY, PersistentDataType.BOOLEAN))) {
                cardCount++;
            }
        }
        return cardCount;
    }

    public Deck deck(Player p) {
        return p.equals(player1) ? player1Deck : player2Deck;
    }

    public void minionDied(Spot spot) {
        spot.minionSetRef().accept(this, null);
    }

    private void minionBeingSet(Minion minion) {
        if (minion == null) {
            // TODO: Might want to actually have a minion object here so that we can do like "When a zombie died..."
        } else {
            allyMinionSpots(minion.minionInfo().master()).stream()
                .filter(s -> !s.equals(minion.minionInfo().spot()))
                .map(Spot::minionRef)
                .filter(Objects::nonNull)
                .map(mr -> mr.apply(this))
                .filter(Objects::nonNull)
                .forEach(m -> m.onAllyMinionEntered(minion));
            enemyMinionSpots(minion.minionInfo().master()).stream()
                .filter(s -> !s.equals(minion.minionInfo().spot()))
                .map(Spot::minionRef)
                .filter(Objects::nonNull)
                .map(mr -> mr.apply(this))
                .filter(Objects::nonNull)
                .forEach(m -> m.onAllyMinionEntered(minion));
        }
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

    private void generateParticlesForCurrentPlayersTurn() {
        Player p = currentPlayersTurn();
        if (p != null) {
            p.getWorld().spawnParticle(Particle.WAX_OFF, p.getEyeLocation(), 2, 0.5, 0.75, 0.5);
        }
    }

    public Player currentPlayersTurn() {
        if (isPlayersTurn(player1)) {
            return player1;
        } else if (isPlayersTurn(player2)) {
            return player2;
        }
        return null;
    }

    public ChatColor playersColor(Player p) {
        return p.equals(player1) ? GREEN : GOLD;
    }

    public boolean isPlayersTurn(Player p) {
        if (phase == null) {
            return false;
        }
        if (p.equals(player1)) {
            return switch (phase) {
                case FIRST_PRECOMBAT_PHASE, SECOND_POSTCOMBAT_PHASE -> turn % 2 == 1;
                case SECOND_PRECOMBAT_PHASE, FIRST_POSTCOMBAT_PHASE -> turn % 2 == 0;
                default -> false;
            };
        } else {
            return switch (phase) {
                case FIRST_PRECOMBAT_PHASE, SECOND_POSTCOMBAT_PHASE -> turn % 2 == 0;
                case SECOND_PRECOMBAT_PHASE, FIRST_POSTCOMBAT_PHASE -> turn % 2 == 1;
                default -> false;
            };
        }
    }

    private void sendMessageToBothPlayers(String message) {
        player1.sendMessage(message);
        player2.sendMessage(message);
    }

    public Minion red2BackMinion() {
        return red2BackMinion;
    }

    public Minion red2FrontMinion() {
        return red2FrontMinion;
    }

    public Minion red1FrontMinion() {
        return red1FrontMinion;
    }

    public Minion red1BackMinion() {
        return red1BackMinion;
    }

    public Minion blue2BackMinion() {
        return blue2BackMinion;
    }

    public Minion blue2FrontMinion() {
        return blue2FrontMinion;
    }

    public Minion blue1FrontMinion() {
        return blue1FrontMinion;
    }

    public Minion blue1BackMinion() {
        return blue1BackMinion;
    }

    public Minion green2BackMinion() {
        return green2BackMinion;
    }

    public Minion green2FrontMinion() {
        return green2FrontMinion;
    }

    public Minion green1FrontMinion() {
        return green1FrontMinion;
    }

    public Minion green1BackMinion() {
        return green1BackMinion;
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

    public void setRed2BackMinion(Minion minion) {
        red2BackMinion = minion;
        minionBeingSet(minion);
    }

    public void setRed2FrontMinion(Minion minion) {
        red2FrontMinion = minion;
    }

    public void setRed1FrontMinion(Minion minion) {
        red1FrontMinion = minion;
    }

    public void setRed1BackMinion(Minion minion) {
        red1BackMinion = minion;
    }

    public void setBlue2BackMinion(Minion minion) {
        blue2BackMinion = minion;
    }

    public void setBlue2FrontMinion(Minion minion) {
        blue2FrontMinion = minion;
    }

    public void setBlue1FrontMinion(Minion minion) {
        blue1FrontMinion = minion;
    }

    public void setBlue1BackMinion(Minion minion) {
        blue1BackMinion = minion;
    }

    public void setGreen2BackMinion(Minion minion) {
        green2BackMinion = minion;
    }

    public void setGreen2FrontMinion(Minion minion) {
        green2FrontMinion = minion;
    }

    public void setGreen1FrontMinion(Minion minion) {
        green1FrontMinion = minion;
    }

    public void setGreen1BackMinion(Minion minion) {
        green1BackMinion = minion;
    }
}
