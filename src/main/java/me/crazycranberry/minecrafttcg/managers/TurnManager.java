package me.crazycranberry.minecrafttcg.managers;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.events.CombatEndEvent;
import me.crazycranberry.minecrafttcg.events.CombatStartAttackingEvent;
import me.crazycranberry.minecrafttcg.events.CombatStartEvent;
import me.crazycranberry.minecrafttcg.events.DuelCloseEvent;
import me.crazycranberry.minecrafttcg.events.DuelEndEvent;
import me.crazycranberry.minecrafttcg.events.DuelStartEvent;
import me.crazycranberry.minecrafttcg.events.FirstPostCombatPhaseStartedEvent;
import me.crazycranberry.minecrafttcg.events.FirstPreCombatPhaseStartedEvent;
import me.crazycranberry.minecrafttcg.events.SecondPostCombatPhaseStartedEvent;
import me.crazycranberry.minecrafttcg.events.SecondPreCombatPhaseStartedEvent;
import me.crazycranberry.minecrafttcg.events.TurnEndEvent;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import me.crazycranberry.minecrafttcg.model.TurnPhase;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

import static me.crazycranberry.minecrafttcg.CommonFunctions.TICKS_PER_SECOND;
import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.MinecraftTCG.logger;
import static me.crazycranberry.minecrafttcg.carddefinitions.Card.CARD_COST_KEY;
import static me.crazycranberry.minecrafttcg.commands.RanksCommand.MAX_RANK_GAIN_PER_MATCH;
import static me.crazycranberry.minecrafttcg.commands.RanksCommand.MAX_RANK_GAIN_PER_TIE;
import static me.crazycranberry.minecrafttcg.commands.RanksCommand.MIN_RANK_GAIN_PER_MATCH;
import static me.crazycranberry.minecrafttcg.commands.RanksCommand.MIN_RANK_GAIN_PER_TIE;
import static me.crazycranberry.minecrafttcg.commands.RanksCommand.UPPER_LIMIT_OF_RANK_DIFFERENTIAL_WEIGHTING;
import static me.crazycranberry.minecrafttcg.model.TurnPhase.COMBAT_PHASE;
import static me.crazycranberry.minecrafttcg.model.TurnPhase.FIRST_POSTCOMBAT_PHASE;
import static me.crazycranberry.minecrafttcg.model.TurnPhase.FIRST_PRECOMBAT_PHASE;
import static me.crazycranberry.minecrafttcg.model.TurnPhase.POST_COMBAT_CLEANUP;
import static me.crazycranberry.minecrafttcg.model.TurnPhase.SECOND_POSTCOMBAT_PHASE;
import static me.crazycranberry.minecrafttcg.model.TurnPhase.SECOND_PRECOMBAT_PHASE;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.RESET;

public class TurnManager implements Listener {
    public static final int TITLE_DURATION = 80; // In ticks
    private static final int numCardsToStart = 2;

