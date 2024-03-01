package me.crazycranberry.minecrafttcg.managers;

import me.crazycranberry.minecrafttcg.events.CombatEndEvent;
import me.crazycranberry.minecrafttcg.events.DuelStartEvent;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.scoreboard.Objective;

public class ScoreboardManager implements Listener {
    @EventHandler
    private void onDuelStart(DuelStartEvent event) {
        Player p1 = event.getStadium().player1();
        Player p2 = event.getStadium().player2();
        updateObjective(p1, p2);
        updateObjective(p2, p1);
    }

    @EventHandler
    private void onPlayerTakeDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player p) {
            Stadium stadium = StadiumManager.stadium(p.getLocation());
            if (stadium != null) {
                updateObjective(p.equals(stadium.player1()) ? stadium.player2() : stadium.player1(), p, -event.getDamage());
            }
        }
    }

    @EventHandler
    private void onPlayerRegainHealth(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player p) {
            Stadium stadium = StadiumManager.stadium(p.getLocation());
            if (stadium != null) {
                updateObjective(p.equals(stadium.player1()) ? stadium.player2() : stadium.player1(), p, event.getAmount());
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    private void onCombatEnd(CombatEndEvent event) {
        updateObjective(event.getStadium().player1(), event.getStadium().player2());
        updateObjective(event.getStadium().player2(), event.getStadium().player1());
    }


    private void updateObjective(Player scoreboardOwner, Player scoreboardEnemy) {
        updateObjective(scoreboardOwner, scoreboardEnemy, 0);
    }


    private void updateObjective(Player scoreboardOwner, Player scoreboardEnemy, double healthChange) {
        Objective o = scoreboardOwner.getScoreboard().getObjective(scoreboardOwner.getName() + "senemyhealth");
        o.getScore(scoreboardEnemy.getName()).setScore((int) (scoreboardEnemy.getHealth() + healthChange));
    }
}
