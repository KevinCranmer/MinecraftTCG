package me.crazycranberry.minecrafttcg.managers;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.events.CombatEndEvent;
import me.crazycranberry.minecrafttcg.events.CombatStartAttackingEvent;
import me.crazycranberry.minecrafttcg.events.CombatStartEvent;
import me.crazycranberry.minecrafttcg.events.DuelStartEvent;
import me.crazycranberry.minecrafttcg.events.FirstPostCombatPhaseStartedEvent;
import me.crazycranberry.minecrafttcg.events.FirstPreCombatPhaseStartedEvent;
import me.crazycranberry.minecrafttcg.events.SecondPostCombatPhaseStartedEvent;
import me.crazycranberry.minecrafttcg.events.SecondPreCombatPhaseStartedEvent;
import me.crazycranberry.minecrafttcg.events.TurnEndEvent;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.model.TurnPhase.COMBAT_PHASE;
import static me.crazycranberry.minecrafttcg.model.TurnPhase.FIRST_POSTCOMBAT_PHASE;
import static me.crazycranberry.minecrafttcg.model.TurnPhase.FIRST_PRECOMBAT_PHASE;
import static me.crazycranberry.minecrafttcg.model.TurnPhase.POST_COMBAT_CLEANUP;
import static me.crazycranberry.minecrafttcg.model.TurnPhase.SECOND_POSTCOMBAT_PHASE;
import static me.crazycranberry.minecrafttcg.model.TurnPhase.SECOND_PRECOMBAT_PHASE;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

public class TurnManager implements Listener {
    public static final int TITLE_DURATION = 80; // In ticks

    @EventHandler
    private void onDuelStart(DuelStartEvent event) {
        sendTitles(
                String.format("%sDuel Started", AQUA), "You are Player 1",
                String.format("%sDuel Started", AQUA), "You are Player 2",
                event.getStadium());
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> Bukkit.getPluginManager().callEvent(new FirstPreCombatPhaseStartedEvent(event.getStadium())), TITLE_DURATION);
    }

    @EventHandler
    private void onFirstPreCombatPhaseStarted(FirstPreCombatPhaseStartedEvent event) {
        System.out.println("onFirstPreCombatPhaseStarted");
        event.getStadium().updatePhase(FIRST_PRECOMBAT_PHASE);
        int turn = event.getStadium().turn();
        executeForAllMinions(event.getStadium(), Minion::onTurnStart);
        String title = turn % 2 != 0 ? String.format("%s%s's Pre-Combat Phase", GREEN, event.getStadium().player1().getName()) : String.format("%s%s's Pre-Combat Phase", GOLD, event.getStadium().player2().getName());
        sendTitles(title, "Turn " + turn, event.getStadium());
    }

    @EventHandler
    private void onSecondPreCombatPhaseStarted(SecondPreCombatPhaseStartedEvent event) {
        System.out.println("onSecondPreCombatPhaseStarted");
        event.getStadium().updatePhase(SECOND_PRECOMBAT_PHASE);
        int turn = event.getStadium().turn();
        String title = turn % 2 != 1 ? String.format("%s%s's Pre-Combat Phase", GREEN, event.getStadium().player1().getName()) : String.format("%s%s's Pre-Combat Phase", GOLD, event.getStadium().player2().getName());
        sendTitles(title, "Turn " + turn, event.getStadium());
    }

    @EventHandler
    private void onCombatStart(CombatStartEvent event) {
        System.out.println("onCombatStart");
        event.getStadium().updatePhase(COMBAT_PHASE);
        sendTitles(String.format("%sCombat Phase", RED), event.getStadium());
        executeForAllMinions(event.getStadium(), Minion::onCombatStart);
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> Bukkit.getPluginManager().callEvent(new CombatStartAttackingEvent(event.getStadium())), 40);
    }

    @EventHandler
    private void onCombatStartAttacking(CombatStartAttackingEvent event) {
        System.out.println("onCombatStartAttacking");
        //TODO: Start attacking mofos
        executeForMinionsThatCanAttack(event.getStadium(), Minion::attackInFront);
    }
    @EventHandler
    private void onCombatEnd(CombatEndEvent event) {
        System.out.println("onCombatStartAttacking");
        event.getStadium().updatePhase(POST_COMBAT_CLEANUP);
        //TODO: Kill any minions with 0 health and trigger their onDeaths
        executeForAllMinions(event.getStadium(), Minion::onCombatEnd);
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> Bukkit.getPluginManager().callEvent(new FirstPostCombatPhaseStartedEvent(event.getStadium())), TITLE_DURATION);
    }

    @EventHandler
    private void onFirstPostCombatPhaseStarted(FirstPostCombatPhaseStartedEvent event) {
        System.out.println("onFirstPostCombatPhaseStarted");
        event.getStadium().updatePhase(FIRST_POSTCOMBAT_PHASE);
        int turn = event.getStadium().turn();
        String title = turn % 2 != 1 ? String.format("%s%s's Post-Combat Phase", GREEN, event.getStadium().player1().getName()) : String.format("%s%s's Post-Combat Phase", GOLD, event.getStadium().player2().getName());
        sendTitles(title, "Turn " + turn, event.getStadium());
    }

    @EventHandler
    private void onSecondPostCombatPhaseStarted(SecondPostCombatPhaseStartedEvent event) {
        System.out.println("onSecondPostCombatPhaseStarted");
        event.getStadium().updatePhase(SECOND_POSTCOMBAT_PHASE);
        int turn = event.getStadium().turn();
        String title = turn % 2 != 0 ? String.format("%s%s's Post-Combat Phase", GREEN, event.getStadium().player1().getName()) : String.format("%s%s's Post-Combat Phase", GOLD, event.getStadium().player2().getName());
        sendTitles(title, "Turn " + turn, event.getStadium());
    }

    @EventHandler
    private void onTurnEnd(TurnEndEvent event) {
        System.out.println("onTurnEnd");
        sendTitles(String.format("%sEnd of Turn %s", AQUA, event.getStadium().turn()), event.getStadium());
        executeForAllMinions(event.getStadium(), Minion::onTurnEnd);
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> Bukkit.getPluginManager().callEvent(new FirstPreCombatPhaseStartedEvent(event.getStadium())), TITLE_DURATION);
    }

    private void executeForAllMinions(Stadium stadium, Consumer<? super Minion> trigger) {
        Arrays.stream(Spot.values())
                .map(Spot::minionRef)
                .filter(Objects::nonNull)
                .map(mr -> mr.apply(stadium))
                .filter(Objects::nonNull)
                .forEach(trigger);
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