    @EventHandler
    private void onDuelStart(DuelStartEvent event) {
        sendTitles(
                String.format("%sDuel Started", AQUA), "You are Player 1",
                String.format("%sDuel Started", AQUA), "You are Player 2",
                event.getStadium());
        for (int i = 0; i < numCardsToStart; i++) {
            event.getStadium().draw(event.getStadium().player1());
            event.getStadium().draw(event.getStadium().player2());
        }
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> Bukkit.getPluginManager().callEvent(new FirstPreCombatPhaseStartedEvent(event.getStadium())), TITLE_DURATION);
    }

    @EventHandler
    private void onFirstPreCombatPhaseStarted(FirstPreCombatPhaseStartedEvent event) {
        if (event.getStadium().isDuelDone()) {
            return;
        }
        event.getStadium().updatePhase(FIRST_PRECOMBAT_PHASE);
        int turn = event.getStadium().turn();
        Player p = event.getStadium().currentPlayersTurn();
        makeSoundForPlayerTurnStart(p);
        event.getStadium().draw(event.getStadium().player1());
        event.getStadium().draw(event.getStadium().player2());
        executeForAllMinions(event.getStadium(), Minion::onTurnStart);
        String title = String.format("%s%s's Pre-Combat Phase", event.getStadium().playersColor(p), p.getName());
        sendTitles(title, "Turn " + turn, event.getStadium());
        startTurnPhaseTimers(turn, FIRST_PRECOMBAT_PHASE, event.getStadium());
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> maybeAutoSkipPhase(event.getStadium(), turn, FIRST_PRECOMBAT_PHASE), 40);
    }

    @EventHandler
    private void onSecondPreCombatPhaseStarted(SecondPreCombatPhaseStartedEvent event) {
        if (event.getStadium().isDuelDone()) {
            return;
        }
        event.getStadium().updatePhase(SECOND_PRECOMBAT_PHASE);
        int turn = event.getStadium().turn();
        Player p = event.getStadium().currentPlayersTurn();
        makeSoundForPlayerTurnStart(p);
        String title = String.format("%s%s's Pre-Combat Phase", event.getStadium().playersColor(p), p.getName());
        sendTitles(title, "Turn " + turn, event.getStadium());
        startTurnPhaseTimers(turn, SECOND_PRECOMBAT_PHASE, event.getStadium());
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> maybeAutoSkipPhase(event.getStadium(), turn, SECOND_PRECOMBAT_PHASE), 40);
    }

    @EventHandler
    private void onCombatStart(CombatStartEvent event) {
        if (event.getStadium().isDuelDone()) {
            return;
        }
        event.getStadium().updatePhase(COMBAT_PHASE);
        sendTitles(String.format("%sCombat Phase", RED), event.getStadium());
        executeForAllMinions(event.getStadium(), Minion::onCombatStart);
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> Bukkit.getPluginManager().callEvent(new CombatStartAttackingEvent(event.getStadium())), 40);
    }

    @EventHandler
    private void onCombatStartAttacking(CombatStartAttackingEvent event) {
        if (event.getStadium().isDuelDone()) {
            return;
        }
        executeForMinionsThatCanAttack(event.getStadium(), Minion::attackInFront);
        startMinionUnstuckTimers(event.getStadium().turn(), COMBAT_PHASE, event.getStadium());
        startTurnPhaseTimers(event.getStadium().turn(), COMBAT_PHASE, event.getStadium());
    }

    @EventHandler
    private void onCombatEnd(CombatEndEvent event) {
        if (event.getStadium().isDuelDone()) {
            return;
        }
        event.getStadium().updatePhase(POST_COMBAT_CLEANUP);
        executeForAllMinions(event.getStadium(), Minion::onCombatEnd);
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> Bukkit.getPluginManager().callEvent(new FirstPostCombatPhaseStartedEvent(event.getStadium())), TITLE_DURATION);
    }

    @EventHandler
    private void onFirstPostCombatPhaseStarted(FirstPostCombatPhaseStartedEvent event) {
        if (event.getStadium().isDuelDone()) {
            return;
        }
        event.getStadium().updatePhase(FIRST_POSTCOMBAT_PHASE);
        int turn = event.getStadium().turn();
        Player p = event.getStadium().currentPlayersTurn();
        makeSoundForPlayerTurnStart(p);
        String title = String.format("%s%s's Post-Combat Phase", event.getStadium().playersColor(p), p.getName());
        sendTitles(title, "Turn " + turn, event.getStadium());
        startTurnPhaseTimers(turn, FIRST_POSTCOMBAT_PHASE, event.getStadium());
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> maybeAutoSkipPhase(event.getStadium(), turn, FIRST_POSTCOMBAT_PHASE), 40);
    }

    @EventHandler
    private void onSecondPostCombatPhaseStarted(SecondPostCombatPhaseStartedEvent event) {
        if (event.getStadium().isDuelDone()) {
            return;
        }
        event.getStadium().updatePhase(SECOND_POSTCOMBAT_PHASE);
        int turn = event.getStadium().turn();
        Player p = event.getStadium().currentPlayersTurn();
        makeSoundForPlayerTurnStart(p);
        String title = String.format("%s%s's Post-Combat Phase", event.getStadium().playersColor(p), p.getName());
        sendTitles(title, "Turn " + turn, event.getStadium());
        startTurnPhaseTimers(turn, SECOND_POSTCOMBAT_PHASE, event.getStadium());
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> maybeAutoSkipPhase(event.getStadium(), turn, SECOND_POSTCOMBAT_PHASE), 40);
    }

    @EventHandler
    private void onTurnEnd(TurnEndEvent event) {
        if (event.getStadium().isDuelDone()) {
            return;
        }
        sendTitles(String.format("%sEnd of Turn %s", AQUA, event.getStadium().turn()), event.getStadium());
        executeForAllMinions(event.getStadium(), Minion::onTurnEnd);
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> Bukkit.getPluginManager().callEvent(new FirstPreCombatPhaseStartedEvent(event.getStadium())), TITLE_DURATION);
    }

    @EventHandler
    private void onDuelEnd(DuelEndEvent event) throws IOException {
        Integer winnerRank = getPlugin().config().playerRank(event.winner());
        Integer loserRank = getPlugin().config().playerRank(event.loser());
        Integer higherRank = winnerRank > loserRank ? winnerRank : loserRank;
        Integer lowerRank = winnerRank > loserRank ? loserRank : winnerRank;
        Integer rankDifferential = higherRank - lowerRank;
        double proportionalDiff = ((double) Math.min(rankDifferential, UPPER_LIMIT_OF_RANK_DIFFERENTIAL_WEIGHTING)) / ((double) UPPER_LIMIT_OF_RANK_DIFFERENTIAL_WEIGHTING);
        Integer rankChange;
        Player playerGainingRank;
        Player playerLosingRank;
        if (event.isTie()) {
            sendTitles(String.format("%sIt's a tie!%s", RED, RESET), event.stadium());
            rankChange = (int) ((proportionalDiff * (MAX_RANK_GAIN_PER_TIE - MIN_RANK_GAIN_PER_TIE)) + MIN_RANK_GAIN_PER_TIE);
            playerGainingRank = winnerRank > loserRank ? event.winner() : event.loser();
            playerLosingRank = winnerRank > loserRank ? event.loser() : event.winner();
        } else {
            sendTitles(String.format("%s%s is the winner!%s", event.winner().equals(event.stadium().player1()) ? GREEN : GOLD, event.winner().getName(), RESET), event.stadium());
            playerGainingRank = event.winner();
            playerLosingRank = event.loser();
            if (winnerRank > loserRank) {
                rankChange = (int) (((1 - proportionalDiff) * (((MAX_RANK_GAIN_PER_MATCH + MIN_RANK_GAIN_PER_MATCH) / 2) - MIN_RANK_GAIN_PER_MATCH)) + MIN_RANK_GAIN_PER_MATCH);
            } else {
                rankChange = (int) ((proportionalDiff * (MAX_RANK_GAIN_PER_MATCH - ((MAX_RANK_GAIN_PER_MATCH + MIN_RANK_GAIN_PER_MATCH) / 2))) + ((MAX_RANK_GAIN_PER_MATCH + MIN_RANK_GAIN_PER_MATCH) / 2));
            }
        }
        if (event.stadium().isRanked()) {
            playerGainingRank.sendMessage(String.format("%sYou've gained %s%s%s rank points for %s%s", GRAY, GREEN, rankChange, GRAY, event.isTie() ? "tying" : "winning", RESET));
            getPlugin().config().setPlayerRank(playerGainingRank, getPlugin().config().playerRank(playerGainingRank) + rankChange);
            playerLosingRank.sendMessage(String.format("%sYou've lost %s%s%s rank points for %s%s", GRAY, RED, rankChange, GRAY, event.isTie() ? "tying" : "losing", RESET));
            getPlugin().config().setPlayerRank(playerLosingRank, getPlugin().config().playerRank(playerLosingRank) - rankChange);
            getPlugin().config().playerRankChanged();
        }
        event.stadium().duelEnded(event.winner(), event.isTie());
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> Bukkit.getPluginManager().callEvent(new DuelCloseEvent(event.stadium())), event.stadium().fireworkDuration());
    }

    private void startMinionUnstuckTimers(int turn, TurnPhase turnPhase, Stadium stadium) {
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            if (turn == stadium.turn() && turnPhase.equals(stadium.phase())) {
                executeForAllMinions(stadium, Minion::unstuckify);
            }
        }, (long) (getPlugin().config().duelSecondsPerRound() * 0.75) * TICKS_PER_SECOND);
    }

    private void startTurnPhaseTimers(int turn, TurnPhase turnPhase, Stadium stadium) {
        startTurnPhaseTimer(turn, turnPhase, stadium, 10);
        startTurnPhaseTimer(turn, turnPhase, stadium, 5);
        startTurnPhaseTimer(turn, turnPhase, stadium, 4);
        startTurnPhaseTimer(turn, turnPhase, stadium, 3);
        startTurnPhaseTimer(turn, turnPhase, stadium, 2);
        startTurnPhaseTimer(turn, turnPhase, stadium, 1);
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            maybeNextPhase(turn, turnPhase, stadium);
        }, (long) getPlugin().config().duelSecondsPerRound() * TICKS_PER_SECOND);
    }

    private void startTurnPhaseTimer(int turn, TurnPhase turnPhase, Stadium stadium, int secondsLeft) {
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            if (turn == stadium.turn() && turnPhase.equals(stadium.phase()) && !stadium.isDuelDone()) {
                Player p = stadium.currentPlayersTurn();
                stadium.player1().sendMessage(String.format("%s%s turn will end in %s seconds.%s", GRAY, p == null ? "The current" : p.getName() + "'s", secondsLeft, RESET));
                stadium.player2().sendMessage(String.format("%s%s turn will end in %s seconds.%s", GRAY, p == null ? "The current" : p.getName() + "'s", secondsLeft, RESET));
            }
        }, (long) (getPlugin().config().duelSecondsPerRound() - secondsLeft) * TICKS_PER_SECOND);
    }

    private void makeSoundForPlayerTurnStart(Player player) {
        player.playSound(player, Sound.BLOCK_BEACON_POWER_SELECT, 1, 1);
    }

    private void executeForAllMinions(Stadium stadium, Consumer<? super Minion> trigger) {
        Arrays.stream(Spot.values())
                .map(Spot::minionRef)
                .filter(Objects::nonNull)
                .map(mr -> mr.apply(stadium))
                .filter(Objects::nonNull)
                .forEach(trigger);
    }

    private void maybeAutoSkipPhase(Stadium stadium, int turn, TurnPhase turnPhase) {
        if (!getPlugin().config().duelAutoSkipPhasesWithoutAvailableActions()) {
            return;
        }
        Player currentPlayer = stadium.currentPlayersTurn();
        int availableMana = stadium.playerMana(currentPlayer);
        boolean hasPlayableCards = false;
        for (ItemStack itemStack : currentPlayer.getInventory().getContents()) {
            if (itemStack != null) {
                ItemMeta meta = itemStack.getItemMeta();
                if (meta != null) {
                    Integer cardCost = meta.getPersistentDataContainer().get(CARD_COST_KEY, PersistentDataType.INTEGER);
                    if (cardCost != null && cardCost <= availableMana) {
                        hasPlayableCards = true;
                        break;
                    }
                }
            }
        }
        if (!hasPlayableCards) {
            maybeNextPhase(turn, turnPhase, stadium);
        }
    }

    private void maybeNextPhase(int turn, TurnPhase turnPhase, Stadium stadium) {
        if (turn == stadium.turn() && turnPhase.equals(stadium.phase()) && !stadium.isDuelDone()) {
            try {
                Constructor<? extends Event> c = turnPhase.nextPhaseRequestEventClass().getConstructor(Stadium.class);
                c.setAccessible(true);
                Event nextPhaseEvent = c.newInstance(stadium);
                Bukkit.getPluginManager().callEvent(nextPhaseEvent);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                logger().severe("Exception trying to create the next phases event: " + e.getMessage());
            }
        }
    }

    private void executeForMinionsThatCanAttack(Stadium stadium, Consumer<? super Minion> trigger) {
        Arrays.stream(Spot.values())
                .map(Spot::minionRef)
                .filter(Objects::nonNull)
                .map(mr -> mr.apply(stadium))
                .filter(Objects::nonNull)
                .filter(m -> m.attacksLeft() > 0)
                .filter(m -> !(!m.cardDef().isRanged() && stadium.hasAllyMinionInFront(m.minionInfo().spot())))
                .forEach(trigger);
    }


    private void sendTitles(String title, Stadium stadium) {
        sendTitles(title, "", title, "", stadium);
    }

    private void sendTitles(String title, String titleLine2, Stadium stadium) {
        sendTitles(title, titleLine2, title, titleLine2, stadium);
    }

    private void sendTitles(String title1, String title1Line2, String title2, String title2Line2, Stadium stadium) {
        Player player1 = stadium.player1();
        CraftPlayer craftPlayer1 = (CraftPlayer) player1;
        ServerPlayer serverPlayer1 = craftPlayer1.getHandle();
        ServerGamePacketListenerImpl playerConnection1 = serverPlayer1.connection;
        playerConnection1.send(new ClientboundSetTitleTextPacket(Component.literal(title1)));
        playerConnection1.send(new ClientboundSetSubtitleTextPacket(Component.literal(title1Line2)));
        playerConnection1.send(new ClientboundSetTitlesAnimationPacket(10, TITLE_DURATION - 30, 20));

        Player player2 = stadium.player2();
        CraftPlayer craftPlayer2 = (CraftPlayer) player2;
        ServerPlayer serverPlayer2 = craftPlayer2.getHandle();
        ServerGamePacketListenerImpl playerConnection2 = serverPlayer2.connection;
        playerConnection2.send(new ClientboundSetTitleTextPacket(Component.literal(title2)));
        playerConnection2.send(new ClientboundSetSubtitleTextPacket(Component.literal(title2Line2)));
        playerConnection2.send(new ClientboundSetTitlesAnimationPacket(10, TITLE_DURATION - 30, 20));
    }
}
