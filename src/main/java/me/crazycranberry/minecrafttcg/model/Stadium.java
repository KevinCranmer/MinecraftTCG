package me.crazycranberry.minecrafttcg.model;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionWithStaticEffect;
import me.crazycranberry.minecrafttcg.events.CombatEndEvent;
import me.crazycranberry.minecrafttcg.events.DuelEndEvent;
import me.crazycranberry.minecrafttcg.events.FirstPreCombatPhaseStartedEvent;
import me.crazycranberry.minecrafttcg.managers.StadiumManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.carddefinitions.Card.IS_CARD_KEY;
import static me.crazycranberry.minecrafttcg.managers.StadiumManager.PLAYER_1_SIGN_OFFSET;
import static me.crazycranberry.minecrafttcg.managers.StadiumManager.PLAYER_2_SIGN_OFFSET;
import static me.crazycranberry.minecrafttcg.managers.StadiumManager.updateManaLampsForPlayer;
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
    private boolean player1DoneMulliganing = false;
    private boolean player2DoneMulliganing = false;
    private int player1Mana = 0;
    private int player1MaxMana = 0;
    private int player2Mana = 0;
    private int player2MaxMana = 0;
    private int player1PendingDamage = 0;
    private int player1PendingHeal = 0;
    private int player1PendingDraws = 0;
    private int player1PendingManaReplenish = 0;
    private int player2PendingDamage = 0;
    private int player2PendingHeal = 0;
    private int player2PendingDraws = 0;
    private int player2PendingManaReplenish = 0;
    private final Deck player1Deck;
    private final Deck player2Deck;
    private int player1MillDamage = 1;
    private int player2MillDamage = 1;
    public int turn = 0; //TODO: MAKE NOT PUBLIC
    private boolean cardAnimationInProgress = false;
    private Event phaseEventToCallAfterAnimationFinishes = null;
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
            addMaxMana(player1, 1);
            addMaxMana(player2, 1);
            player1Mana = player1MaxMana;
            player2Mana = player2MaxMana;
            StadiumManager.updateManaLamps(this);
        } else if (phase != null && phase.ordinal() + 1 != turnPhase.ordinal()) {
            System.out.println("What the hell, the turn phases are out of order!!!");
            System.out.println("We tried going from " + phase + " to " + turnPhase);
        }
        phase = turnPhase;

        // In the event that nothing is on the board able to attack, we should just skip combat
        if (phase.equals(COMBAT_PHASE)) {
            doneAttacking();
        } else if (phase.equals(POST_COMBAT_CLEANUP)) {
            postCombatCleanUp();
        }
        phaseEventToCallAfterAnimationFinishes = null;
    }

    private void postCombatCleanUp() {
        // Is it a tie?
        if (player1.getHealth() <= player1PendingDamage && player2.getHealth() <= player2PendingDamage) {
            Bukkit.getPluginManager().callEvent(new DuelEndEvent(player1, true));
        }
        if (player1PendingDamage - player1PendingHeal > 0) {
            player1.damage(0); // In case it would kill the player, need to let the heal hit first
        }
        player1.setHealth(Math.max(Math.min(player1.getHealth() + player1PendingHeal - player1PendingDamage, player1.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()), 0));
        for (int i = 0; i < player1PendingDraws; i++) {
            draw(player1);
        }
        if (player1PendingManaReplenish > 0) {
            player1Mana = Math.min(player1Mana + player1PendingManaReplenish, player1MaxMana);
            player1PendingManaReplenish = 0;
            updateManaLampsForPlayer(this, player1);
        }
        player1PendingDamage = 0;
        player1PendingHeal = 0;
        player1PendingDraws = 0;
        if (player2PendingDamage - player2PendingHeal > 0) {
            player2.damage(0); // In case it would kill the player, need to let the heal hit first
        }
        player2.setHealth(Math.max(Math.min(player2.getHealth() + player2PendingHeal - player2PendingDamage, player2.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()), 0));
        for (int i = 0; i < player2PendingDraws; i++) {
            draw(player2);
        }
        if (player2PendingManaReplenish > 0) {
            player2Mana = Math.min(player2Mana + player2PendingManaReplenish, player2MaxMana);
            player2PendingManaReplenish = 0;
            updateManaLampsForPlayer(this, player2);
        }
        player2PendingDamage = 0;
        player2PendingHeal = 0;
        player2PendingDraws = 0;
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
            DARK_GREEN, minion.hasFlying() ? "☁" : minion.cardDef().isRanged() ? "\uD83C\uDFF9" : "🗡", RESET, minion.strength(), RED, RESET, minion.health(), minion.maxHealth()
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
        if (spot.minionRef() != null && spot.minionRef().apply(this) != null && !getPlugin().config().duelShowAllMinionNames()) {
            spot.minionRef().apply(this).minionInfo().entity().setCustomNameVisible(false);
        }
    }

    public int playerMaxMana(Player p) {
        if (p.equals(player1)) {
            return player1MaxMana;
        } else {
            return player2MaxMana;
        }
    }

    public void addMaxMana(Player p, int maxMana) {
        if (p.equals(player1)) {
            player1MaxMana = Math.min(player1MaxMana + maxMana, 10);
        } else {
            player2MaxMana = Math.min(player2MaxMana + maxMana, 10);
        }
    }

    public void addMana(Player p, int bonusMana) {
        if (p.equals(player1)) {
            player1Mana += bonusMana;
        } else {
            player2Mana += bonusMana;
        }
    }

    public void pendingDamageForPlayer(Player p, Integer damage) {
        if (p.equals(player1)) {
            player1PendingDamage += damage;
        } else {
            player2PendingDamage += damage;
        }
    }

    public void pendingHealForPlayer(Player p, Integer damage) {
        if (p.equals(player1)) {
            player1PendingHeal += damage;
        } else {
            player2PendingHeal += damage;
        }
    }

    public void pendingDrawsForPlayer(Player p, Integer damage) {
        if (p.equals(player1)) {
            player1PendingDraws += damage;
        } else {
            player2PendingDraws += damage;
        }
    }

    public void pendingManaReplenishForPlayer(Player p, Integer damage) {
        if (p.equals(player1)) {
            player1PendingManaReplenish += damage;
        } else {
            player2PendingManaReplenish += damage;
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

    public List<Minion> allMinions() {
        List<Minion> minions = new ArrayList<>();
        for (Spot spot : List.of(RED_1_FRONT, RED_1_BACK, BLUE_1_FRONT, BLUE_1_BACK, GREEN_1_FRONT, GREEN_1_BACK, RED_2_FRONT, RED_2_BACK, BLUE_2_FRONT, BLUE_2_BACK, GREEN_2_FRONT, GREEN_2_BACK)) {
            Minion m = spot.minionRef().apply(this);
            if (m != null) {
                minions.add(m);
            }
        }
        return minions;
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
        return getTargetInFront(minion, minion.hasFlying());
    }

    public LivingEntity getTargetInFront(Minion minion, boolean canTargetFlying) {
        Minion opposingMinion = Spot.opposingFrontRankSpot(minion.minionInfo().spot()).minionRef().apply(this);
        if (opposingMinion == null || (opposingMinion.hasFlying() && !canTargetFlying)) {
            opposingMinion = Spot.opposingBackRankSpot(minion.minionInfo().spot()).minionRef().apply(this);
            if (opposingMinion == null || (opposingMinion.hasFlying() && !canTargetFlying)) {
                return Spot.opposingChicken(minion.minionInfo().spot(), this);
            }
        }
        return opposingMinion.minionInfo().entity();
    }

    public Optional<Minion> minionFromEntity(LivingEntity entity) {
        for (Spot spot : Spot.values()) {
            if (spot.minionRef() != null) {
                Minion minion = spot.minionRef().apply(this);
                if (minion != null && minion.minionInfo().entity().equals(entity)) {
                    return Optional.of(minion);
                }
            }
        }
        return Optional.empty();
    }

    public Optional<LivingEntity> livingEntityFromSpot(Spot spot) {
        if (spot.equals(PLAYER_1_OUTLOOK)) {
            return Optional.of(player1);
        } else if (spot.equals(PLAYER_2_OUTLOOK)) {
            return Optional.of(player2);
        } else {
            return Optional.ofNullable(spot.minionRef().apply(this)).map(m -> m.minionInfo().entity());
        }
    }

    public void playerFinishedMulliganing(Player p, Inventory mulliganInv) {
        if (player1DoneMulliganing && player2DoneMulliganing) {
            return;
        }
        if (p.equals(player1)) {
            player1DoneMulliganing = true;
            List<ItemStack> mulliganedCards = Arrays.stream(mulliganInv.getContents())
                .filter(i -> Optional.ofNullable(i).map(ItemStack::getItemMeta).map(ItemMeta::getPersistentDataContainer).map(pdc -> pdc.has(IS_CARD_KEY)).orElse(false))
                .toList();
            for (int i = 0; i < mulliganedCards.size(); i++) {
                draw(player1);
            }
            mulliganedCards.forEach(i -> deck(player1).addCard(i));
        } else {
            player2DoneMulliganing = true;
            List<ItemStack> mulliganedCards = Arrays.stream(mulliganInv.getContents())
                .filter(i -> Optional.ofNullable(i).map(ItemStack::getItemMeta).map(ItemMeta::getPersistentDataContainer).map(pdc -> pdc.has(IS_CARD_KEY)).orElse(false))
                .toList();
            for (int i = 0; i < mulliganedCards.size(); i++) {
                draw(player2);
            }
            mulliganedCards.forEach(i -> deck(player2).addCard(i));
        }
        if (player1DoneMulliganing && player2DoneMulliganing) {
            Bukkit.getPluginManager().callEvent(new FirstPreCombatPhaseStartedEvent(this));
        }
    }

    public boolean isCardAnimationInProgress() {
        return cardAnimationInProgress;
    }

    public void cardAnimationStarted() {
        cardAnimationInProgress = true;
    }

    public void cardAnimationFinished() {
        cardAnimationInProgress = false;
        if (phaseEventToCallAfterAnimationFinishes != null) {
            Bukkit.getPluginManager().callEvent(phaseEventToCallAfterAnimationFinishes);
        }
    }

    public void callThisPhaseEvent(Event phaseEvent) {
        if (cardAnimationInProgress) {
            phaseEventToCallAfterAnimationFinishes = phaseEvent;
        } else {
            Bukkit.getPluginManager().callEvent(phaseEvent);
        }
    }

    public boolean isDuelDone() {
        return duelDone;
    }

    public void duelEnded(Player winner, Boolean isTie) {
        Bukkit.getScheduler().cancelTask(currentPlayerTurnParticlesTaskId);
        duelDone = true;
        killAllMinions();
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

    private void killAllMinions() {
        allyMinionSpots(player1).stream()
            .map(Spot::minionRef)
            .map(mr -> mr.apply(this))
            .filter(Objects::nonNull)
            .forEach(Minion::onDeath);
        allyMinionSpots(player2).stream()
            .map(Spot::minionRef)
            .map(mr -> mr.apply(this))
            .filter(Objects::nonNull)
            .forEach(Minion::onDeath);
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
        sign1.getSide(Side.FRONT).setLine(1, String.format("%s%s%s:%s %s❤%s:%s/%s\n", DARK_GREEN, minion.hasFlying() ? "☁" : minion.cardDef().isRanged() ? "\uD83C\uDFF9" : "🗡", RESET, minion.strength(), RED, RESET, minion.health(), minion.maxHealth()));
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

    public Minion getAllyMinionBehind(Spot spot) {
        return switch (spot) {
            case RED_2_FRONT -> RED_2_BACK.minionRef().apply(this);
            case RED_1_FRONT -> RED_1_BACK.minionRef().apply(this);
            case BLUE_2_FRONT -> BLUE_2_BACK.minionRef().apply(this);
            case BLUE_1_FRONT -> BLUE_1_BACK.minionRef().apply(this);
            case GREEN_2_FRONT -> GREEN_2_BACK.minionRef().apply(this);
            case GREEN_1_FRONT -> GREEN_1_BACK.minionRef().apply(this);
            default -> null;
        };
    }

    public Minion getAllyMinionInFront(Spot spot) {
        Spot s = getSpotInFront(spot);
        if (s != null) {
            return s.minionRef().apply(this);
        }
        return null;
    }

    public Spot getSpotInFront(Spot spot) {
        return switch (spot) {
            case RED_2_BACK -> RED_2_FRONT;
            case RED_1_BACK -> RED_1_FRONT;
            case BLUE_2_BACK -> BLUE_2_FRONT;
            case BLUE_1_BACK -> BLUE_1_FRONT;
            case GREEN_2_BACK -> GREEN_2_FRONT;
            case GREEN_1_BACK -> GREEN_1_FRONT;
            default -> null;
        };
    }

    public LivingEntity getEntityBehind(Spot spot) {
        return switch (spot) {
            case RED_2_BACK -> player2RedChicken;
            case RED_2_FRONT -> Optional.ofNullable(RED_2_BACK.minionRef().apply(this)).map(m -> m.minionInfo().entity()).orElse(player2RedChicken);
            case RED_1_BACK -> player1RedChicken;
            case RED_1_FRONT -> Optional.ofNullable(RED_1_BACK.minionRef().apply(this)).map(m -> m.minionInfo().entity()).orElse(player1RedChicken);
            case BLUE_2_BACK -> player2BlueChicken;
            case BLUE_2_FRONT -> Optional.ofNullable(BLUE_2_BACK.minionRef().apply(this)).map(m -> m.minionInfo().entity()).orElse(player2BlueChicken);
            case BLUE_1_BACK -> player1BlueChicken;
            case BLUE_1_FRONT -> Optional.ofNullable(BLUE_1_BACK.minionRef().apply(this)).map(m -> m.minionInfo().entity()).orElse(player1BlueChicken);
            case GREEN_2_BACK -> player2GreenChicken;
            case GREEN_2_FRONT -> Optional.ofNullable(GREEN_2_BACK.minionRef().apply(this)).map(m -> m.minionInfo().entity()).orElse(player2GreenChicken);
            case GREEN_1_BACK -> player1GreenChicken;
            case GREEN_1_FRONT -> Optional.ofNullable(GREEN_1_BACK.minionRef().apply(this)).map(m -> m.minionInfo().entity()).orElse(player1GreenChicken);
            default -> null;
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
        spot.minionSetRef().accept(this, null, false); // The minions themselves cancel tasks when they die
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

    public Player opponent(Player p) {
        return p.equals(player1) ? player2 : player1;
    }

    public void setRed2BackMinion(Minion minion, boolean cancelStaticTasks) {
        if (cancelStaticTasks && red2BackMinion instanceof MinionWithStaticEffect staticMinion) {
            staticMinion.cancelTask();
        }
        red2BackMinion = minion;
    }

    public void setRed2FrontMinion(Minion minion, boolean cancelStaticTasks) {
        if (cancelStaticTasks && red2FrontMinion instanceof MinionWithStaticEffect staticMinion) {
            staticMinion.cancelTask();
        }
        red2FrontMinion = minion;
    }

    public void setRed1FrontMinion(Minion minion, boolean cancelStaticTasks) {
        if (cancelStaticTasks && red1FrontMinion instanceof MinionWithStaticEffect staticMinion) {
            staticMinion.cancelTask();
        }
        red1FrontMinion = minion;
    }

    public void setRed1BackMinion(Minion minion, boolean cancelStaticTasks) {
        if (cancelStaticTasks && red1BackMinion instanceof MinionWithStaticEffect staticMinion) {
            staticMinion.cancelTask();
        }
        red1BackMinion = minion;
    }

    public void setBlue2BackMinion(Minion minion, boolean cancelStaticTasks) {
        if (cancelStaticTasks && blue2BackMinion instanceof MinionWithStaticEffect staticMinion) {
            staticMinion.cancelTask();
        }
        blue2BackMinion = minion;
    }

    public void setBlue2FrontMinion(Minion minion, boolean cancelStaticTasks) {
        if (cancelStaticTasks && blue2FrontMinion instanceof MinionWithStaticEffect staticMinion) {
            staticMinion.cancelTask();
        }
        blue2FrontMinion = minion;
    }

    public void setBlue1FrontMinion(Minion minion, boolean cancelStaticTasks) {
        if (cancelStaticTasks && blue1FrontMinion instanceof MinionWithStaticEffect staticMinion) {
            staticMinion.cancelTask();
        }
        blue1FrontMinion = minion;
    }

    public void setBlue1BackMinion(Minion minion, boolean cancelStaticTasks) {
        if (cancelStaticTasks && blue1BackMinion instanceof MinionWithStaticEffect staticMinion) {
            staticMinion.cancelTask();
        }
        blue1BackMinion = minion;
    }

    public void setGreen2BackMinion(Minion minion, boolean cancelStaticTasks) {
        if (cancelStaticTasks && green2BackMinion instanceof MinionWithStaticEffect staticMinion) {
            staticMinion.cancelTask();
        }
        green2BackMinion = minion;
    }

    public void setGreen2FrontMinion(Minion minion, boolean cancelStaticTasks) {
        if (cancelStaticTasks && green2FrontMinion instanceof MinionWithStaticEffect staticMinion) {
            staticMinion.cancelTask();
        }
        green2FrontMinion = minion;
    }

    public void setGreen1FrontMinion(Minion minion, boolean cancelStaticTasks) {
        if (cancelStaticTasks && green1FrontMinion instanceof MinionWithStaticEffect staticMinion) {
            staticMinion.cancelTask();
        }
        green1FrontMinion = minion;
    }

    public void setGreen1BackMinion(Minion minion, boolean cancelStaticTasks) {
        if (cancelStaticTasks && green1BackMinion instanceof MinionWithStaticEffect staticMinion) {
            staticMinion.cancelTask();
        }
        green1BackMinion = minion;
    }
}
